package com.abhishek.expense.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import java.math.BigDecimal;

public class UpdateExpenseRequest {

    private BigDecimal amount;
    private String description;
    private String category;
    private String expenseDate;
    private boolean amountPresent;
    private boolean descriptionPresent;
    private boolean categoryPresent;
    private boolean expenseDatePresent;

    @JsonSetter("amount")
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        this.amountPresent = true;
    }

    @JsonSetter("description")
    public void setDescription(String description) {
        this.description = description;
        this.descriptionPresent = true;
    }

    @JsonSetter("category")
    public void setCategory(String category) {
        this.category = category;
        this.categoryPresent = true;
    }

    @JsonSetter("expenseDate")
    public void setExpenseDate(String expenseDate) {
        this.expenseDate = expenseDate;
        this.expenseDatePresent = true;
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

    public String getExpenseDate() {
        return expenseDate;
    }

    public boolean isAmountPresent() {
        return amountPresent;
    }

    public boolean isDescriptionPresent() {
        return descriptionPresent;
    }

    public boolean isCategoryPresent() {
        return categoryPresent;
    }

    public boolean isExpenseDatePresent() {
        return expenseDatePresent;
    }

    public boolean hasAnyField() {
        return amountPresent || descriptionPresent || categoryPresent || expenseDatePresent;
    }
}
