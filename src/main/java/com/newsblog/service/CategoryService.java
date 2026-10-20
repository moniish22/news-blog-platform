package com.newsblog.service;

import com.newsblog.dto.CategoryDto;
import com.newsblog.model.Category;
import com.newsblog.repository.ArticleRepository;
import com.newsblog.repository.CategoryRepository;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.ResourceNotFoundException;
import com.newsblog.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    /** All categories with their published-article counts filled in. */
    @Transactional(readOnly = true)
    public List<Category> withCounts() {
        List<Category> list = categoryRepository.findAllByOrderByNameAsc();
        list.forEach(c -> c.setArticleCount(articleRepository.countByCategoryAndStatus(c, ArticleStatus.PUBLISHED)));
        return list;
    }

    @Transactional(readOnly = true)
    public List<Category> findAll() { return categoryRepository.findAllByOrderByNameAsc(); }

    @Transactional(readOnly = true)
    public Category getBySlug(String slug) {
        return categoryRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Transactional
    public Category create(CategoryDto dto) {
        String name = dto.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("A category named \"" + name + "\" already exists.");
        }
        Category c = new Category();
        c.setName(name);
        c.setDescription(dto.getDescription() == null || dto.getDescription().isBlank() ? null : dto.getDescription().trim());
        c.setSlug(SlugGenerator.unique(name, categoryRepository::existsBySlug));
        return categoryRepository.save(c);
    }

    @Transactional
    public void delete(Long id) {
        Category c = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (articleRepository.countByCategory(c) > 0) {
            throw new IllegalStateException("Move or delete the articles in \"" + c.getName() + "\" before deleting it.");
        }
        categoryRepository.delete(c);
    }
}
