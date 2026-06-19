import { getCategoryLabel } from './chartData';
import { parseAmount } from './expenseFormat';

export function computeExpenseStats(expenses) {
  const now = new Date();
  const currentMonth = now.getMonth();
  const currentYear = now.getFullYear();

  let totalAmount = 0;
  let currentMonthAmount = 0;
  let highestExpense = null;
  const categoryUsage = new Map();

  for (const expense of expenses) {
    const amount = parseAmount(expense.amount);
    totalAmount += amount;

    if (!highestExpense || amount > parseAmount(highestExpense.amount)) {
      highestExpense = expense;
    }

    const category = getCategoryLabel(expense.category);
    categoryUsage.set(category, (categoryUsage.get(category) || 0) + 1);

    const expenseDate = new Date(expense.expenseDate);
    if (
      expenseDate.getMonth() === currentMonth &&
      expenseDate.getFullYear() === currentYear
    ) {
      currentMonthAmount += amount;
    }
  }

  const recentExpenses = [...expenses]
    .sort((a, b) => new Date(b.expenseDate) - new Date(a.expenseDate))
    .slice(0, 5);

  const mostUsedCategory = Array.from(categoryUsage.entries()).sort(
    ([, aCount], [, bCount]) => bCount - aCount,
  )[0]?.[0] || '';

  return {
    totalAmount,
    currentMonthAmount,
    transactionCount: expenses.length,
    highestExpense,
    averageExpense: expenses.length > 0 ? totalAmount / expenses.length : 0,
    mostUsedCategory,
    recentExpenses,
  };
}
