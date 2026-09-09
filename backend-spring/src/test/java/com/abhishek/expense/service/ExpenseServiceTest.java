package com.abhishek.expense.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.abhishek.expense.dto.CreateExpenseRequest;
import com.abhishek.expense.dto.UpdateExpenseRequest;
import com.abhishek.expense.error.ApiException;
import com.abhishek.expense.repository.ExpenseRepository;
import com.abhishek.expense.repository.UserRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseRepository, userRepository);
    }

    @Test
    void rejectsNonPositiveAmount() {
        CreateExpenseRequest request = new CreateExpenseRequest(
            BigDecimal.ZERO,
            "Lunch",
            "Food",
            "2026-09-09"
        );

        assertThatThrownBy(() -> expenseService.create("user-id", request))
            .isInstanceOf(ApiException.class);
    }

    @Test
    void rejectsUpdateWithoutFields() {
        assertThatThrownBy(() -> expenseService.update("user-id", "expense-id", new UpdateExpenseRequest()))
            .isInstanceOf(ApiException.class)
            .hasMessage("No fields to update");
    }
}
