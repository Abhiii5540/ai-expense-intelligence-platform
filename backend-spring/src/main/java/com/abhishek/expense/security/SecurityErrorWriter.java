package com.abhishek.expense.security;

import com.abhishek.expense.error.ApiErrorResponse;
import com.abhishek.expense.error.RequestIdFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;

final class SecurityErrorWriter {

    private SecurityErrorWriter() {
    }

    static void write(
        ObjectMapper objectMapper,
        HttpServletRequest request,
        HttpServletResponse response,
        int status,
        String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String requestId = (String) request.getAttribute(RequestIdFilter.REQUEST_ID_ATTRIBUTE);
        objectMapper.writeValue(response.getOutputStream(), ApiErrorResponse.of(message, requestId));
    }
}
