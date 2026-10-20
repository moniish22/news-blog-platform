package com.newsblog.controller;

import com.newsblog.config.MailConfig;
import com.newsblog.dto.NewsletterDto;
import com.newsblog.model.Article;
import com.newsblog.service.ArticleService;
import com.newsblog.service.NewsletterService;
import com.newsblog.util.TextUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

/** Public: subscribe / verify / unsubscribe. EDITOR+ADMIN: subscriber list and sending campaigns. */
@Controller
@RequestMapping("/newsletter")
@RequiredArgsConstructor
public class NewsletterController {
    private final NewsletterService newsletterService;
    private final ArticleService articleService;
    private final MailConfig mailConfig;

    @GetMapping("/subscribe")
    public String subscribeForm() { return "newsletter/subscribe"; }

    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String email, RedirectAttributes ra) {
        try {
            NewsletterService.Result r = newsletterService.subscribe(email);
            ra.addFlashAttribute("msg", switch (r) {
                case PENDING_VERIFICATION -> "Almost done! Check your inbox and confirm your email address.";
                case SUBSCRIBED -> "You are subscribed. Thanks!";
                case ALREADY_SUBSCRIBED -> "You are already subscribed.";
            });
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/newsletter/subscribe";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token, RedirectAttributes ra) {
        if (newsletterService.verify(token)) ra.addFlashAttribute("msg", "Email confirmed. You will now receive our newsletter.");
        else ra.addFlashAttribute("error", "That confirmation link is not valid.");
        return "redirect:/newsletter/subscribe";
    }

    @GetMapping("/unsubscribe")
    public String unsubscribeForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("valid", newsletterService.isValidToken(token));
        return "newsletter/unsubscribe";
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(@RequestParam String token, RedirectAttributes ra) {
        if (newsletterService.unsubscribe(token)) ra.addFlashAttribute("msg", "You have been unsubscribed.");
        else ra.addFlashAttribute("error", "That unsubscribe link is not valid.");
        return "redirect:/newsletter/subscribe";
    }

    // ---------- staff ----------
    @GetMapping("/manage")
    public String manage(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("page", newsletterService.subscribers(PageRequest.of(Math.max(page, 0), 20, Sort.by(Sort.Direction.DESC, "subscribedAt"))));
        model.addAttribute("pageUrl", TextUtils.query("/newsletter/manage"));
        model.addAttribute("campaigns", newsletterService.campaigns());
        model.addAttribute("recipients", newsletterService.activeRecipients());
        return "newsletter/manage";
    }

    @PostMapping("/manage/{id}/toggle")
    public String toggle(@PathVariable Long id, RedirectAttributes ra) {
        newsletterService.toggleActive(id);
        ra.addFlashAttribute("msg", "Subscriber updated.");
        return "redirect:/newsletter/manage";
    }

    @PostMapping("/manage/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        newsletterService.delete(id);
        ra.addFlashAttribute("msg", "Subscriber removed.");
        return "redirect:/newsletter/manage";
    }

    @GetMapping("/send")
    public String sendForm(Model model) {
        NewsletterDto dto = new NewsletterDto();
        dto.setSubject("This week on NewsBlog");
        StringBuilder html = new StringBuilder("<h2>This week on NewsBlog</h2><ul>");
        for (Article a : articleService.latest(5)) {
            html.append("<li><a href=\"").append(mailConfig.getBaseUrl()).append("/article/").append(a.getSlug()).append("\">")
                .append(HtmlUtils.htmlEscape(a.getTitle())).append("</a></li>");
        }
        dto.setContent(html.append("</ul>").toString());
        model.addAttribute("newsletterDto", dto);
        model.addAttribute("recipients", newsletterService.activeRecipients());
        return "newsletter/send";
    }

    @PostMapping("/send")
    public String send(@Valid @ModelAttribute("newsletterDto") NewsletterDto dto, BindingResult result, Model model, RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("recipients", newsletterService.activeRecipients());
            return "newsletter/send";
        }
        newsletterService.send(dto);
        ra.addFlashAttribute("msg", "Newsletter is being sent in the background. Check the campaign list for the result.");
        return "redirect:/newsletter/manage";
    }
}
