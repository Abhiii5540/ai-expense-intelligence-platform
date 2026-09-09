package com.abhishek.expense.service;

import com.abhishek.expense.domain.Expense;
import com.abhishek.expense.dto.AiExpense;
import com.abhishek.expense.dto.AiInsightsRequest;
import com.abhishek.expense.dto.InsightsResponse;
import com.abhishek.expense.error.ApiException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AiInsightsService {

    private final ExpenseService expenseService;
    private final AiInsightsClient aiInsightsClient;

    public AiInsightsService(ExpenseService expenseService, AiInsightsClient aiInsightsClient) {
        this.expenseService = expenseService;
        this.aiInsightsClient = aiInsightsClient;
    }

    public InsightsResponse generate(String userId) {
        List<Expense> expenses = expenseService.listAllForUser(userId);
        if (expenses.isEmpty()) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "At least one expense is required to generate insights"
            );
        }

        List<AiExpense> items = expenses.stream()
            .map(expense -> new AiExpense(
                expense.getAmount(),
                expense.getCategory() == null || expense.getCategory().isBlank()
                    ? "Uncategorized"
                    : expense.getCategory(),
                expense.getDescription() == null ? "" : expense.getDescription()
            ))
            .toList();
        return aiInsightsClient.generate(new AiInsightsRequest(items));
    }
}
