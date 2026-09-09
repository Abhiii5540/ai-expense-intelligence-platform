package com.abhishek.expense.repository;

import com.abhishek.expense.domain.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, String> {

    Page<Expense> findAllByUser_Id(String userId, Pageable pageable);

    Page<Expense> findAllByUser_IdAndCategory(String userId, String category, Pageable pageable);

    Optional<Expense> findByIdAndUser_Id(String id, String userId);

    List<Expense> findAllByUser_IdOrderByExpenseDateDesc(String userId);
}
