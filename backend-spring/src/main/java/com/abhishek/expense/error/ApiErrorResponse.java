package com.abhishek.expense.error;

public record ApiErrorResponse(boolean success, ErrorDetail error) {

    public static ApiErrorResponse of(String message, String requestId) {
        return new ApiErrorResponse(false, new ErrorDetail(message, requestId));
    }

    public record ErrorDetail(String message, String requestId) {
    }
}
