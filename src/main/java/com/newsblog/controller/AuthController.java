package com.newsblog.controller;

import com.newsblog.dto.UserDto;
import com.newsblog.service.NotificationService;
import com.newsblog.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

/** Login page, registration and password reset. (POST /login and /logout are handled by Spring Security.) */
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDto") UserDto dto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) return "register";
        try {
            userService.register(dto);
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "duplicate", e.getMessage());
            return "register";
        }
        ra.addFlashAttribute("msg", "Account created. Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotForm() { return "forgot-password"; }

    @PostMapping("/forgot-password")
    public String forgot(@RequestParam String email, RedirectAttributes ra) {
        userService.createResetToken(email).ifPresent(token -> notificationService.sendPasswordReset(email.trim().toLowerCase(), token));
        // Same message whether or not the account exists, so emails can't be probed.
        ra.addFlashAttribute("msg", "If that email has an account, we have sent a reset link.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("valid", userService.isResetTokenValid(token));
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String reset(@RequestParam String token, @RequestParam String password, RedirectAttributes ra) {
        if (userService.resetPassword(token, password)) {
            ra.addFlashAttribute("msg", "Password updated. You can log in now.");
            return "redirect:/login";
        }
        ra.addFlashAttribute("error", "The link is invalid or expired, or the password has fewer than 6 characters.");
        return "redirect:/reset-password?token=" + UriUtils.encodeQueryParam(token, StandardCharsets.UTF_8);
    }
}
