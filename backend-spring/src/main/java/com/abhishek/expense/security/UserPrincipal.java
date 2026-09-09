package com.abhishek.expense.security;

import com.abhishek.expense.domain.User;

public record UserPrincipal(String id, String name, String email) {

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user.getId(), user.getName(), user.getEmail());
    }
}
