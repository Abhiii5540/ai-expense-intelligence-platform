export function validateExpenseForm({ amount, expenseDate, description, category }) {
  const errors = {};

  const parsedAmount = Number(amount);
  if (!amount && amount !== 0) {
    errors.amount = 'Amount is required';
  } else if (!Number.isFinite(parsedAmount) || parsedAmount <= 0) {
    errors.amount = 'Amount must be a positive number';
  }

  if (!expenseDate) {
    errors.expenseDate = 'Date is required';
  } else {
    const date = new Date(expenseDate);
    if (Number.isNaN(date.getTime())) {
      errors.expenseDate = 'Enter a valid date';
    }
  }

  if (description?.trim().length > 500) {
    errors.description = 'Description must be 500 characters or less';
  }

  if (category?.trim().length > 100) {
    errors.category = 'Category must be 100 characters or less';
  }

  return errors;
}

export function buildExpensePayload({ amount, expenseDate, description, category }) {
  return {
    amount: Number(amount),
    expenseDate,
    description: description?.trim() || null,
    category: category?.trim() || null,
  };
}
