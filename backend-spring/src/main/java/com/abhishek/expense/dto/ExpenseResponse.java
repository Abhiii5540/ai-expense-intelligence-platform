package com.abhishek.expense.dto;

import com.abhishek.expense.domain.Expense;
import com.abhishek.expense.util.DateTimes;
import java.math.BigDecimal;

public record ExpenseResponse(
    String id,
    BigDecimal amount,
    String description,
    String category,
    String expenseDate,
    String createdAt,
    String updatedAt,
    String userId
) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
            expense.getId(),
            expense.getAmount(),
            expense.getDescription(),
            expense.getCategory(),
            DateTimes.toApiTimestamp(expense.getExpenseDate()),
            DateTimes.toApiTimestamp(expense.getCreatedAt()),
            DateTimes.toApiTimestamp(expense.getUpdatedAt()),
            expense.getUser().getId()
        );
    }
}
