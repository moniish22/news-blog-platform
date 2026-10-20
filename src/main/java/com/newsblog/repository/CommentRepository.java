package com.newsblog.repository;

import com.newsblog.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleAndParentIsNullAndStatusOrderByCreatedAtDesc(Article article, CommentStatus status);
    Page<Comment> findByStatus(CommentStatus status, Pageable pageable);
    Page<Comment> findByUser(User user, Pageable pageable);
    List<Comment> findTop5ByOrderByCreatedAtDesc();
    long countByStatus(CommentStatus status);
    long countByArticleAndStatus(Article article, CommentStatus status);

    @Query("select count(c) from Comment c where c.article.author = :author")
    long countByArticleAuthor(@Param("author") User author);
}
