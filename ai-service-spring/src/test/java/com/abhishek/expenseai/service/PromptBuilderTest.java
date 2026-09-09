package com.abhishek.expenseai.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.abhishek.expenseai.dto.ExpenseInput;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromptBuilderTest {

    private final PromptBuilder promptBuilder = new PromptBuilder();

    @Test
    void usesInrAndVariesRepeatedRequests() {
        List<ExpenseInput> expenses = List.of(
            new ExpenseInput(new BigDecimal("1250.50"), "Food", "Monthly groceries")
        );

        String first = promptBuilder.build(expenses);
        String second = promptBuilder.build(expenses);

        assertThat(first).contains("₹1,250.50", "Indian Rupees", "exactly 3 to 5");
        assertThat(second).isNotEqualTo(first);
    }
}
