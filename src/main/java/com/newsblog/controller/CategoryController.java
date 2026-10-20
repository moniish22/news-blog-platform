package com.newsblog.controller;

import com.newsblog.dto.CategoryDto;
import com.newsblog.model.Category;
import com.newsblog.service.ArticleService;
import com.newsblog.service.CategoryService;
import com.newsblog.util.TextUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ArticleService articleService;

    @Value("${app.newsblog.articles-per-page:10}")
    private int pageSize;

    @GetMapping("/category")
    public String list(Model model) {
        model.addAttribute("categories", categoryService.withCounts());
        model.addAttribute("categoryDto", new CategoryDto());
        return "categories/list";
    }

    @GetMapping("/category/{slug}")
    public String detail(@PathVariable String slug, @RequestParam(defaultValue = "0") int page, Model model) {
        Category c = categoryService.getBySlug(slug);
        model.addAttribute("category", c);
        model.addAttribute("page", articleService.byCategory(c, PageRequest.of(Math.max(page, 0), pageSize, ArticleService.NEWEST)));
        model.addAttribute("pageUrl", TextUtils.query("/category/" + slug));
        return "categories/detail";
    }

    @PostMapping("/categories")
    public String create(@Valid @ModelAttribute("categoryDto") CategoryDto dto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            ra.addFlashAttribute("error", "Category name is required (max 100 characters).");
        } else {
            try {
                categoryService.create(dto);
                ra.addFlashAttribute("msg", "Category created.");
            } catch (IllegalArgumentException e) {
                ra.addFlashAttribute("error", e.getMessage());
            }
        }
        return "redirect:/category";
    }

    @PostMapping("/categories/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.delete(id);
            ra.addFlashAttribute("msg", "Category deleted.");
        } catch (IllegalStateException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/category";
    }
}
