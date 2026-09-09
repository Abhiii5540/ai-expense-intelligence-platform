package com.abhishek.expenseai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.abhishek.expenseai.error.InsightsGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class InsightParserTest {

    private final InsightParser parser = new InsightParser(new ObjectMapper());

    @Test
    void parsesAtMostFiveInsightsAndSanitizesCurrency() {
        String response = """
            ```json
            ["Food cost $500", "Travel cost USD 900", "Bills were 300 U.S. dollars", "Fourth", "Fifth", "Sixth"]
            ```
            """;

        List<String> insights = parser.parse(response);

        assertThat(insights).hasSize(5);
        assertThat(insights).allMatch(insight -> !insight.contains("$") && !insight.contains("USD"));
        assertThat(insights.getFirst()).contains("₹500");
    }

    @Test
    void rejectsTooFewInsights() {
        assertThatThrownBy(() -> parser.parse("[\"One\", \"Two\"]"))
            .isInstanceOf(InsightsGenerationException.class)
            .hasMessageContaining("at least 3");
    }
}
