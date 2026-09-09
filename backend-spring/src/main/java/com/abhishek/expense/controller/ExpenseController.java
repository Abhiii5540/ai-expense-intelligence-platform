package com.abhishek.expense.controller;

import com.abhishek.expense.dto.ApiSuccess;
import com.abhishek.expense.dto.CreateExpenseRequest;
import com.abhishek.expense.dto.ExpensePage;
import com.abhishek.expense.dto.ExpenseResponse;
import com.abhishek.expense.dto.UpdateExpenseRequest;
import com.abhishek.expense.security.UserPrincipal;
import com.abhishek.expense.service.ExpenseService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    ResponseEntity<ApiSuccess<Map<String, ExpenseResponse>>> create(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody CreateExpenseRequest request
    ) {
        ExpenseResponse expense = expenseService.create(principal.id(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiSuccess.of(Map.of("expense", expense)));
    }

    @GetMapping
    ApiSuccess<ExpensePage> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer limit,
        @RequestParam(required = false) String category
    ) {
        return ApiSuccess.of(expenseService.list(principal.id(), page, limit, category));
    }

    @GetMapping("/{id}")
    ApiSuccess<Map<String, ExpenseResponse>> getById(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String id
    ) {
        return ApiSuccess.of(Map.of("expense", expenseService.getById(principal.id(), id)));
    }

    @PutMapping("/{id}")
    ApiSuccess<Map<String, ExpenseResponse>> update(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String id,
        @RequestBody UpdateExpenseRequest request
    ) {
        return ApiSuccess.of(Map.of("expense", expenseService.update(principal.id(), id, request)));
    }

    @DeleteMapping("/{id}")
    ApiSuccess<Map<String, Boolean>> delete(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable String id
    ) {
        expenseService.delete(principal.id(), id);
        return ApiSuccess.of(Map.of("deleted", true));
    }
}
