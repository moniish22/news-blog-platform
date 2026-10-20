package com.newsblog.model;

import com.newsblog.util.ArticleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A news/blog article. Tags are linked through the {@code article_tags} join table.
 * For SCHEDULED articles {@code publishedAt} holds the future publish time.
 */
@Entity
@Table(name = "articles", indexes = {
        @Index(name = "idx_article_status", columnList = "status"),
        @Index(name = "idx_article_published", columnList = "published_at")})
@Getter @Setter @NoArgsConstructor
public class Article {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 500)
    private String excerpt;

    @Column(name = "featured_image", length = 1000)
    private String featuredImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    private long views;
    private long likes;
    private long shares;

    @Column(name = "reading_time")
    private int readingTime = 1;

    private boolean featured;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate void onUpdate() { updatedAt = LocalDateTime.now(); }
}
