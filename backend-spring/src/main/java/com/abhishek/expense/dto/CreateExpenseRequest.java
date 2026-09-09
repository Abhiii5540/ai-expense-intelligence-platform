package com.abhishek.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateExpenseRequest(
    @NotNull(message = "Amount must be a positive number")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be a positive number")
    BigDecimal amount,
    String description,
    String category,
    @NotBlank(message = "expenseDate must be a valid date") String expenseDate
) {
}
