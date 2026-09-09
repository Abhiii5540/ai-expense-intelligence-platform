package com.abhishek.expense.dto;

import java.util.List;

public record ExpensePage(
    List<ExpenseResponse> items,
    int page,
    int limit,
    long total,
    int totalPages
) {
}
