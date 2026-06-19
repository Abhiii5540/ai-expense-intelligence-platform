import { formatCurrency, formatDate } from './expenseFormat';

const CSV_HEADERS = ['Date', 'Category', 'Description', 'Amount'];

function getCategoryLabel(category) {
  return category?.trim() || 'Uncategorized';
}

function escapeCsvValue(value) {
  const text = String(value ?? '');
  if (/[",\n\r]/.test(text)) {
    return `"${text.replaceAll('"', '""')}"`;
  }

  return text;
}

function formatFileDate(date = new Date()) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function getExpensesCsvFileName(date = new Date()) {
  return `expenses-${formatFileDate(date)}.csv`;
}

export function createExpensesCsv(expenses) {
  const rows = expenses.map((expense) => [
    formatDate(expense.expenseDate),
    getCategoryLabel(expense.category),
    expense.description || '',
    formatCurrency(expense.amount),
  ]);

  return [CSV_HEADERS, ...rows]
    .map((row) => row.map(escapeCsvValue).join(','))
    .join('\n');
}

export function downloadCsv(csvContent, fileName) {
  const blob = new Blob([`\uFEFF${csvContent}`], {
    type: 'text/csv;charset=utf-8',
  });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');

  link.href = url;
  link.download = fileName;
  link.style.display = 'none';

  document.body.append(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}

export function exportExpensesToCsv(expenses) {
  if (!expenses.length) return;

  downloadCsv(createExpensesCsv(expenses), getExpensesCsvFileName());
}
