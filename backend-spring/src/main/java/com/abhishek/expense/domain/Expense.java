package com.abhishek.expense.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @Column(name = "expenseDate", nullable = false)
    private LocalDateTime expenseDate;

    @Column(name = "createdAt", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    protected Expense() {
    }

    private Expense(
        User user,
        BigDecimal amount,
        String description,
        String category,
        LocalDateTime expenseDate
    ) {
        this.user = user;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.expenseDate = expenseDate;
    }

    public static Expense create(
        User user,
        BigDecimal amount,
        String description,
        String category,
        LocalDateTime expenseDate
    ) {
        return new Expense(user, amount, description, category, expenseDate);
    }

    public void updateAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateCategory(String category) {
        this.category = category;
    }

    public void updateExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = nowUtc();
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = nowUtc();
    }

    private static LocalDateTime nowUtc() {
        return LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getUser() {
        return user;
    }
}
