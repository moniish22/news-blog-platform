package com.newsblog.repository;

import com.newsblog.model.Article;
import com.newsblog.model.SavedArticle;
import com.newsblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedArticleRepository extends JpaRepository<SavedArticle, Long> {
    List<SavedArticle> findByUserOrderBySavedAtDesc(User user);
    Optional<SavedArticle> findByUserAndArticle(User user, Article article);
    boolean existsByUserAndArticle(User user, Article article);
}
