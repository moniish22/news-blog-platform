package com.newsblog.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/** An article in a user's reading list. */
@Entity
@Table(name = "saved_articles", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "article_id"}))
@Getter @Setter @NoArgsConstructor
public class SavedArticle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;

    @Column(name = "saved_at", updatable = false)
    private LocalDateTime savedAt;

    @PrePersist void onCreate() { savedAt = LocalDateTime.now(); }
}
