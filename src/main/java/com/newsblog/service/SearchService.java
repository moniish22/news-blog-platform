package com.newsblog.service;

import com.newsblog.model.Article;
import com.newsblog.repository.ArticleRepository;
import com.newsblog.util.ArticleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Full-text style search over title, excerpt, body and tag names of published articles. */
@Service
@RequiredArgsConstructor
public class SearchService {
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<Article> search(String query, Long categoryId, Pageable pageable) {
        String q = query == null ? "" : query.trim();
        if (q.isEmpty() && categoryId == null) return articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageable);
        return articleRepository.search(ArticleStatus.PUBLISHED, q, categoryId, pageable);
    }
}
