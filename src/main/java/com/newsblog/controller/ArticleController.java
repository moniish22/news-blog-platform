package com.newsblog.controller;

import com.newsblog.dto.ArticleDto;
import com.newsblog.dto.CommentDto;
import com.newsblog.model.Article;
import com.newsblog.model.User;
import com.newsblog.service.*;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.DateUtils;
import com.newsblog.util.TextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.YearMonth;
import java.util.*;

/** Public reading pages (home, list, detail, archive) and the author/editor screens to write articles. */
@Controller
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final CommentService commentService;
    private final AnalyticsService analyticsService;
    private final UserService userService;
    private final DateUtils dateUtils;

    @Value("${app.newsblog.articles-per-page:10}")
    private int pageSize;

    // ---------- public pages ----------
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featured", articleService.featured());
        model.addAttribute("latest", articleService.published(PageRequest.of(0, 6, ArticleService.NEWEST)));
        model.addAttribute("trending", analyticsService.trending(5));
        model.addAttribute("popularTags", tagService.popular(12));
        model.addAttribute("categories", categoryService.withCounts());
        return "index";
    }

    @GetMapping("/articles")
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", articleService.published(PageRequest.of(Math.max(page, 0), pageSize, ArticleService.NEWEST)));
        model.addAttribute("pageUrl", TextUtils.query("/articles"));
        return "articles/list";
    }

    @GetMapping("/articles/archive")
    public String archive(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
                          @RequestParam(defaultValue = "0") int page, Model model) {
        YearMonth ym = (year != null && month != null && month >= 1 && month <= 12) ? YearMonth.of(year, month) : YearMonth.now();
        model.addAttribute("selected", ym);
        model.addAttribute("months", dateUtils.lastMonths(12));
        model.addAttribute("page", articleService.byMonth(ym, PageRequest.of(Math.max(page, 0), pageSize, ArticleService.NEWEST)));
        model.addAttribute("pageUrl", TextUtils.query("/articles/archive", "year", String.valueOf(ym.getYear()), "month", String.valueOf(ym.getMonthValue())));
        return "articles/archive";
    }

    @GetMapping("/article/{slug}")
    public String detail(@PathVariable String slug, Principal principal, HttpServletRequest request, HttpSession session, Model model) {
        User viewer = userService.currentUser(principal);
        Article article = articleService.getForViewer(slug, viewer);
        if (article.getStatus() == ArticleStatus.PUBLISHED) {
            analyticsService.recordView(article, viewer, clientIp(request));
        }
        model.addAttribute("article", article);
        model.addAttribute("comments", commentService.approvedFor(article));
        model.addAttribute("related", articleService.related(article));
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("saved", viewer != null && articleService.isSaved(viewer, article));
        model.addAttribute("liked", likedSlugs(session).contains(slug));
        model.addAttribute("canEdit", viewer != null && articleService.canManage(viewer, article));
        return "articles/detail";
    }

    @PostMapping("/article/{slug}/like")
    public String like(@PathVariable String slug, HttpSession session) {
        if (likedSlugs(session).add(slug)) articleService.like(slug);   // one like per visitor session
        return "redirect:/article/" + slug;
    }

    @PostMapping("/article/{slug}/save")
    public String save(@PathVariable String slug, Principal principal, RedirectAttributes ra) {
        User user = userService.currentUser(principal);
        Article article = articleService.getForViewer(slug, user);
        boolean saved = articleService.toggleSaved(user, article);
        ra.addFlashAttribute("msg", saved ? "Added to your reading list." : "Removed from your reading list.");
        return "redirect:/article/" + slug;
    }

    /** Called by article.js when a share button is used. */
    @PostMapping("/article/{slug}/share")
    @ResponseBody
    public ResponseEntity<Void> share(@PathVariable String slug) {
        analyticsService.recordShare(slug);
        return ResponseEntity.noContent().build();
    }

    /** Called by article.js (sendBeacon) when the reader leaves the page. */
    @PostMapping("/analytics/read-time")
    @ResponseBody
    public ResponseEntity<Void> readTime(@RequestParam Long articleId, @RequestParam int seconds) {
        analyticsService.recordReadTime(articleId, seconds);
        return ResponseEntity.noContent().build();
    }

    // ---------- write / edit (AUTHOR, EDITOR, ADMIN) ----------
    @GetMapping("/author/articles/new")
    public String createForm(Principal principal, Model model) {
        ArticleDto dto = new ArticleDto();
        prepareForm(model, userService.currentUser(principal), dto.getStatus(), "/author/articles", false);
        model.addAttribute("articleDto", dto);
        return "articles/create";
    }

    @PostMapping("/author/articles")
    public String create(@Valid @ModelAttribute("articleDto") ArticleDto dto, BindingResult result,
                         Principal principal, Model model, RedirectAttributes ra) {
        User user = userService.currentUser(principal);
        if (result.hasErrors()) {
            prepareForm(model, user, dto.getStatus(), "/author/articles", false);
            return "articles/create";
        }
        Article a = articleService.create(dto, user);
        ra.addFlashAttribute("msg", "Saved \"" + a.getTitle() + "\" as " + a.getStatus().getLabel() + ".");
        return "redirect:/dashboard";
    }

    @GetMapping("/author/articles/{id}/edit")
    public String editForm(@PathVariable Long id, Principal principal, Model model) {
        User user = userService.currentUser(principal);
        Article a = articleService.getForEdit(id, user);
        prepareForm(model, user, a.getStatus(), "/author/articles/" + id, true);
        model.addAttribute("articleDto", articleService.toDto(a));
        model.addAttribute("article", a);
        return "articles/edit";
    }

    @PostMapping("/author/articles/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("articleDto") ArticleDto dto, BindingResult result,
                         Principal principal, Model model, RedirectAttributes ra) {
        User user = userService.currentUser(principal);
        if (result.hasErrors()) {
            prepareForm(model, user, dto.getStatus(), "/author/articles/" + id, true);
            model.addAttribute("article", articleService.getForEdit(id, user));
            return "articles/edit";
        }
        Article a = articleService.update(id, dto, user);
        ra.addFlashAttribute("msg", "Updated \"" + a.getTitle() + "\" (" + a.getStatus().getLabel() + ").");
        return "redirect:/dashboard";
    }

    @PostMapping("/author/articles/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam ArticleStatus status, Principal principal, RedirectAttributes ra) {
        Article a = articleService.changeStatus(id, status, userService.currentUser(principal));
        ra.addFlashAttribute("msg", "\"" + a.getTitle() + "\" is now " + a.getStatus().getLabel() + ".");
        return "redirect:/dashboard";
    }

    @PostMapping("/author/articles/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        articleService.delete(id, userService.currentUser(principal));
        ra.addFlashAttribute("msg", "Article deleted.");
        return "redirect:/dashboard";
    }

    // ---------- helpers ----------
    private void prepareForm(Model model, User user, ArticleStatus current, String action, boolean edit) {
        List<ArticleStatus> options = new ArrayList<>(articleService.isPrivileged(user)
                ? List.of(ArticleStatus.values())
                : List.of(ArticleStatus.DRAFT, ArticleStatus.PENDING_REVIEW));
        if (current != null && !options.contains(current)) options.add(current);
        model.addAttribute("statusOptions", options);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("canFeature", articleService.isPrivileged(user));
        model.addAttribute("formAction", action);
        model.addAttribute("editing", edit);
    }

    @SuppressWarnings("unchecked")
    private Set<String> likedSlugs(HttpSession session) {
        Set<String> liked = (Set<String>) session.getAttribute("likedSlugs");
        if (liked == null) {
            liked = new HashSet<>();
            session.setAttribute("likedSlugs", liked);
        }
        return liked;
    }

    private String clientIp(HttpServletRequest req) {
        String fwd = req.getHeader("X-Forwarded-For");
        return (fwd != null && !fwd.isBlank()) ? fwd.split(",")[0].trim() : req.getRemoteAddr();
    }
}
