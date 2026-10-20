package com.newsblog.service;

import com.newsblog.config.MailConfig;
import com.newsblog.dto.CommentDto;
import com.newsblog.model.*;
import com.newsblog.repository.ArticleRepository;
import com.newsblog.repository.CommentRepository;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.HtmlUtils;
import com.newsblog.util.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Nested comments with moderation.
 * Staff (ADMIN/EDITOR/AUTHOR) are auto-approved; readers' comments wait in PENDING until a moderator approves them.
 * Replies are limited to two levels: a reply to a reply is attached to the top-level comment.
 */
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final NotificationService notificationService;
    private final MailConfig mailConfig;
    private final HtmlUtils htmlUtils;

    @Transactional
    public Comment add(CommentDto dto, User user) {
        Article article = articleRepository.findById(dto.getArticleId())
                .filter(a -> a.getStatus() == ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("Comments are only open on published articles."));
        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .filter(p -> p.getArticle().getId().equals(article.getId()) && p.getStatus() == CommentStatus.APPROVED)
                    .orElseThrow(() -> new IllegalArgumentException("The comment you replied to is no longer available."));
            if (parent.getParent() != null) parent = parent.getParent();
        }
        String text = htmlUtils.plainText(dto.getComment()).trim();   // comments are plain text only
        if (text.isEmpty()) throw new IllegalArgumentException("Comment cannot be empty.");

        Comment c = new Comment();
        c.setArticle(article);
        c.setUser(user);
        c.setParent(parent);
        c.setComment(text);
        boolean trusted = user.getRole() != Role.READER || mailConfig.isAutoApproveReaders();
        c.setStatus(trusted ? CommentStatus.APPROVED : CommentStatus.PENDING);
        c = commentRepository.save(c);
        if (trusted) notifyAuthor(c);
        return c;
    }

    @Transactional(readOnly = true)
    public List<Comment> approvedFor(Article article) {
        return commentRepository.findByArticleAndParentIsNullAndStatusOrderByCreatedAtDesc(article, CommentStatus.APPROVED);
    }

    @Transactional(readOnly = true)
    public Page<Comment> byStatus(CommentStatus status, Pageable p) { return commentRepository.findByStatus(status, p); }

    @Transactional(readOnly = true)
    public Page<Comment> byUser(User user, Pageable p) { return commentRepository.findByUser(user, p); }

    @Transactional(readOnly = true)
    public List<Comment> recent() { return commentRepository.findTop5ByOrderByCreatedAtDesc(); }

    public long count(CommentStatus status) { return commentRepository.countByStatus(status); }

    @Transactional
    public void approve(Long id) {
        Comment c = get(id);
        boolean wasApproved = c.getStatus() == CommentStatus.APPROVED;
        c.setStatus(CommentStatus.APPROVED);
        commentRepository.save(c);
        if (!wasApproved) notifyAuthor(c);
    }

    @Transactional
    public void reject(Long id) {
        Comment c = get(id);
        c.setStatus(CommentStatus.REJECTED);
        commentRepository.save(c);
    }

    @Transactional
    public void delete(Long id) { commentRepository.delete(get(id)); }

    private Comment get(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }

    /** Emails the article's author when someone else's comment becomes visible. */
    private void notifyAuthor(Comment c) {
        User author = c.getArticle().getAuthor();
        if (!author.getId().equals(c.getUser().getId())) {
            notificationService.notifyCommentApproved(author.getEmail(), c.getArticle().getTitle(),
                    c.getArticle().getSlug(), c.getUser().getName(), c.getComment());
        }
    }
}
