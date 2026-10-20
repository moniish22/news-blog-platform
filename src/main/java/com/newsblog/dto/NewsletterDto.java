package com.newsblog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NewsletterDto {
    @NotBlank(message = "Subject is required")
    @Size(max = 200)
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;
}
