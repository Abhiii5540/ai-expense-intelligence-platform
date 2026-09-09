package com.abhishek.expense.service;

import com.abhishek.expense.dto.AiInsightsRequest;
import com.abhishek.expense.dto.InsightsResponse;
import com.abhishek.expense.error.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SpringAiInsightsClient implements AiInsightsClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public SpringAiInsightsClient(
        @Qualifier("aiRestClient") RestClient restClient,
        ObjectMapper objectMapper
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public InsightsResponse generate(AiInsightsRequest request) {
        try {
            InsightsResponse response = restClient.post()
                .uri("/ai/insights")
                .body(request)
                .retrieve()
                .body(InsightsResponse.class);
            if (response == null || response.insights() == null) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "AI service returned an invalid response");
            }
            return response;
        } catch (ApiException exception) {
            throw exception;
        } catch (ResourceAccessException exception) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "AI service is unavailable", exception);
        } catch (RestClientResponseException exception) {
            throw mapRemoteError(exception);
        }
    }

    private ApiException mapRemoteError(RestClientResponseException exception) {
        int status = exception.getStatusCode().value();
        String detail = extractDetail(exception.getResponseBodyAsString());
        return switch (status) {
            case 503 -> new ApiException(
                HttpStatus.SERVICE_UNAVAILABLE,
                detail == null ? "AI service is not configured" : detail,
                exception
            );
            case 502 -> new ApiException(
                HttpStatus.BAD_GATEWAY,
                detail == null ? "Failed to generate insights" : detail,
                exception
            );
            case 422 -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid expense data sent to AI service", exception);
            default -> new ApiException(
                HttpStatus.BAD_GATEWAY,
                detail == null ? "AI service request failed" : detail,
                exception
            );
        };
    }

    private String extractDetail(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String detail = root.path("detail").asText(null);
            return detail == null || detail.isBlank() ? null : detail;
        } catch (Exception ignored) {
            return null;
        }
    }
}
