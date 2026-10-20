package com.newsblog.service;

import com.newsblog.config.MailConfig;
import com.newsblog.dto.CommentDto;
import com.newsblog.model.*;
import com.newsblog.repository.ArticleRepository;
import com.newsblog.repository.CommentRepository;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.HtmlUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock CommentRepository commentRepository;
    @Mock ArticleRepository articleRepository;
    @Mock NotificationService notificationService;
    @Mock MailConfig mailConfig;

    CommentService service;
    Article article;

    @BeforeEach
    void setUp() {
        service = new CommentService(commentRepository, articleRepository, notificationService, mailConfig, new HtmlUtils());
        User author = user(1, Role.AUTHOR);
        article = new Article();
        article.setId(10L);
        article.setTitle("T");
        article.setSlug("t");
        article.setAuthor(author);
        article.setStatus(ArticleStatus.PUBLISHED);
        lenient().when(articleRepository.findById(10L)).thenReturn(Optional.of(article));
        lenient().when(commentRepository.save(any(Comment.class))).thenAnswer(i -> i.getArgument(0));
    }

    private User user(long id, Role role) {
        User u = new User();
        u.setId(id);
        u.setName("U" + id);
        u.setEmail("u" + id + "@x.com");
        u.setRole(role);
        return u;
    }

    private CommentDto dto(String text, Long parentId) {
        CommentDto d = new CommentDto();
        d.setArticleId(10L);
        d.setParentId(parentId);
        d.setComment(text);
        return d;
    }

    @Test
    void readerCommentWaitsForModeration() {
        Comment c = service.add(dto("Nice!", null), user(2, Role.READER));
        assertEquals(CommentStatus.PENDING, c.getStatus());
        verifyNoInteractions(notificationService);
    }

    @Test
    void staffCommentIsApprovedAndAuthorIsNotified() {
        Comment c = service.add(dto("Thanks all", null), user(3, Role.EDITOR));
        assertEquals(CommentStatus.APPROVED, c.getStatus());
        verify(notificationService).notifyCommentApproved(eq("u1@x.com"), eq("T"), eq("t"), eq("U3"), eq("Thanks all"));
    }

    @Test
    void htmlInCommentsIsStripped() {
        Comment c = service.add(dto("<b>Hi</b><script>x()</script>", null), user(2, Role.READER));
        assertEquals("Hi", c.getComment());
    }

    @Test
    void replyToReplyIsAttachedToTopLevelComment() {
        Comment top = new Comment();
        top.setId(1L); top.setArticle(article); top.setStatus(CommentStatus.APPROVED);
        Comment reply = new Comment();
        reply.setId(2L); reply.setArticle(article); reply.setStatus(CommentStatus.APPROVED); reply.setParent(top);
        when(commentRepository.findById(2L)).thenReturn(Optional.of(reply));
        Comment c = service.add(dto("Re: re", 2L), user(2, Role.READER));
        assertSame(top, c.getParent());
    }

    @Test
    void cannotCommentOnUnpublishedArticle() {
        article.setStatus(ArticleStatus.DRAFT);
        assertThrows(IllegalArgumentException.class, () -> service.add(dto("Hi", null), user(2, Role.READER)));
    }
}
