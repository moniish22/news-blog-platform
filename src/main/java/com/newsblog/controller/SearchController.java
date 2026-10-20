package com.newsblog.controller;

import com.newsblog.service.ArticleService;
import com.newsblog.service.CategoryService;
import com.newsblog.service.SearchService;
import com.newsblog.util.TextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final CategoryService categoryService;

    @Value("${app.newsblog.articles-per-page:10}")
    private int pageSize;

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, @RequestParam(required = false) Long category,
                         @RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("q", q == null ? "" : q.trim());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("page", searchService.search(q, category, PageRequest.of(Math.max(page, 0), pageSize, ArticleService.NEWEST)));
        model.addAttribute("pageUrl", TextUtils.query("/search", "q", q, "category", category == null ? null : category.toString()));
        return "articles/search";
    }
}
