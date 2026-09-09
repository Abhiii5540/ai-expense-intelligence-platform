package com.abhishek.expenseai.error;

public class InsightsGenerationException extends RuntimeException {

    public InsightsGenerationException(String message) {
        super(message);
    }

    public InsightsGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
