package com.newsblog.dto;

import com.newsblog.util.ArticleStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/** Create/edit article form. {@code tagNames} is a comma separated string. */
@Data
public class ArticleDto {
    @NotBlank(message = "Title is required")
    @Size(max = 255)
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 500, message = "Excerpt must be at most 500 characters")
    private String excerpt;

    @Size(max = 1000)
    private String featuredImage;

    private Long categoryId;
    private String tagNames;
    private ArticleStatus status = ArticleStatus.DRAFT;
    private boolean featured;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledAt;
}
