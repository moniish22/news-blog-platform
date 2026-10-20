package com.newsblog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/** A newsletter campaign (subject + HTML body) and its delivery result. */
@Entity
@Table(name = "newsletters")
@Getter @Setter @NoArgsConstructor
public class Newsletter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    /** SENDING, SENT or FAILED. */
    @Column(length = 20)
    private String status = "SENDING";

    @Column(name = "recipient_count")
    private int recipientCount;
}
