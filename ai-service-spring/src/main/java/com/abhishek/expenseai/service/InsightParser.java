package com.abhishek.expenseai.service;

import com.abhishek.expenseai.error.InsightsGenerationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class InsightParser {

    private static final int MIN_INSIGHTS = 3;
    private static final int MAX_INSIGHTS = 5;
    private static final Pattern CODE_BLOCK = Pattern.compile("```(?:json)?\\s*([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOLLARS = Pattern.compile("\\bU\\.?S\\.?\\s*dollars?\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern USD = Pattern.compile("\\bUSD\\b", Pattern.CASE_INSENSITIVE);

    private final ObjectMapper objectMapper;

    public InsightParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<String> parse(String rawText) {
        String text = rawText == null ? "" : rawText.trim();
        if (text.isBlank()) {
            throw new InsightsGenerationException("Gemini returned an empty response");
        }

        Matcher codeBlock = CODE_BLOCK.matcher(text);
        if (codeBlock.find()) {
            text = codeBlock.group(1).trim();
        }

        try {
            JsonNode payload = objectMapper.readTree(text);
            JsonNode insightsNode = payload.isArray() ? payload : payload.path("insights");
            if (!insightsNode.isArray()) {
                throw new InsightsGenerationException("Gemini response must be a JSON array of insights");
            }

            List<String> insights = new ArrayList<>();
            insightsNode.forEach(node -> {
                String insight = sanitize(node.asText("").trim());
                if (!insight.isBlank()) {
                    insights.add(insight);
                }
            });
            if (insights.size() < MIN_INSIGHTS) {
                throw new InsightsGenerationException(
                    "Expected at least " + MIN_INSIGHTS + " insights, received " + insights.size()
                );
            }
            return List.copyOf(insights.subList(0, Math.min(insights.size(), MAX_INSIGHTS)));
        } catch (InsightsGenerationException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InsightsGenerationException("Failed to parse Gemini response as JSON", exception);
        }
    }

    private String sanitize(String insight) {
        String text = DOLLARS.matcher(insight).replaceAll("₹");
        text = USD.matcher(text).replaceAll("₹");
        return text.replace("$", "₹");
    }
}
