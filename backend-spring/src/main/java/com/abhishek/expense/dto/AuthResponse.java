package com.abhishek.expense.dto;

public record AuthResponse(UserResponse user, String token) {
}
