package com.newsblog.controller;

import com.newsblog.model.Tag;
import com.newsblog.service.ArticleService;
import com.newsblog.service.TagService;
import com.newsblog.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final ArticleService articleService;

    @Value("${app.newsblog.articles-per-page:10}")
    private int pageSize;

    @GetMapping("/tags")
    public String list(Model model) {
        model.addAttribute("tags", tagService.withCounts());
        return "tags/list";
    }

    @GetMapping("/tags/{slug}")
    public String detail(@PathVariable String slug, @RequestParam(defaultValue = "0") int page, Model model) {
        Tag tag = tagService.getBySlug(slug);
        model.addAttribute("tag", tag);
        model.addAttribute("page", articleService.byTag(tag, PageRequest.of(Math.max(page, 0), pageSize, ArticleService.NEWEST)));
        model.addAttribute("pageUrl", TextUtils.query("/tags/" + slug));
        return "tags/detail";
    }

    @PostMapping("/tags")
    public String create(@RequestParam String name, RedirectAttributes ra) {
        try {
            tagService.create(name);
            ra.addFlashAttribute("msg", "Tag added.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tags";
    }

    @PostMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        tagService.delete(id);
        ra.addFlashAttribute("msg", "Tag deleted.");
        return "redirect:/tags";
    }
}
