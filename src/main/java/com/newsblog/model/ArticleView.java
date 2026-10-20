package com.newsblog.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/** One page view; the raw data behind trending articles and the views-per-day chart. */
@Entity
@Table(name = "article_views", indexes = @Index(name = "idx_view_time", columnList = "viewed_at"))
@Getter @Setter @NoArgsConstructor
public class ArticleView {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @PrePersist void onCreate() { if (viewedAt == null) viewedAt = LocalDateTime.now(); }
}
