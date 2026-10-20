package com.newsblog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/** Registration form. */
@Data
public class UserDto {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 72, message = "Password must be 6-72 characters")
    private String password;
}
