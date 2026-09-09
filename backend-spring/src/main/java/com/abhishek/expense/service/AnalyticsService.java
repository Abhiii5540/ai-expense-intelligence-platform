package com.abhishek.expense.service;

import com.abhishek.expense.domain.Expense;
import com.abhishek.expense.dto.AnalyticsResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyticsService {

    private final ExpenseService expenseService;

    public AnalyticsService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(String userId) {
        List<Expense> expenses = expenseService.listAllForUser(userId);
        BigDecimal total = expenses.stream()
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        YearMonth currentMonth = YearMonth.now(ZoneOffset.UTC);
        BigDecimal currentMonthTotal = expenses.stream()
            .filter(expense -> YearMonth.from(expense.getExpenseDate()).equals(currentMonth))
            .map(Expense::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal highest = expenses.stream()
            .map(Expense::getAmount)
            .max(Comparator.naturalOrder())
            .orElse(BigDecimal.ZERO);
        BigDecimal average = expenses.isEmpty()
            ? BigDecimal.ZERO.setScale(2)
            : total.divide(BigDecimal.valueOf(expenses.size()), 2, RoundingMode.HALF_UP);

        Map<String, List<Expense>> byCategory = expenses.stream()
            .collect(Collectors.groupingBy(this::categoryLabel));
        String mostUsedCategory = byCategory.entrySet().stream()
            .max(Comparator.<Map.Entry<String, List<Expense>>>comparingInt(entry -> entry.getValue().size())
                .thenComparing(Map.Entry::getKey))
            .map(Map.Entry::getKey)
            .orElse(null);

        List<AnalyticsResponse.CategoryTotal> categoryTotals = byCategory.entrySet().stream()
            .map(entry -> new AnalyticsResponse.CategoryTotal(
                entry.getKey(),
                entry.getValue().stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add),
                entry.getValue().size()
            ))
            .sorted(Comparator.comparing(AnalyticsResponse.CategoryTotal::total).reversed())
            .toList();

        Map<YearMonth, BigDecimal> monthly = new LinkedHashMap<>();
        expenses.stream()
            .sorted(Comparator.comparing(Expense::getExpenseDate))
            .forEach(expense -> monthly.merge(
                YearMonth.from(expense.getExpenseDate()),
                expense.getAmount(),
                BigDecimal::add
            ));
        List<AnalyticsResponse.MonthlyTotal> monthlyTotals = monthly.entrySet().stream()
            .map(entry -> new AnalyticsResponse.MonthlyTotal(entry.getKey().toString(), entry.getValue()))
            .toList();

        return new AnalyticsResponse(
            new AnalyticsResponse.Summary(
                total,
                currentMonthTotal,
                expenses.size(),
                highest,
                average,
                mostUsedCategory
            ),
            monthlyTotals,
            categoryTotals,
            categoryTotals.stream().limit(5).toList()
        );
    }

    private String categoryLabel(Expense expense) {
        return expense.getCategory() == null || expense.getCategory().isBlank()
            ? "Uncategorized"
            : expense.getCategory();
    }
}
