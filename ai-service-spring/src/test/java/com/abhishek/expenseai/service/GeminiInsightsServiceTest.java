package com.abhishek.expenseai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.abhishek.expenseai.dto.ExpenseInput;
import com.abhishek.expenseai.error.InsightsGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeminiInsightsServiceTest {

    @Mock
    private GeminiClient geminiClient;

    @Test
    void returnsParsedStructuredInsights() {
        when(geminiClient.generate(anyString(), anyString()))
            .thenReturn("[\"One\", \"Two\", \"Three\"]");
        GeminiInsightsService service = new GeminiInsightsService(
            new PromptBuilder(),
            geminiClient,
            new InsightParser(new ObjectMapper())
        );

        List<String> insights = service.generate(
            List.of(new ExpenseInput(new BigDecimal("100.00"), "Food", "Lunch"))
        );

        assertThat(insights).containsExactly("One", "Two", "Three");
    }

    @Test
    void rejectsAnEmptyExpenseListBeforeCallingGemini() {
        GeminiInsightsService service = new GeminiInsightsService(
            new PromptBuilder(),
            geminiClient,
            new InsightParser(new ObjectMapper())
        );

        assertThatThrownBy(() -> service.generate(List.of()))
            .isInstanceOf(InsightsGenerationException.class)
            .hasMessage("At least one expense is required");
    }
}
