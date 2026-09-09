package com.abhishek.expenseai.service;

import com.abhishek.expenseai.dto.ExpenseInput;
import com.abhishek.expenseai.error.InsightsGenerationException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GeminiInsightsService {

    private final PromptBuilder promptBuilder;
    private final GeminiClient geminiClient;
    private final InsightParser insightParser;

    public GeminiInsightsService(
        PromptBuilder promptBuilder,
        GeminiClient geminiClient,
        InsightParser insightParser
    ) {
        this.promptBuilder = promptBuilder;
        this.geminiClient = geminiClient;
        this.insightParser = insightParser;
    }

    public List<String> generate(List<ExpenseInput> expenses) {
        if (expenses.isEmpty()) {
            throw new InsightsGenerationException("At least one expense is required");
        }
        String prompt = promptBuilder.build(expenses);
        String rawResponse = geminiClient.generate(PromptBuilder.SYSTEM_INSTRUCTION, prompt);
        return insightParser.parse(rawResponse);
    }
}
