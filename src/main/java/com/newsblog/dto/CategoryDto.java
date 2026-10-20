package com.newsblog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryDto {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;
}
