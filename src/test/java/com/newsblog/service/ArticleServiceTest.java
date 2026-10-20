package com.newsblog.service;

import com.newsblog.config.MailConfig;
import com.newsblog.dto.ArticleDto;
import com.newsblog.model.Article;
import com.newsblog.model.Role;
import com.newsblog.model.User;
import com.newsblog.repository.*;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.HtmlUtils;
import com.newsblog.util.TextUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @Mock ArticleRepository articleRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock ArticleAnalyticsRepository analyticsRepository;
    @Mock SavedArticleRepository savedArticleRepository;
    @Mock TagService tagService;
    @Mock NotificationService notificationService;
    @Mock MailConfig mailConfig;

    ArticleService service;

    @BeforeEach
    void setUp() {
        HtmlUtils html = new HtmlUtils();
        service = new ArticleService(articleRepository, categoryRepository, analyticsRepository, savedArticleRepository,
                tagService, notificationService, mailConfig, html, new TextUtils(html));
        lenient().when(articleRepository.save(any(Article.class))).thenAnswer(i -> i.getArgument(0));
    }

    private User user(long id, Role role) {
        User u = new User();
        u.setId(id);
        u.setName("User " + id);
        u.setRole(role);
        return u;
    }

    private ArticleDto dto(ArticleStatus status) {
        ArticleDto d = new ArticleDto();
        d.setTitle("My First Story");
        d.setContent("<p>Hello world</p>");
        d.setStatus(status);
        return d;
    }

    @Test
    void authorCannotPublishDirectly() {
        Article a = service.create(dto(ArticleStatus.PUBLISHED), user(1, Role.AUTHOR));
        assertEquals(ArticleStatus.PENDING_REVIEW, a.getStatus());
        assertNull(a.getPublishedAt());
    }

    @Test
    void editorPublishesAndSubscribersAreNotified() {
        when(mailConfig.isNotifyOnPublish()).thenReturn(true);
        Article a = service.create(dto(ArticleStatus.PUBLISHED), user(2, Role.EDITOR));
        assertEquals(ArticleStatus.PUBLISHED, a.getStatus());
        assertNotNull(a.getPublishedAt());
        verify(notificationService).notifyNewArticle(eq("My First Story"), anyString(), anyString(), anyString());
    }

    @Test
    void scheduledInThePastPublishesImmediately() {
        ArticleDto d = dto(ArticleStatus.SCHEDULED);
        d.setScheduledAt(java.time.LocalDateTime.now().minusHours(1));
        assertEquals(ArticleStatus.PUBLISHED, service.create(d, user(2, Role.ADMIN)).getStatus());
    }

    @Test
    void scriptTagsAreStrippedFromContent() {
        ArticleDto d = dto(ArticleStatus.DRAFT);
        d.setContent("<p>Hi</p><script>alert(1)</script><img src=x onerror=alert(2)>");
        Article a = service.create(d, user(1, Role.AUTHOR));
        assertFalse(a.getContent().contains("script"));
        assertFalse(a.getContent().contains("onerror"));
    }

    @Test
    void readingTimeAndExcerptAreGenerated() {
        Article a = service.create(dto(ArticleStatus.DRAFT), user(1, Role.AUTHOR));
        assertEquals(1, a.getReadingTime());
        assertEquals("Hello world", a.getExcerpt());
    }

    @Test
    void authorCannotEditSomeoneElsesArticle() {
        Article existing = new Article();
        existing.setId(5L);
        existing.setAuthor(user(1, Role.AUTHOR));
        when(articleRepository.findById(5L)).thenReturn(java.util.Optional.of(existing));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.update(5L, dto(ArticleStatus.DRAFT), user(9, Role.AUTHOR)));
    }
}
