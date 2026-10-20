package com.newsblog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CommentDto {
    @NotNull
    private Long articleId;

    /** Id of the comment being replied to (null for a top-level comment). */
    private Long parentId;

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 2000, message = "Comment is too long (max 2000 characters)")
    private String comment;
}
