package com.newsblog.repository;

import com.newsblog.model.Article;
import com.newsblog.model.ArticleView;
import com.newsblog.util.ArticleStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {
    boolean existsByArticleAndIpAddress(Article article, String ipAddress);
    long countByViewedAtAfter(LocalDateTime since);

    /** Ids of the most-viewed published articles since a point in time (the trending algorithm). */
    @Query("select v.article.id from ArticleView v where v.viewedAt >= :since and v.article.status = :status "
            + "group by v.article.id order by count(v) desc")
    List<Long> trendingIds(@Param("since") LocalDateTime since, @Param("status") ArticleStatus status, Pageable pageable);

    @Query("select v.viewedAt from ArticleView v where v.viewedAt >= :since")
    List<LocalDateTime> viewTimesSince(@Param("since") LocalDateTime since);
}
