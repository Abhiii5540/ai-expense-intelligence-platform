package com.abhishek.expense.dto;

import java.math.BigDecimal;
import java.util.List;

public record AnalyticsResponse(
    Summary summary,
    List<MonthlyTotal> monthlyTrend,
    List<CategoryTotal> categoryDistribution,
    List<CategoryTotal> topCategories
) {

    public record Summary(
        BigDecimal totalAmount,
        BigDecimal currentMonthAmount,
        long transactionCount,
        BigDecimal highestExpense,
        BigDecimal averageExpense,
        String mostUsedCategory
    ) {
    }

    public record MonthlyTotal(String month, BigDecimal total) {
    }

    public record CategoryTotal(String category, BigDecimal total, long count) {
    }
}
