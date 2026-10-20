package com.newsblog.service;

import com.newsblog.config.MailConfig;
import com.newsblog.model.NewsletterSubscriber;
import com.newsblog.repository.NewsletterRepository;
import com.newsblog.repository.NewsletterSubscriberRepository;
import com.newsblog.util.HtmlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsletterServiceTest {
    @Mock NewsletterSubscriberRepository subscriberRepository;
    @Mock NewsletterRepository newsletterRepository;
    @Mock NotificationService notificationService;
    @Mock MailConfig mailConfig;

    NewsletterService service;

    @BeforeEach
    void setUp() {
        service = new NewsletterService(subscriberRepository, newsletterRepository, notificationService, mailConfig, new HtmlUtils());
        lenient().when(subscriberRepository.save(any(NewsletterSubscriber.class))).thenAnswer(i -> i.getArgument(0));
        lenient().when(mailConfig.isVerificationRequired()).thenReturn(true);
    }

    @Test
    void rejectsInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> service.subscribe("not-an-email"));
    }

    @Test
    void newSubscriberMustVerifyByEmail() {
        when(subscriberRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.empty());
        assertEquals(NewsletterService.Result.PENDING_VERIFICATION, service.subscribe("  A@B.com "));
        verify(notificationService).sendVerificationEmail(eq("a@b.com"), anyString());
    }

    @Test
    void alreadyVerifiedSubscriberIsNotEmailedAgain() {
        NewsletterSubscriber s = new NewsletterSubscriber();
        s.setEmail("a@b.com");
        s.setVerifiedAt(LocalDateTime.now());
        when(subscriberRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(s));
        assertEquals(NewsletterService.Result.ALREADY_SUBSCRIBED, service.subscribe("a@b.com"));
        verifyNoInteractions(notificationService);
    }

    @Test
    void verifyMarksSubscriberConfirmed() {
        NewsletterSubscriber s = new NewsletterSubscriber();
        when(subscriberRepository.findByVerificationToken("tok")).thenReturn(Optional.of(s));
        assertTrue(service.verify("tok"));
        assertTrue(s.isVerified());
        assertTrue(s.isActive());
    }

    @Test
    void unsubscribeDeactivatesSubscriber() {
        NewsletterSubscriber s = new NewsletterSubscriber();
        when(subscriberRepository.findByVerificationToken("tok")).thenReturn(Optional.of(s));
        assertTrue(service.unsubscribe("tok"));
        assertFalse(s.isActive());
        assertFalse(service.unsubscribe("unknown"));
    }
}
