package com.newsblog.repository;

import com.newsblog.model.Article;
import com.newsblog.model.Category;
import com.newsblog.model.Tag;
import com.newsblog.model.User;
import com.newsblog.util.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findBySlug(String slug);
    boolean existsBySlug(String slug);

    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);
    Page<Article> findByStatusAndCategory(ArticleStatus status, Category category, Pageable pageable);
    Page<Article> findByStatusAndPublishedAtBetween(ArticleStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<Article> findByAuthor(User author, Pageable pageable);
    Page<Article> findByAuthorAndStatus(User author, ArticleStatus status, Pageable pageable);
    List<Article> findByStatus(ArticleStatus status);
    List<Article> findByStatusAndPublishedAtBefore(ArticleStatus status, LocalDateTime time);

    List<Article> findTop5ByStatusAndFeaturedTrueOrderByPublishedAtDesc(ArticleStatus status);
    List<Article> findTop5ByStatusOrderByViewsDesc(ArticleStatus status);
    List<Article> findTop5ByOrderByCreatedAtDesc();
    List<Article> findTop4ByStatusAndCategoryAndIdNotOrderByPublishedAtDesc(ArticleStatus status, Category category, Long id);

    long countByStatus(ArticleStatus status);
    long countByAuthor(User author);
    long countByCategory(Category category);
    long countByCategoryAndStatus(Category category, ArticleStatus status);

    @Query("select a from Article a join a.tags t where t = :tag and a.status = :status")
    Page<Article> findByTagAndStatus(@Param("tag") Tag tag, @Param("status") ArticleStatus status, Pageable pageable);

    @Query("select count(a) from Article a join a.tags t where t = :tag and a.status = :status")
    long countByTagAndStatus(@Param("tag") Tag tag, @Param("status") ArticleStatus status);

    @Query("select distinct a from Article a left join a.tags t where a.status = :status "
            + "and (:categoryId is null or a.category.id = :categoryId) "
            + "and (lower(a.title) like lower(concat('%', :q, '%')) "
            + "or lower(a.excerpt) like lower(concat('%', :q, '%')) "
            + "or lower(a.content) like lower(concat('%', :q, '%')) "
            + "or lower(t.name) like lower(concat('%', :q, '%')))")
    Page<Article> search(@Param("status") ArticleStatus status, @Param("q") String q,
                         @Param("categoryId") Long categoryId, Pageable pageable);

    @Query("select coalesce(sum(a.views), 0) from Article a")
    long sumViews();

    @Query("select coalesce(sum(a.views), 0) from Article a where a.author = :author")
    long sumViewsByAuthor(@Param("author") User author);

    @Modifying @Query("update Article a set a.views = a.views + 1 where a.id = :id")
    void incrementViews(@Param("id") Long id);

    @Modifying @Query("update Article a set a.likes = a.likes + 1 where a.id = :id")
    void incrementLikes(@Param("id") Long id);

    @Modifying @Query("update Article a set a.shares = a.shares + 1 where a.id = :id")
    void incrementShares(@Param("id") Long id);
}
