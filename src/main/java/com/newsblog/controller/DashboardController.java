package com.newsblog.controller;

import com.newsblog.model.User;
import com.newsblog.service.*;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

/** /dashboard shows a different page for each role. */
@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;
    private final AnalyticsService analyticsService;
    private final NewsletterService newsletterService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "0") int page, Principal principal, Model model) {
        User user = userService.currentUser(principal);
        var pageable = PageRequest.of(Math.max(page, 0), 10, ArticleService.RECENTLY_CREATED);
        model.addAttribute("pageUrl", TextUtils.query("/dashboard"));
        switch (user.getRole()) {
            case ADMIN -> {
                model.addAttribute("stats", analyticsService.dashboardStats());
                model.addAttribute("page", articleService.manageable(user, pageable));
                model.addAttribute("recentComments", commentService.recent());
                model.addAttribute("statuses", ArticleStatus.values());
                return "dashboard/admin";
            }
            case EDITOR -> {
                model.addAttribute("stats", analyticsService.dashboardStats());
                model.addAttribute("reviewQueue", articleService.withStatus(ArticleStatus.PENDING_REVIEW, PageRequest.of(0, 20, ArticleService.RECENTLY_CREATED)).getContent());
                model.addAttribute("page", articleService.manageable(user, pageable));
                return "dashboard/editor";
            }
            case AUTHOR -> {
                model.addAttribute("stats", analyticsService.authorStats(user));
                model.addAttribute("page", articleService.manageable(user, pageable));
                return "dashboard/author";
            }
            default -> { return "redirect:/profile/dashboard"; }
        }
    }
}
