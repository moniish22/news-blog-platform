package com.newsblog.service;

import com.newsblog.config.MailConfig;
import com.newsblog.dto.ArticleDto;
import com.newsblog.model.*;
import com.newsblog.repository.*;
import com.newsblog.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.newsblog.util.ArticleStatus.*;

/**
 * Article CRUD plus the publishing workflow:
 * AUTHOR can save drafts and submit for review; EDITOR/ADMIN can publish, schedule, archive and feature.
 */
@Service
@RequiredArgsConstructor
public class ArticleService {
    public static final Sort NEWEST = Sort.by(Sort.Direction.DESC, "publishedAt");
    public static final Sort RECENTLY_CREATED = Sort.by(Sort.Direction.DESC, "createdAt");

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final ArticleAnalyticsRepository analyticsRepository;
    private final SavedArticleRepository savedArticleRepository;
    private final TagService tagService;
    private final NotificationService notificationService;
    private final MailConfig mailConfig;
    private final HtmlUtils htmlUtils;
    private final TextUtils textUtils;

    // ---------- reading ----------
    @Transactional(readOnly = true)
    public Page<Article> published(Pageable p) { return articleRepository.findByStatus(PUBLISHED, p); }

    @Transactional(readOnly = true)
    public Page<Article> byCategory(Category c, Pageable p) { return articleRepository.findByStatusAndCategory(PUBLISHED, c, p); }

    @Transactional(readOnly = true)
    public Page<Article> byTag(Tag t, Pageable p) { return articleRepository.findByTagAndStatus(t, PUBLISHED, p); }

    @Transactional(readOnly = true)
    public Page<Article> byMonth(YearMonth ym, Pageable p) {
        return articleRepository.findByStatusAndPublishedAtBetween(PUBLISHED,
                ym.atDay(1).atStartOfDay(), ym.plusMonths(1).atDay(1).atStartOfDay(), p);
    }

    @Transactional(readOnly = true)
    public Page<Article> byAuthor(User author, Pageable p) { return articleRepository.findByAuthorAndStatus(author, PUBLISHED, p); }

    @Transactional(readOnly = true)
    public List<Article> featured() { return articleRepository.findTop5ByStatusAndFeaturedTrueOrderByPublishedAtDesc(PUBLISHED); }

    @Transactional(readOnly = true)
    public List<Article> related(Article a) {
        return a.getCategory() == null ? List.of()
                : articleRepository.findTop4ByStatusAndCategoryAndIdNotOrderByPublishedAtDesc(PUBLISHED, a.getCategory(), a.getId());
    }

    @Transactional(readOnly = true)
    public Article getById(Long id) {
        return articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article not found"));
    }

    /** Published articles are public; drafts etc. are only visible to people who can manage them (preview). */
    @Transactional(readOnly = true)
    public Article getForViewer(String slug, User viewer) {
        Article a = articleRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Article not found"));
        if (a.getStatus() == PUBLISHED || (viewer != null && canManage(viewer, a))) return a;
        throw new ResourceNotFoundException("Article not found");
    }

    // ---------- permissions ----------
    public boolean isPrivileged(User u) { return u != null && (u.getRole() == Role.ADMIN || u.getRole() == Role.EDITOR); }

    public boolean canManage(User u, Article a) {
        return isPrivileged(u) || (u != null && u.getRole() == Role.AUTHOR && a.getAuthor().getId().equals(u.getId()));
    }

    // ---------- writing ----------
    @Transactional
    public Article create(ArticleDto dto, User author) {
        Article a = new Article();
        a.setAuthor(author);
        a.setSlug(SlugGenerator.unique(dto.getTitle(), articleRepository::existsBySlug));
        boolean newlyPublished = apply(a, dto, author);
        a = articleRepository.save(a);
        ArticleAnalytics an = new ArticleAnalytics();
        an.setArticle(a);
        analyticsRepository.save(an);
        if (newlyPublished) notifySubscribers(a);
        return a;
    }

    @Transactional(readOnly = true)
    public Article getForEdit(Long id, User actor) {
        Article a = getById(id);
        if (!canManage(actor, a)) throw new AccessDeniedException("You cannot edit this article");
        return a;
    }

    @Transactional
    public Article update(Long id, ArticleDto dto, User actor) {
        Article a = getForEdit(id, actor);
        boolean newlyPublished = apply(a, dto, actor);   // the slug stays stable so old links keep working
        a = articleRepository.save(a);
        if (newlyPublished) notifySubscribers(a);
        return a;
    }

    @Transactional
    public Article changeStatus(Long id, ArticleStatus status, User actor) {
        Article a = getForEdit(id, actor);
        boolean newlyPublished = applyStatus(a, status, null, isPrivileged(actor));
        a = articleRepository.save(a);
        if (newlyPublished) notifySubscribers(a);
        return a;
    }

    @Transactional
    public void delete(Long id, User actor) {
        articleRepository.delete(getForEdit(id, actor));
    }

    public ArticleDto toDto(Article a) {
        ArticleDto d = new ArticleDto();
        d.setTitle(a.getTitle());
        d.setContent(a.getContent());
        d.setExcerpt(a.getExcerpt());
        d.setFeaturedImage(a.getFeaturedImage());
        d.setCategoryId(a.getCategory() == null ? null : a.getCategory().getId());
        d.setTagNames(a.getTags().stream().map(Tag::getName).sorted().collect(Collectors.joining(", ")));
        d.setStatus(a.getStatus());
        d.setFeatured(a.isFeatured());
        if (a.getStatus() == SCHEDULED) d.setScheduledAt(a.getPublishedAt());
        return d;
    }

    /** Copies form data onto the entity. Returns true if the article was just published for the first time. */
    private boolean apply(Article a, ArticleDto dto, User actor) {
        String html = htmlUtils.sanitize(dto.getContent());
        a.setTitle(dto.getTitle().trim());
        a.setContent(html);
        a.setExcerpt(dto.getExcerpt() == null || dto.getExcerpt().isBlank()
                ? textUtils.excerpt(html, 200) : dto.getExcerpt().trim());
        a.setFeaturedImage(safeUrl(dto.getFeaturedImage()));
        a.setReadingTime(textUtils.readingTime(html));
        a.setCategory(dto.getCategoryId() == null ? null : categoryRepository.findById(dto.getCategoryId()).orElse(null));
        a.setTags(tagService.resolve(dto.getTagNames()));
        boolean privileged = isPrivileged(actor);
        if (privileged) a.setFeatured(dto.isFeatured());
        return applyStatus(a, dto.getStatus(), dto.getScheduledAt(), privileged);
    }

    private boolean applyStatus(Article a, ArticleStatus requested, LocalDateTime scheduledAt, boolean privileged) {
        ArticleStatus old = a.getId() == null ? null : a.getStatus();
        ArticleStatus req = requested == null ? DRAFT : requested;
        // Authors cannot publish: their request becomes "pending review" for an editor.
        if (!privileged && req != old && (req == PUBLISHED || req == SCHEDULED || req == ARCHIVED)) req = PENDING_REVIEW;
        LocalDateTime now = LocalDateTime.now();
        if (req == SCHEDULED) {
            if (scheduledAt == null || !scheduledAt.isAfter(now)) req = PUBLISHED;   // time already passed: publish now
            else a.setPublishedAt(scheduledAt);
        }
        boolean newlyPublished = false;
        if (req == PUBLISHED && old != PUBLISHED) {
            if (!(old == ARCHIVED && a.getPublishedAt() != null)) {   // restoring from archive keeps the original date
                a.setPublishedAt(now);
                newlyPublished = true;
            }
        }
        if (old == SCHEDULED && req != SCHEDULED && req != PUBLISHED) a.setPublishedAt(null);
        a.setStatus(req);
        return newlyPublished;
    }

    private String safeUrl(String url) {
        if (url == null) return null;
        String u = url.trim();
        return (u.startsWith("http://") || u.startsWith("https://")) ? u : null;
    }

    private void notifySubscribers(Article a) {
        if (mailConfig.isNotifyOnPublish()) {
            notificationService.notifyNewArticle(a.getTitle(), a.getSlug(), a.getExcerpt(), a.getAuthor().getName());
        }
    }

    /** Runs every minute and publishes scheduled articles whose time has come. */
    @Scheduled(fixedRate = 60_000, initialDelay = 30_000)
    @Transactional
    public void publishDueArticles() {
        for (Article a : articleRepository.findByStatusAndPublishedAtBefore(SCHEDULED, LocalDateTime.now())) {
            a.setStatus(PUBLISHED);
            articleRepository.save(a);
            notifySubscribers(a);
        }
    }

    // ---------- engagement ----------
    @Transactional
    public void like(String slug) {
        articleRepository.findBySlug(slug).filter(a -> a.getStatus() == PUBLISHED)
                .ifPresent(a -> articleRepository.incrementLikes(a.getId()));
    }

    @Transactional(readOnly = true)
    public boolean isSaved(User user, Article a) { return savedArticleRepository.existsByUserAndArticle(user, a); }

    /** Adds or removes the article from the user's reading list; returns true if it is now saved. */
    @Transactional
    public boolean toggleSaved(User user, Article a) {
        Optional<SavedArticle> existing = savedArticleRepository.findByUserAndArticle(user, a);
        if (existing.isPresent()) {
            savedArticleRepository.delete(existing.get());
            return false;
        }
        SavedArticle s = new SavedArticle();
        s.setUser(user);
        s.setArticle(a);
        savedArticleRepository.save(s);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Article> savedArticles(User user) {
        return savedArticleRepository.findByUserOrderBySavedAtDesc(user).stream().map(SavedArticle::getArticle).toList();
    }

    // ---------- dashboards ----------
    @Transactional(readOnly = true)
    public Page<Article> manageable(User u, Pageable p) {
        return isPrivileged(u) ? articleRepository.findAll(p) : articleRepository.findByAuthor(u, p);
    }

    @Transactional(readOnly = true)
    public Page<Article> withStatus(ArticleStatus status, Pageable p) { return articleRepository.findByStatus(status, p); }

    @Transactional(readOnly = true)
    public List<Article> recent() { return articleRepository.findTop5ByOrderByCreatedAtDesc(); }

    @Transactional(readOnly = true)
    public List<Article> latest(int n) { return articleRepository.findByStatus(PUBLISHED, org.springframework.data.domain.PageRequest.of(0, n, NEWEST)).getContent(); }
}
