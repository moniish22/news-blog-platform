package com.newsblog.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/** Aggregated statistics for one article (one row per article). */
@Entity
@Table(name = "article_analytics")
@Getter @Setter @NoArgsConstructor
public class ArticleAnalytics {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "article_id", unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Article article;

    private long views;

    @Column(name = "unique_views")
    private long uniqueViews;

    private long shares;

    /** Average seconds spent on the page. */
    @Column(name = "avg_read_time")
    private double avgReadTime;

    /** Percentage of visits that left within 10 seconds. */
    @Column(name = "bounce_rate")
    private double bounceRate;

    /** Internal counters used to keep the running average and bounce rate. */
    private int readSamples;
    private int bounces;
}
