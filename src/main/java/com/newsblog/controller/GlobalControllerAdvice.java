package com.newsblog.controller;

import com.newsblog.model.Category;
import com.newsblog.model.User;
import com.newsblog.service.CategoryService;
import com.newsblog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

/** Adds the logged-in user and the category list (for the navigation bar) to every page. */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {
    private final UserService userService;
    private final CategoryService categoryService;

    @ModelAttribute("currentUser")
    public User currentUser(Principal principal) { return userService.currentUser(principal); }

    @ModelAttribute("navCategories")
    public List<Category> navCategories() { return categoryService.findAll(); }
}
