package com.abhishek.expense.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupRequest(
    String name,
    @NotBlank(message = "Invalid email") @Email(message = "Invalid email") String email,
    @NotNull(message = "Password must be at least 6 characters")
    @Size(min = 6, message = "Password must be at least 6 characters") String password
) {
}
