import { parseAmount } from './expenseFormat';

export const UNCATEGORIZED = 'Uncategorized';

export const CHART_COLORS = [
  '#aa3bff',
  '#6366f1',
  '#14b8a6',
  '#f59e0b',
  '#ef4444',
  '#8b5cf6',
  '#06b6d4',
  '#84cc16',
];

export function getCategoryLabel(category) {
  return category?.trim() || UNCATEGORIZED;
}

export function computeMonthlyTrend(expenses) {
  const monthTotals = new Map();

  for (const expense of expenses) {
    const date = new Date(expense.expenseDate);
    if (Number.isNaN(date.getTime())) continue;

    const sortKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
    const amount = parseAmount(expense.amount);
    monthTotals.set(sortKey, (monthTotals.get(sortKey) || 0) + amount);
  }

  return Array.from(monthTotals.entries())
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([sortKey, total]) => {
      const [year, month] = sortKey.split('-');
      const label = new Date(Number(year), Number(month) - 1).toLocaleDateString('en-IN', {
        month: 'short',
        year: 'numeric',
      });

      return { month: label, total, sortKey };
    });
}

export function computeCategoryTotals(expenses) {
  const categoryTotals = new Map();
  let grandTotal = 0;

  for (const expense of expenses) {
    const category = getCategoryLabel(expense.category);
    const amount = parseAmount(expense.amount);
    categoryTotals.set(category, (categoryTotals.get(category) || 0) + amount);
    grandTotal += amount;
  }

  return Array.from(categoryTotals.entries())
    .map(([category, amount]) => ({
      category,
      amount,
      percentage: grandTotal > 0 ? (amount / grandTotal) * 100 : 0,
    }))
    .sort((a, b) => b.amount - a.amount);
}

export function computeCategoryDistribution(expenses) {
  return computeCategoryTotals(expenses);
}

export function computeTopCategories(expenses, limit = 8) {
  return computeCategoryTotals(expenses).slice(0, limit);
}
