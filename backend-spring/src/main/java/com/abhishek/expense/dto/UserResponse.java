package com.abhishek.expense.dto;

import com.abhishek.expense.domain.User;
import com.abhishek.expense.util.DateTimes;

public record UserResponse(
    String id,
    String name,
    String email,
    String createdAt,
    String updatedAt
) {

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            DateTimes.toApiTimestamp(user.getCreatedAt()),
            DateTimes.toApiTimestamp(user.getUpdatedAt())
        );
    }

}
