package com.newsblog.service;

import com.newsblog.model.Tag;
import com.newsblog.repository.ArticleRepository;
import com.newsblog.repository.TagRepository;
import com.newsblog.util.ArticleStatus;
import com.newsblog.util.ResourceNotFoundException;
import com.newsblog.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<Tag> withCounts() {
        List<Tag> tags = tagRepository.findAllByOrderByNameAsc();
        tags.forEach(t -> t.setArticleCount(articleRepository.countByTagAndStatus(t, ArticleStatus.PUBLISHED)));
        return tags;
    }

    /** Tags that have at least one published article, most used first. */
    @Transactional(readOnly = true)
    public List<Tag> popular(int limit) {
        return withCounts().stream()
                .filter(t -> t.getArticleCount() > 0)
                .sorted(Comparator.comparingLong(Tag::getArticleCount).reversed())
                .limit(limit).toList();
    }

    @Transactional(readOnly = true)
    public Tag getBySlug(String slug) {
        return tagRepository.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
    }

    /** Turns "java, Spring Boot" into persisted Tag entities, creating the missing ones. */
    @Transactional
    public Set<Tag> resolve(String csv) {
        Set<Tag> result = new HashSet<>();
        if (csv == null || csv.isBlank()) return result;
        Set<String> seen = new HashSet<>();
        for (String raw : csv.split(",")) {
            String name = raw.trim();
            if (name.isEmpty() || name.length() > 50 || result.size() >= 10) continue;
            String slug = SlugGenerator.slugify(name);
            if (!seen.add(slug)) continue;
            result.add(tagRepository.findBySlug(slug).orElseGet(() -> {
                Tag t = new Tag();
                t.setName(name);
                t.setSlug(slug);
                return tagRepository.save(t);
            }));
        }
        return result;
    }

    @Transactional
    public Tag create(String name) {
        Set<Tag> tags = resolve(name);
        if (tags.isEmpty()) throw new IllegalArgumentException("Enter a tag name.");
        return tags.iterator().next();
    }

    /** Deleting a tag also removes it from articles (join rows are owned by Article). */
    @Transactional
    public void delete(Long id) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        articleRepository.findAll().stream().filter(a -> a.getTags().remove(tag)).forEach(articleRepository::save);
        tagRepository.delete(tag);
    }
}
