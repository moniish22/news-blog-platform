package com.newsblog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Newsletter subscriber. Emails are only sent when {@code active} is true AND the address is verified.
 * {@code verificationToken} doubles as the unsubscribe token.
 */
@Entity
@Table(name = "newsletter_subscribers")
@Getter @Setter @NoArgsConstructor
public class NewsletterSubscriber {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "subscribed_at", updatable = false)
    private LocalDateTime subscribedAt;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    public boolean isVerified() { return verifiedAt != null; }

    @PrePersist void onCreate() { subscribedAt = LocalDateTime.now(); }
}
