package com.newsblog.service;

import com.newsblog.config.MailConfig;
import com.newsblog.dto.NewsletterDto;
import com.newsblog.model.Newsletter;
import com.newsblog.model.NewsletterSubscriber;
import com.newsblog.repository.NewsletterRepository;
import com.newsblog.repository.NewsletterSubscriberRepository;
import com.newsblog.util.HtmlUtils;
import com.newsblog.util.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Newsletter logic: double opt-in subscription, unsubscribe by token and campaign sending.
 * A subscriber receives email only when active AND verified.
 */
@Service
@RequiredArgsConstructor
public class NewsletterService {
    public enum Result { PENDING_VERIFICATION, SUBSCRIBED, ALREADY_SUBSCRIBED }

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final NewsletterSubscriberRepository subscriberRepository;
    private final NewsletterRepository newsletterRepository;
    private final NotificationService notificationService;
    private final MailConfig mailConfig;
    private final HtmlUtils htmlUtils;

    @Transactional
    public Result subscribe(String rawEmail) {
        String email = rawEmail == null ? "" : rawEmail.trim().toLowerCase();
        if (!EMAIL.matcher(email).matches() || email.length() > 254) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        Optional<NewsletterSubscriber> existing = subscriberRepository.findByEmailIgnoreCase(email);
        NewsletterSubscriber s;
        if (existing.isPresent()) {
            s = existing.get();
            if (s.isActive() && s.isVerified()) return Result.ALREADY_SUBSCRIBED;
            s.setActive(true);
        } else {
            s = new NewsletterSubscriber();
            s.setEmail(email);
            s.setVerificationToken(UUID.randomUUID().toString());
        }
        // Returning subscribers who already proved their address don't need to verify again.
        if (!mailConfig.isVerificationRequired() || s.isVerified()) {
            if (!s.isVerified()) s.setVerifiedAt(LocalDateTime.now());
            subscriberRepository.save(s);
            return Result.SUBSCRIBED;
        }
        subscriberRepository.save(s);
        notificationService.sendVerificationEmail(s.getEmail(), s.getVerificationToken());
        return Result.PENDING_VERIFICATION;
    }

    @Transactional
    public boolean verify(String token) {
        return token != null && subscriberRepository.findByVerificationToken(token).map(s -> {
            s.setVerifiedAt(s.getVerifiedAt() == null ? LocalDateTime.now() : s.getVerifiedAt());
            s.setActive(true);
            subscriberRepository.save(s);
            return true;
        }).orElse(false);
    }

    @Transactional
    public boolean unsubscribe(String token) {
        return token != null && subscriberRepository.findByVerificationToken(token).map(s -> {
            s.setActive(false);
            subscriberRepository.save(s);
            return true;
        }).orElse(false);
    }

    public boolean isValidToken(String token) {
        return token != null && subscriberRepository.findByVerificationToken(token).isPresent();
    }

    @Transactional(readOnly = true)
    public Page<NewsletterSubscriber> subscribers(Pageable pageable) { return subscriberRepository.findAll(pageable); }

    @Transactional
    public void toggleActive(Long id) {
        NewsletterSubscriber s = subscriberRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Subscriber not found"));
        s.setActive(!s.isActive());
        subscriberRepository.save(s);
    }

    @Transactional
    public void delete(Long id) { subscriberRepository.deleteById(id); }

    public List<Newsletter> campaigns() { return newsletterRepository.findTop10ByOrderByIdDesc(); }

    public long activeRecipients() { return subscriberRepository.countByActiveTrueAndVerifiedAtIsNotNull(); }

    /**
     * Saves the campaign, then sends it in the background. Deliberately NOT @Transactional: the row must be
     * committed before the async sender looks it up.
     */
    public Newsletter send(NewsletterDto dto) {
        Newsletter n = new Newsletter();
        n.setSubject(dto.getSubject().trim());
        n.setContent(htmlUtils.sanitize(dto.getContent()));
        n.setStatus("SENDING");
        n = newsletterRepository.save(n);
        notificationService.sendCampaign(n.getId());
        return n;
    }
}
