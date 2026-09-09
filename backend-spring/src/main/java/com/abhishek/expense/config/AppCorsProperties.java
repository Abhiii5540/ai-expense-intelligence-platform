package com.abhishek.expense.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cors")
public class AppCorsProperties {

    private List<String> allowedOrigins = new ArrayList<>(
        List.of("http://127.0.0.1:5173", "http://localhost:5173")
    );

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins == null ? new ArrayList<>() : allowedOrigins;
    }
}
