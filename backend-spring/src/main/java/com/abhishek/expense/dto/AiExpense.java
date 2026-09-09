package com.abhishek.expense.dto;

import java.math.BigDecimal;

public record AiExpense(BigDecimal amount, String category, String description) {
}
