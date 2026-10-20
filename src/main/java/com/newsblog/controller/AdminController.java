package com.newsblog.controller;

import com.newsblog.config.MailConfig;
import com.newsblog.model.Role;
import com.newsblog.model.User;
import com.newsblog.service.AnalyticsService;
import com.newsblog.service.NewsletterService;
import com.newsblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/** Admin console (ADMIN only; enforced in SecurityConfig). */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AnalyticsService analyticsService;
    private final NewsletterService newsletterService;
    private final MailConfig mailConfig;

    @Value("${app.newsblog.articles-per-page:10}") private int articlesPerPage;
    @Value("${app.newsblog.comments-per-page:20}") private int commentsPerPage;

    @GetMapping
    public String console(Model model) {
        Map<String, Long> byRole = new LinkedHashMap<>();
        for (Role r : Role.values()) byRole.put(r.name(), userService.countByRole(r));
        model.addAttribute("stats", analyticsService.dashboardStats());
        model.addAttribute("byRole", byRole);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam Role role, Principal principal, RedirectAttributes ra) {
        if (userService.currentUser(principal).getId().equals(id)) {
            ra.addFlashAttribute("error", "You cannot change your own role.");
        } else {
            userService.changeRole(id, role);
            ra.addFlashAttribute("msg", "Role updated.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggle(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        User me = userService.currentUser(principal);
        if (me.getId().equals(id)) {
            ra.addFlashAttribute("error", "You cannot disable your own account.");
        } else {
            userService.toggleEnabled(id);
            ra.addFlashAttribute("msg", "Account updated.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("Articles per page", articlesPerPage);
        s.put("Comments per page", commentsPerPage);
        s.put("Site URL (used in emails)", mailConfig.getBaseUrl());
        s.put("Newsletter from address", mailConfig.getFromEmail());
        s.put("Newsletter email verification", mailConfig.isVerificationRequired() ? "Required" : "Off");
        s.put("Email subscribers on publish", mailConfig.isNotifyOnPublish() ? "On" : "Off");
        s.put("Auto-approve readers' comments", mailConfig.isAutoApproveReaders() ? "On" : "Off");
        model.addAttribute("settings", s);
        return "admin/settings";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        Map<String, Long> perDay = analyticsService.viewsPerDay(14);
        model.addAttribute("stats", analyticsService.dashboardStats());
        model.addAttribute("viewsByDay", perDay);
        model.addAttribute("maxViews", perDay.values().stream().mapToLong(Long::longValue).max().orElse(0));
        model.addAttribute("top", analyticsService.topAnalytics(10));
        model.addAttribute("byCategory", analyticsService.publishedPerCategory());
        return "admin/analytics";
    }

    @GetMapping("/analytics/export")
    public ResponseEntity<byte[]> export() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=article-analytics.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(analyticsService.exportCsv().getBytes(StandardCharsets.UTF_8));
    }
}
