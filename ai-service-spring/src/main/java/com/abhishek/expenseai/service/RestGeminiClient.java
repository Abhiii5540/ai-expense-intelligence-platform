package com.abhishek.expenseai.service;

import com.abhishek.expenseai.config.GeminiProperties;
import com.abhishek.expenseai.error.GeminiConfigurationException;
import com.abhishek.expenseai.error.InsightsGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class RestGeminiClient implements GeminiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestGeminiClient.class);
    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_RETRY_DELAY_MILLIS = 500;

    private final RestClient restClient;
    private final GeminiProperties properties;

    public RestGeminiClient(RestClient restClient, GeminiProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public String generate(String systemInstruction, String prompt) {
        validateConfiguration();
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return executeRequest(systemInstruction, prompt);
            } catch (RestClientResponseException exception) {
                int status = exception.getStatusCode().value();
                if (isTransient(status) && attempt < MAX_ATTEMPTS) {
                    LOGGER.warn(
                        "Gemini API returned status {}; retrying ({}/{})",
                        status,
                        attempt + 1,
                        MAX_ATTEMPTS
                    );
                    pauseBeforeRetry(attempt);
                    continue;
                }
                LOGGER.error("Gemini API returned status {}", status);
                throw new InsightsGenerationException("Gemini API request failed", exception);
            } catch (ResourceAccessException exception) {
                if (attempt < MAX_ATTEMPTS) {
                    LOGGER.warn(
                        "Gemini API request could not be completed; retrying ({}/{})",
                        attempt + 1,
                        MAX_ATTEMPTS
                    );
                    pauseBeforeRetry(attempt);
                    continue;
                }
                LOGGER.error("Gemini API request could not be completed");
                throw new InsightsGenerationException("Gemini API request failed", exception);
            }
        }
        throw new InsightsGenerationException("Gemini API request failed");
    }

    private String executeRequest(String systemInstruction, String prompt) {
        JsonNode response = restClient.post()
            .uri(uriBuilder -> uriBuilder
                .path("/v1beta/models/{model}:generateContent")
                .build(properties.getModel()))
            .header("x-goog-api-key", properties.getApiKey())
            .contentType(MediaType.APPLICATION_JSON)
            .body(requestBody(systemInstruction, prompt))
            .retrieve()
            .body(JsonNode.class);
        return extractText(response);
    }

    private boolean isTransient(int status) {
        return status == 429 || status >= 500 && status <= 599;
    }

    private void pauseBeforeRetry(int attempt) {
        try {
            Thread.sleep(INITIAL_RETRY_DELAY_MILLIS * (1L << (attempt - 1)));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new InsightsGenerationException("Gemini API request was interrupted", exception);
        }
    }

    private Map<String, Object> requestBody(String systemInstruction, String prompt) {
        Map<String, Object> schema = Map.of(
            "type", "ARRAY",
            "items", Map.of("type", "STRING")
        );
        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", 0.9);
        generationConfig.put("response_mime_type", "application/json");
        generationConfig.put("response_schema", schema);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("system_instruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
        body.put(
            "contents",
            List.of(Map.of("role", "user", "parts", List.of(Map.of("text", prompt))))
        );
        body.put("generationConfig", generationConfig);
        return body;
    }

    private String extractText(JsonNode response) {
        if (response == null) {
            throw new InsightsGenerationException("Gemini returned no text content");
        }

        JsonNode parts = response.path("candidates").path(0).path("content").path("parts");
        if (!parts.isArray()) {
            throw new InsightsGenerationException("Gemini returned no text content");
        }

        StringBuilder text = new StringBuilder();
        parts.forEach(part -> {
            String value = part.path("text").asText("");
            if (!value.isBlank()) {
                text.append(value);
            }
        });
        if (text.isEmpty()) {
            throw new InsightsGenerationException("Gemini returned no text content");
        }
        return text.toString();
    }

    private void validateConfiguration() {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new GeminiConfigurationException("GEMINI_API_KEY is not configured");
        }
        if (properties.getModel() == null || properties.getModel().isBlank()) {
            throw new GeminiConfigurationException("GEMINI_MODEL is not configured");
        }
    }
}
