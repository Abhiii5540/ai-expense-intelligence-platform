package com.abhishek.expense.security;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Unauthorized");
    }

    public InvalidTokenException(Throwable cause) {
        super("Unauthorized", cause);
    }
}
