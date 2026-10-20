package com.newsblog.controller;

import com.newsblog.model.Article;
import com.newsblog.model.User;
import com.newsblog.service.ArticleService;
import com.newsblog.service.CommentService;
import com.newsblog.service.UserService;
import com.newsblog.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** Public author profiles, "edit my profile" and the reader's own dashboard (reading list, comments). */
@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ArticleService articleService;
    private final CommentService commentService;

    @GetMapping("/authors/{id}")
    public String profile(@PathVariable Long id, @RequestParam(defaultValue = "0") int page, Model model) {
        User author = userService.getById(id);
        model.addAttribute("author", author);
        model.addAttribute("page", articleService.byAuthor(author, PageRequest.of(Math.max(page, 0), 10, ArticleService.NEWEST)));
        model.addAttribute("pageUrl", TextUtils.query("/authors/" + id));
        return "users/profile";
    }

    @GetMapping("/profile")
    public String myProfile(Principal principal) {
        return "redirect:/authors/" + userService.currentUser(principal).getId();
    }

    @GetMapping("/profile/edit")
    public String editForm() { return "users/edit"; }

    @PostMapping("/profile/edit")
    public String edit(@RequestParam String name, @RequestParam(required = false) String bio,
                       @RequestParam(required = false) String avatar, Principal principal, RedirectAttributes ra) {
        if (name == null || name.isBlank() || name.length() > 100) {
            ra.addFlashAttribute("error", "Name is required (max 100 characters).");
            return "redirect:/profile/edit";
        }
        userService.updateProfile(userService.currentUser(principal), name, bio, avatar);
        ra.addFlashAttribute("msg", "Profile updated.");
        return "redirect:/profile/edit";
    }

    @GetMapping("/profile/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.currentUser(principal);
        model.addAttribute("saved", articleService.savedArticles(user));
        model.addAttribute("myComments", commentService.byUser(user, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent());
        return "users/dashboard";
    }

    @PostMapping("/profile/saved/{articleId}/remove")
    public String removeSaved(@PathVariable Long articleId, Principal principal, RedirectAttributes ra) {
        User user = userService.currentUser(principal);
        Article a = articleService.getById(articleId);
        if (articleService.isSaved(user, a)) articleService.toggleSaved(user, a);
        ra.addFlashAttribute("msg", "Removed from your reading list.");
        return "redirect:/profile/dashboard";
    }
}
