package com.newsblog.repository;

import com.newsblog.model.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {
    Optional<NewsletterSubscriber> findByEmailIgnoreCase(String email);
    Optional<NewsletterSubscriber> findByVerificationToken(String token);
    /** Everyone who should actually receive emails. */
    List<NewsletterSubscriber> findByActiveTrueAndVerifiedAtIsNotNull();
    List<NewsletterSubscriber> findTop5ByOrderBySubscribedAtDesc();
    long countByActiveTrue();
    long countByActiveTrueAndVerifiedAtIsNotNull();
}
