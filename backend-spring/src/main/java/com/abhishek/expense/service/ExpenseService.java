package com.abhishek.expense.service;

import com.abhishek.expense.domain.Expense;
import com.abhishek.expense.domain.User;
import com.abhishek.expense.dto.CreateExpenseRequest;
import com.abhishek.expense.dto.ExpensePage;
import com.abhishek.expense.dto.ExpenseResponse;
import com.abhishek.expense.dto.UpdateExpenseRequest;
import com.abhishek.expense.error.ApiException;
import com.abhishek.expense.repository.ExpenseRepository;
import com.abhishek.expense.repository.UserRepository;
import com.abhishek.expense.util.DateTimes;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExpenseResponse create(String userId, CreateExpenseRequest request) {
        User user = requireUser(userId);
        Expense expense = Expense.create(
            user,
            normalizeAmount(request.amount()),
            request.description(),
            request.category(),
            DateTimes.parseExpenseDate(request.expenseDate())
        );
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public ExpensePage list(String userId, Integer requestedPage, Integer requestedLimit, String category) {
        int page = requestedPage == null || requestedPage <= 0 ? 1 : requestedPage;
        int limit = requestedLimit == null || requestedLimit <= 0
            ? DEFAULT_LIMIT
            : Math.min(requestedLimit, MAX_LIMIT);
        PageRequest pageable = PageRequest.of(
            page - 1,
            limit,
            Sort.by(Sort.Direction.DESC, "expenseDate")
        );

        Page<Expense> result = category == null || category.isBlank()
            ? expenseRepository.findAllByUser_Id(userId, pageable)
            : expenseRepository.findAllByUser_IdAndCategory(userId, category, pageable);
        List<ExpenseResponse> items = result.getContent().stream().map(ExpenseResponse::from).toList();

        return new ExpensePage(
            items,
            page,
            limit,
            result.getTotalElements(),
            Math.max(1, result.getTotalPages())
        );
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getById(String userId, String expenseId) {
        return ExpenseResponse.from(requireExpense(userId, expenseId));
    }

    @Transactional
    public ExpenseResponse update(String userId, String expenseId, UpdateExpenseRequest request) {
        if (!request.hasAnyField()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No fields to update");
        }

        Expense expense = requireExpense(userId, expenseId);
        if (request.isAmountPresent()) {
            expense.updateAmount(normalizeAmount(request.getAmount()));
        }
        if (request.isDescriptionPresent()) {
            expense.updateDescription(request.getDescription());
        }
        if (request.isCategoryPresent()) {
            expense.updateCategory(request.getCategory());
        }
        if (request.isExpenseDatePresent()) {
            expense.updateExpenseDate(DateTimes.parseExpenseDate(request.getExpenseDate()));
        }
        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(String userId, String expenseId) {
        Expense expense = requireExpense(userId, expenseId);
        expenseRepository.delete(expense);
    }

    @Transactional(readOnly = true)
    public List<Expense> listAllForUser(String userId) {
        return expenseRepository.findAllByUser_IdOrderByExpenseDateDesc(userId);
    }

    private User requireUser(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    private Expense requireExpense(String userId, String expenseId) {
        return expenseRepository.findByIdAndUser_Id(expenseId, userId)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Expense not found"));
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Amount must be a positive number");
        }
        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
        if (normalized.precision() - normalized.scale() > 10) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Amount is too large");
        }
        return normalized;
    }

}
