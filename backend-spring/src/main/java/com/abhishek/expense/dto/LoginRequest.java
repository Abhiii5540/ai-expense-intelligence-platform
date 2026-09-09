package com.abhishek.expense.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
    @NotBlank(message = "Invalid email") @Email(message = "Invalid email") String email,
    @NotEmpty(message = "Password is required") String password
) {
}
