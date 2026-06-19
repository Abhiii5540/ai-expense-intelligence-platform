import { useMemo, useState } from 'react';
import toast from 'react-hot-toast';
import { FaCalculator, FaDownload, FaReceipt, FaWallet } from 'react-icons/fa';
import ExpenseForm from '../components/ExpenseForm';
import ExpenseTable from '../components/ExpenseTable';
import StatCard from '../components/StatCard';
import { useExpenses } from '../hooks/useExpenses';
import {
  createExpense,
  deleteExpense,
  updateExpense,
} from '../services/expenseService';
import { formatCurrency } from '../utils/expenseFormat';
import { computeExpenseStats } from '../utils/expenseStats';
import { exportExpensesToCsv } from '../utils/exportCsv';
import { getApiErrorMessage } from '../utils/getApiErrorMessage';

const ALL_CATEGORIES = 'all';
const QUICK_FILTER_ALL_TIME = 'all-time';

const QUICK_FILTERS = [
  { value: 'today', label: 'Today' },
  { value: 'last-7-days', label: 'Last 7 Days' },
  { value: 'this-month', label: 'This Month' },
  { value: 'last-month', label: 'Last Month' },
  { value: QUICK_FILTER_ALL_TIME, label: 'All Time' },
];

const emptyFilters = {
  search: '',
  category: ALL_CATEGORIES,
  fromDate: '',
  toDate: '',
  quickFilter: QUICK_FILTER_ALL_TIME,
};

function getCategoryLabel(category) {
  return category?.trim() || 'Uncategorized';
}

function toComparableDate(dateValue) {
  const date = new Date(dateValue);
  if (Number.isNaN(date.getTime())) return '';
  return date.toISOString().slice(0, 10);
}

function formatInputDate(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

function addDays(date, days) {
  const nextDate = new Date(date);
  nextDate.setDate(nextDate.getDate() + days);
  return nextDate;
}

function getQuickFilterDateRange(quickFilter) {
  const today = new Date();

  if (quickFilter === 'today') {
    const todayValue = formatInputDate(today);
    return { fromDate: todayValue, toDate: todayValue };
  }

  if (quickFilter === 'last-7-days') {
    return {
      fromDate: formatInputDate(addDays(today, -6)),
      toDate: formatInputDate(today),
    };
  }

  if (quickFilter === 'this-month') {
    return {
      fromDate: formatInputDate(new Date(today.getFullYear(), today.getMonth(), 1)),
      toDate: formatInputDate(today),
    };
  }

  if (quickFilter === 'last-month') {
    return {
      fromDate: formatInputDate(new Date(today.getFullYear(), today.getMonth() - 1, 1)),
      toDate: formatInputDate(new Date(today.getFullYear(), today.getMonth(), 0)),
    };
  }

  return { fromDate: '', toDate: '' };
}

function Expenses() {
  const { expenses, loading, error, refetch } = useExpenses();
  const [formMode, setFormMode] = useState(null);
  const [editingExpense, setEditingExpense] = useState(null);
  const [actionError, setActionError] = useState('');
  const [isSaving, setIsSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [filters, setFilters] = useState(emptyFilters);
  const [expenseToDelete, setExpenseToDelete] = useState(null);

  const categoryOptions = useMemo(() => {
    return Array.from(new Set(expenses.map((expense) => getCategoryLabel(expense.category))))
      .sort((a, b) => a.localeCompare(b));
  }, [expenses]);

  const hasActiveFilters = Boolean(
    filters.search.trim() ||
    filters.category !== ALL_CATEGORIES ||
    filters.fromDate ||
    filters.toDate,
  );

  const filteredExpenses = useMemo(() => {
    const searchTerm = filters.search.trim().toLowerCase();

    return expenses.filter((expense) => {
      const category = getCategoryLabel(expense.category);
      const description = expense.description || '';
      const expenseDate = toComparableDate(expense.expenseDate);

      const matchesSearch =
        !searchTerm ||
        description.toLowerCase().includes(searchTerm) ||
        category.toLowerCase().includes(searchTerm);
      const matchesCategory =
        filters.category === ALL_CATEGORIES || category === filters.category;
      const matchesFromDate = !filters.fromDate || expenseDate >= filters.fromDate;
      const matchesToDate = !filters.toDate || expenseDate <= filters.toDate;

      return matchesSearch && matchesCategory && matchesFromDate && matchesToDate;
    });
  }, [expenses, filters]);

  const visibleStats = computeExpenseStats(filteredExpenses);
  const canExport = !loading && filteredExpenses.length > 0;
  const showExportUnavailableMessage = !loading && !canExport;

  function openAddForm() {
    setActionError('');
    setEditingExpense(null);
    setFormMode('add');
  }

  function openEditForm(expense) {
    setActionError('');
    setEditingExpense(expense);
    setFormMode('edit');
  }

  function closeForm() {
    setFormMode(null);
    setEditingExpense(null);
  }

  async function handleCreate(payload) {
    setIsSaving(true);
    setActionError('');
    try {
      await createExpense(payload);
      closeForm();
      await refetch();
      toast.success('Expense Added');
    } catch (err) {
      toast.error('Failed to Add Expense');
      throw new Error(getApiErrorMessage(err), { cause: err });
    } finally {
      setIsSaving(false);
    }
  }

  async function handleUpdate(payload) {
    if (!editingExpense) return;

    setIsSaving(true);
    setActionError('');
    try {
      await updateExpense(editingExpense.id, payload);
      closeForm();
      await refetch();
      toast.success('Expense Updated');
    } catch (err) {
      toast.error('Failed to Update Expense');
      throw new Error(getApiErrorMessage(err), { cause: err });
    } finally {
      setIsSaving(false);
    }
  }

  function handleFilterChange(event) {
    const { name, value } = event.target;
    setFilters((prev) => ({
      ...prev,
      [name]: value,
      quickFilter:
        name === 'fromDate' || name === 'toDate'
          ? 'custom'
          : prev.quickFilter,
    }));
  }

  function clearFilters() {
    setFilters(emptyFilters);
  }

  function applyQuickFilter(quickFilter) {
    const dateRange = getQuickFilterDateRange(quickFilter);
    setFilters((prev) => ({
      ...prev,
      ...dateRange,
      quickFilter,
    }));
  }

  function handleExportCsv() {
    try {
      exportExpensesToCsv(filteredExpenses);
      toast.success('CSV Exported');
    } catch {
      toast.error('Export Failed');
    }
  }

  function requestDelete(expense) {
    setExpenseToDelete(expense);
  }

  function cancelDelete() {
    if (deletingId) return;
    setExpenseToDelete(null);
  }

  async function confirmDelete() {
    if (!expenseToDelete) return;

    setDeletingId(expenseToDelete.id);
    setActionError('');
    try {
      await deleteExpense(expenseToDelete.id);
      if (formMode === 'edit' && editingExpense?.id === expenseToDelete.id) {
        closeForm();
      }
      setExpenseToDelete(null);
      await refetch();
      toast.success('Expense Deleted');
    } catch (err) {
      setActionError(getApiErrorMessage(err));
      toast.error('Failed to Delete Expense');
    } finally {
      setDeletingId(null);
    }
  }

  return (
    <div className="page">
      <header className="page-header expense-page-header">
        <div>
          <h1>Expense Management</h1>
          <p>Track and manage your spending.</p>
        </div>
        <div className="page-actions">
          <div className="export-action">
            <button
              type="button"
              className="export-button"
              onClick={handleExportCsv}
              disabled={!canExport}
              title={
                loading
                  ? 'Expenses are loading'
                  : canExport
                    ? 'Export displayed expenses'
                    : 'No expenses available to export'
              }
              aria-describedby={showExportUnavailableMessage ? 'export-disabled-message' : undefined}
            >
              <FaDownload aria-hidden="true" />
              Export CSV
            </button>
            {showExportUnavailableMessage && (
              <span id="export-disabled-message" className="export-disabled-message">
                No expenses available to export
              </span>
            )}
          </div>
          {formMode !== 'add' && (
            <button type="button" className="primary-action" onClick={openAddForm}>
              Add expense
            </button>
          )}
        </div>
      </header>

      {actionError && <p className="form-error" role="alert">{actionError}</p>}

      {formMode === 'add' && (
        <ExpenseForm
          key="add"
          title="Add expense"
          onSubmit={handleCreate}
          onCancel={closeForm}
          isSubmitting={isSaving}
        />
      )}

      {formMode === 'edit' && editingExpense && (
        <ExpenseForm
          key={editingExpense.id}
          title="Edit expense"
          expense={editingExpense}
          onSubmit={handleUpdate}
          onCancel={closeForm}
          isSubmitting={isSaving}
        />
      )}

      <section className="stat-grid expense-summary-grid">
        <StatCard
          icon={<FaReceipt />}
          label="Total Expenses"
          value={String(visibleStats.transactionCount)}
        />
        <StatCard
          icon={<FaWallet />}
          label="Total Amount"
          value={formatCurrency(visibleStats.totalAmount)}
        />
        <StatCard
          icon={<FaCalculator />}
          label="Average Expense"
          value={formatCurrency(visibleStats.averageExpense)}
        />
      </section>

      <section className="expense-filters" aria-label="Expense filters">
        <div className="filter-field filter-field--search">
          <label htmlFor="expense-search">Search</label>
          <input
            id="expense-search"
            name="search"
            type="search"
            placeholder="Description or category"
            value={filters.search}
            onChange={handleFilterChange}
          />
        </div>

        <div className="filter-field">
          <label htmlFor="expense-category-filter">Category</label>
          <select
            id="expense-category-filter"
            name="category"
            value={filters.category}
            onChange={handleFilterChange}
          >
            <option value={ALL_CATEGORIES}>All categories</option>
            {categoryOptions.map((category) => (
              <option value={category} key={category}>
                {category}
              </option>
            ))}
          </select>
        </div>

        <div className="filter-field">
          <label htmlFor="expense-from-date">From</label>
          <input
            id="expense-from-date"
            name="fromDate"
            type="date"
            value={filters.fromDate}
            onChange={handleFilterChange}
          />
        </div>

        <div className="filter-field">
          <label htmlFor="expense-to-date">To</label>
          <input
            id="expense-to-date"
            name="toDate"
            type="date"
            value={filters.toDate}
            onChange={handleFilterChange}
          />
        </div>

        <div className="quick-filter-group" role="group" aria-label="Quick date filters">
          {QUICK_FILTERS.map((quickFilter) => (
            <button
              type="button"
              className={
                filters.quickFilter === quickFilter.value
                  ? 'quick-filter-button quick-filter-button--active'
                  : 'quick-filter-button'
              }
              onClick={() => applyQuickFilter(quickFilter.value)}
              key={quickFilter.value}
            >
              {quickFilter.label}
            </button>
          ))}
        </div>
      </section>

      <section className="filter-summary" aria-live="polite">
        <p>
          Showing {filteredExpenses.length} of {expenses.length} expenses
        </p>
        {hasActiveFilters && (
          <button type="button" className="filter-clear" onClick={clearFilters}>
            Clear All Filters
          </button>
        )}
      </section>

      {loading && <ExpenseTable.Skeleton />}
      {error && !loading && <p className="form-error" role="alert">{error}</p>}

      {!loading && !error && (
        <ExpenseTable
          expenses={filteredExpenses}
          hasAnyExpenses={expenses.length > 0}
          hasActiveFilters={hasActiveFilters}
          onClearFilters={clearFilters}
          onEdit={openEditForm}
          onDelete={requestDelete}
          isDeletingId={deletingId}
        />
      )}

      {expenseToDelete && (
        <div className="modal-backdrop" role="presentation">
          <section
            className="confirm-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="delete-expense-title"
            aria-describedby="delete-expense-message"
          >
            <h2 id="delete-expense-title">Delete Expense?</h2>
            <p id="delete-expense-message">This action cannot be undone.</p>
            <div className="modal-actions">
              <button type="button" onClick={cancelDelete} disabled={Boolean(deletingId)}>
                Cancel
              </button>
              <button
                type="button"
                className="danger-button"
                onClick={confirmDelete}
                disabled={Boolean(deletingId)}
              >
                {deletingId ? 'Deleting...' : 'Delete'}
              </button>
            </div>
          </section>
        </div>
      )}
    </div>
  );
}

export default Expenses;
