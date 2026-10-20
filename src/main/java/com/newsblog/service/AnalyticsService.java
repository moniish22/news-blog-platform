package com.newsblog.service;

import com.newsblog.model.*;
import com.newsblog.repository.*;
import com.newsblog.util.ArticleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/** View tracking, trending algorithm, dashboard KPIs and reports. */
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private static final int TRENDING_WINDOW_DAYS = 7;

    private final ArticleRepository articleRepository;
    private final ArticleViewRepository viewRepository;
    private final ArticleAnalyticsRepository analyticsRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final NewsletterSubscriberRepository subscriberRepository;
    private final UserRepository userRepository;

    // ---------- tracking ----------
    @Transactional
    public void recordView(Article article, User user, String ip) {
        boolean firstFromThisIp = !viewRepository.existsByArticleAndIpAddress(article, ip);
        ArticleView v = new ArticleView();
        v.setArticle(article);
        v.setUser(user);
        v.setIpAddress(ip);
        viewRepository.save(v);
        articleRepository.incrementViews(article.getId());
        ArticleAnalytics an = analyticsFor(article);
        an.setViews(an.getViews() + 1);
        if (firstFromThisIp) an.setUniqueViews(an.getUniqueViews() + 1);
        analyticsRepository.save(an);
    }

    /** Called from the browser when a reader leaves the page: updates average read time and bounce rate. */
    @Transactional
    public void recordReadTime(Long articleId, int seconds) {
        articleRepository.findById(articleId).ifPresent(article -> {
            int s = Math.max(0, Math.min(seconds, 3600));
            ArticleAnalytics an = analyticsFor(article);
            int n = an.getReadSamples();
            an.setAvgReadTime((an.getAvgReadTime() * n + s) / (n + 1));
            if (s < 10) an.setBounces(an.getBounces() + 1);
            an.setReadSamples(n + 1);
            an.setBounceRate(an.getBounces() * 100.0 / an.getReadSamples());
            analyticsRepository.save(an);
        });
    }

    @Transactional
    public void recordShare(String slug) {
        articleRepository.findBySlug(slug).filter(a -> a.getStatus() == ArticleStatus.PUBLISHED).ifPresent(article -> {
            articleRepository.incrementShares(article.getId());
            ArticleAnalytics an = analyticsFor(article);
            an.setShares(an.getShares() + 1);
            analyticsRepository.save(an);
        });
    }

    private ArticleAnalytics analyticsFor(Article article) {
        return analyticsRepository.findByArticle(article).orElseGet(() -> {
            ArticleAnalytics n = new ArticleAnalytics();
            n.setArticle(article);
            return n;
        });
    }

    // ---------- trending ----------
    /**
     * Trending = most views in the last 7 days. If fewer than {@code limit} articles
     * were viewed recently, the list is topped up with the all-time most viewed.
     */
    @Transactional(readOnly = true)
    public List<Article> trending(int limit) {
        List<Long> ids = viewRepository.trendingIds(LocalDateTime.now().minusDays(TRENDING_WINDOW_DAYS),
                ArticleStatus.PUBLISHED, PageRequest.of(0, limit));
        Map<Long, Article> byId = articleRepository.findAllById(ids).stream().collect(Collectors.toMap(Article::getId, a -> a));
        List<Article> result = new ArrayList<>();
        ids.forEach(id -> { if (byId.containsKey(id)) result.add(byId.get(id)); });
        for (Article a : articleRepository.findTop5ByStatusOrderByViewsDesc(ArticleStatus.PUBLISHED)) {
            if (result.size() >= limit) break;
            if (result.stream().noneMatch(x -> x.getId().equals(a.getId()))) result.add(a);
        }
        return result;
    }

    // ---------- dashboards & reports ----------
    @Transactional(readOnly = true)
    public Map<String, Object> dashboardStats() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("totalArticles", articleRepository.count());
        s.put("draftArticles", articleRepository.countByStatus(ArticleStatus.DRAFT));
        s.put("pendingArticles", articleRepository.countByStatus(ArticleStatus.PENDING_REVIEW));
        s.put("publishedArticles", articleRepository.countByStatus(ArticleStatus.PUBLISHED));
        s.put("scheduledArticles", articleRepository.countByStatus(ArticleStatus.SCHEDULED));
        s.put("archivedArticles", articleRepository.countByStatus(ArticleStatus.ARCHIVED));
        s.put("totalComments", commentRepository.count());
        s.put("pendingComments", commentRepository.countByStatus(CommentStatus.PENDING));
        s.put("subscribers", subscriberRepository.countByActiveTrue());
        s.put("verifiedSubscribers", subscriberRepository.countByActiveTrueAndVerifiedAtIsNotNull());
        s.put("totalViews", articleRepository.sumViews());
        s.put("viewsToday", viewRepository.countByViewedAtAfter(LocalDate.now().atStartOfDay()));
        s.put("totalUsers", userRepository.count());
        return s;
    }

    /** Stats for one author's own dashboard. */
    @Transactional(readOnly = true)
    public Map<String, Object> authorStats(User author) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("articles", articleRepository.countByAuthor(author));
        s.put("views", articleRepository.sumViewsByAuthor(author));
        s.put("comments", commentRepository.countByArticleAuthor(author));
        return s;
    }

    /** Page views for each of the last {@code days} days (oldest first), for the bar chart. */
    @Transactional(readOnly = true)
    public Map<String, Long> viewsPerDay(int days) {
        LocalDate today = LocalDate.now();
        Map<LocalDate, Long> counts = new LinkedHashMap<>();
        for (int i = days - 1; i >= 0; i--) counts.put(today.minusDays(i), 0L);
        for (LocalDateTime t : viewRepository.viewTimesSince(today.minusDays(days - 1L).atStartOfDay())) {
            counts.computeIfPresent(t.toLocalDate(), (d, c) -> c + 1);
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
        Map<String, Long> labelled = new LinkedHashMap<>();
        counts.forEach((d, c) -> labelled.put(d.format(f), c));
        return labelled;
    }

    @Transactional(readOnly = true)
    public List<ArticleAnalytics> topAnalytics(int n) {
        return analyticsRepository.findAll(PageRequest.of(0, n, Sort.by(Sort.Direction.DESC, "views"))).getContent();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> publishedPerCategory() {
        Map<String, Long> m = new LinkedHashMap<>();
        categoryRepository.findAllByOrderByNameAsc()
                .forEach(c -> m.put(c.getName(), articleRepository.countByCategoryAndStatus(c, ArticleStatus.PUBLISHED)));
        return m;
    }

    /** CSV report of per-article analytics (downloaded from the admin analytics page). */
    @Transactional(readOnly = true)
    public String exportCsv() {
        StringBuilder sb = new StringBuilder("Title,Status,Author,Views,Unique views,Shares,Avg read time (s),Bounce rate (%)\n");
        for (ArticleAnalytics an : analyticsRepository.findAll(Sort.by(Sort.Direction.DESC, "views"))) {
            Article a = an.getArticle();
            sb.append(csv(a.getTitle())).append(',').append(a.getStatus().getLabel()).append(',').append(csv(a.getAuthor().getName()))
              .append(',').append(an.getViews()).append(',').append(an.getUniqueViews()).append(',').append(an.getShares())
              .append(',').append(String.format(Locale.ROOT, "%.0f", an.getAvgReadTime()))
              .append(',').append(String.format(Locale.ROOT, "%.1f", an.getBounceRate())).append('\n');
        }
        return sb.toString();
    }

    private String csv(String v) {
        String s = v == null ? "" : v.replace("\"", "\"\"");
        // Guard against spreadsheet formula injection.
        if (!s.isEmpty() && "=+-@".indexOf(s.charAt(0)) >= 0) s = "'" + s;
        return "\"" + s + "\"";
    }
}
