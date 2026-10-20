package com.newsblog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tags")
@Getter @Setter @NoArgsConstructor
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Number of published articles; filled in by TagService, not stored. */
    @Transient
    private long articleCount;

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
