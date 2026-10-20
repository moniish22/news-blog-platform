package com.newsblog.controller;

import com.newsblog.dto.CommentDto;
import com.newsblog.model.Article;
import com.newsblog.model.Comment;
import com.newsblog.model.CommentStatus;
import com.newsblog.service.ArticleService;
import com.newsblog.service.CommentService;
import com.newsblog.service.UserService;
import com.newsblog.util.TextUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** Posting comments (any logged-in user) and moderating them (EDITOR/ADMIN). */
@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final ArticleService articleService;
    private final UserService userService;

    @Value("${app.newsblog.comments-per-page:20}")
    private int pageSize;

    @PostMapping("/comments")
    public String add(@Valid @ModelAttribute CommentDto dto, BindingResult result, Principal principal, RedirectAttributes ra) {
        if (dto.getArticleId() == null) return "redirect:/";
        Article article = articleService.getById(dto.getArticleId());
        String back = "redirect:/article/" + article.getSlug() + "#comments";
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Write a comment of up to 2000 characters.");
            return back;
        }
        try {
            Comment c = commentService.add(dto, userService.currentUser(principal));
            ra.addFlashAttribute("msg", c.getStatus() == CommentStatus.APPROVED
                    ? "Comment posted." : "Thanks! Your comment will appear once a moderator approves it.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return back;
    }

    @GetMapping("/comments/mine")
    public String mine(@RequestParam(defaultValue = "0") int page, Principal principal, Model model) {
        model.addAttribute("page", commentService.byUser(userService.currentUser(principal),
                PageRequest.of(Math.max(page, 0), pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("pageUrl", TextUtils.query("/comments/mine"));
        return "comments/list";
    }

    @GetMapping("/moderation/comments")
    public String moderate(@RequestParam(defaultValue = "PENDING") CommentStatus status,
                           @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("status", status);
        model.addAttribute("page", commentService.byStatus(status,
                PageRequest.of(Math.max(page, 0), pageSize, Sort.by(Sort.Direction.DESC, "createdAt"))));
        model.addAttribute("pageUrl", TextUtils.query("/moderation/comments", "status", status.name()));
        model.addAttribute("pendingCount", commentService.count(CommentStatus.PENDING));
        model.addAttribute("approvedCount", commentService.count(CommentStatus.APPROVED));
        model.addAttribute("rejectedCount", commentService.count(CommentStatus.REJECTED));
        return "comments/moderate";
    }

    @PostMapping("/moderation/comments/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam(defaultValue = "PENDING") CommentStatus status, RedirectAttributes ra) {
        commentService.approve(id);
        ra.addFlashAttribute("msg", "Comment approved.");
        return "redirect:/moderation/comments?status=" + status;
    }

    @PostMapping("/moderation/comments/{id}/reject")
    public String reject(@PathVariable Long id, @RequestParam(defaultValue = "PENDING") CommentStatus status, RedirectAttributes ra) {
        commentService.reject(id);
        ra.addFlashAttribute("msg", "Comment rejected.");
        return "redirect:/moderation/comments?status=" + status;
    }

    @PostMapping("/moderation/comments/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam(defaultValue = "PENDING") CommentStatus status, RedirectAttributes ra) {
        commentService.delete(id);
        ra.addFlashAttribute("msg", "Comment deleted.");
        return "redirect:/moderation/comments?status=" + status;
    }
}
