package com.abhishek.expenseai.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ExpenseInput(
    @NotNull BigDecimal amount,
    @NotNull String category,
    @NotNull String description
) {
}
