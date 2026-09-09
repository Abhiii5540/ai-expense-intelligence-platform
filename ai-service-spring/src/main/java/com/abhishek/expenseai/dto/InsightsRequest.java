package com.abhishek.expenseai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record InsightsRequest(@NotNull List<@Valid ExpenseInput> expenses) {
}
