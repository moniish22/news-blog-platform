package com.newsblog.repository;

import com.newsblog.model.Article;
import com.newsblog.model.ArticleAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleAnalyticsRepository extends JpaRepository<ArticleAnalytics, Long> {
    Optional<ArticleAnalytics> findByArticle(Article article);
    Optional<ArticleAnalytics> findByArticleId(Long articleId);
}
