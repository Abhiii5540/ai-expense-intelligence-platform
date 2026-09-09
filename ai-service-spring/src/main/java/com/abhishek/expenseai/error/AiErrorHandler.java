package com.abhishek.expenseai.error;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AiErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiErrorHandler.class);

    @ExceptionHandler(GeminiConfigurationException.class)
    ResponseEntity<Map<String, String>> handleConfiguration(GeminiConfigurationException exception) {
        return detail(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
    }

    @ExceptionHandler(InsightsGenerationException.class)
    ResponseEntity<Map<String, String>> handleGeneration(InsightsGenerationException exception) {
        return detail(HttpStatus.BAD_GATEWAY, exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Map<String, String>> handleValidation(Exception exception) {
        return detail(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid expense data");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
        LOGGER.error("Unhandled AI service error", exception);
        return detail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ResponseEntity<Map<String, String>> detail(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("detail", message));
    }
}
