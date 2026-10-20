package com.newsblog.service;

import com.newsblog.config.MailConfig;
import com.newsblog.model.Newsletter;
import com.newsblog.model.NewsletterSubscriber;
import com.newsblog.repository.NewsletterRepository;
import com.newsblog.repository.NewsletterSubscriberRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Every email the app sends. Methods are @Async so a slow SMTP server never blocks a web request,
 * and they take plain values (not entities) because they run outside the request's database session.
 * A failed send is logged, never thrown.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ObjectProvider<JavaMailSender> mailSender;
    private final MailConfig mailConfig;
    private final NewsletterSubscriberRepository subscriberRepository;
    private final NewsletterRepository newsletterRepository;

    @Async
    public void sendVerificationEmail(String email, String token) {
        String link = mailConfig.getBaseUrl() + "/newsletter/verify?token=" + token;
        send(email, "Confirm your NewsBlog subscription",
                "<p>Thanks for subscribing! Please confirm your email address:</p><p><a href=\"" + link + "\">Confirm subscription</a></p>");
    }

    @Async
    public void sendPasswordReset(String email, String token) {
        String link = mailConfig.getBaseUrl() + "/reset-password?token=" + token;
        send(email, "Reset your NewsBlog password",
                "<p>Use the link below to choose a new password. It expires in one hour.</p><p><a href=\"" + link + "\">Reset password</a></p>");
    }

    /** Tells verified subscribers about a newly published article. */
    @Async
    public void notifyNewArticle(String title, String slug, String excerpt, String authorName) {
        String link = mailConfig.getBaseUrl() + "/article/" + slug;
        for (NewsletterSubscriber s : subscriberRepository.findByActiveTrueAndVerifiedAtIsNotNull()) {
            send(s.getEmail(), "New on NewsBlog: " + title,
                    "<h2>" + HtmlUtils.htmlEscape(title) + "</h2><p>" + HtmlUtils.htmlEscape(excerpt == null ? "" : excerpt)
                            + "</p><p>By " + HtmlUtils.htmlEscape(authorName) + "</p><p><a href=\"" + link + "\">Read the article</a></p>"
                            + unsubscribeFooter(s));
        }
    }

    /** Tells an author that a comment on their article is now visible. */
    @Async
    public void notifyCommentApproved(String authorEmail, String articleTitle, String slug, String commenterName, String text) {
        send(authorEmail, "New comment on \"" + articleTitle + "\"",
                "<p><b>" + HtmlUtils.htmlEscape(commenterName) + "</b> commented:</p><blockquote>" + HtmlUtils.htmlEscape(text)
                        + "</blockquote><p><a href=\"" + mailConfig.getBaseUrl() + "/article/" + slug + "#comments\">View comment</a></p>");
    }

    /** Sends a saved newsletter campaign to every active, verified subscriber and records the result. */
    @Async
    public void sendCampaign(Long newsletterId) {
        Newsletter n = newsletterRepository.findById(newsletterId).orElse(null);
        if (n == null) return;
        int sent = 0;
        List<NewsletterSubscriber> recipients = subscriberRepository.findByActiveTrueAndVerifiedAtIsNotNull();
        for (NewsletterSubscriber s : recipients) {
            if (send(s.getEmail(), n.getSubject(), n.getContent() + unsubscribeFooter(s))) sent++;
        }
        n.setRecipientCount(sent);
        n.setSentAt(LocalDateTime.now());
        n.setStatus(recipients.isEmpty() || sent > 0 ? "SENT" : "FAILED");
        newsletterRepository.save(n);
    }

    private String unsubscribeFooter(NewsletterSubscriber s) {
        return "<hr><p style=\"font-size:12px;color:#666\">You receive this because you subscribed to NewsBlog. "
                + "<a href=\"" + mailConfig.getBaseUrl() + "/newsletter/unsubscribe?token=" + s.getVerificationToken() + "\">Unsubscribe</a></p>";
    }

    private boolean send(String to, String subject, String html) {
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            log.warn("No mail sender configured; skipped email to {}", to);
            return false;
        }
        try {
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            helper.setFrom(mailConfig.getFromEmail());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            sender.send(msg);
            return true;
        } catch (Exception e) {
            log.warn("Could not send email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
