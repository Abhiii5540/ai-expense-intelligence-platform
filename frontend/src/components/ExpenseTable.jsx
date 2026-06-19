import CategoryIcon from './CategoryIcon';
import { formatCurrency, formatDate } from '../utils/expenseFormat';

function getCategoryLabel(category) {
  return category?.trim() || 'Uncategorized';
}

function ExpenseTable({
  expenses,
  hasAnyExpenses,
  hasActiveFilters,
  onClearFilters,
  onEdit,
  onDelete,
  isDeletingId,
}) {
  if (expenses.length === 0) {
    return (
      <div className="empty-state empty-state--panel expense-empty-state">
        {hasAnyExpenses && hasActiveFilters ? (
          <>
            <p>No expenses match your filters.</p>
            <button type="button" onClick={onClearFilters}>
              Clear Filters
            </button>
          </>
        ) : (
          <>
            <p>No expenses yet.</p>
            <span>Add your first expense to start tracking spending.</span>
          </>
        )}
      </div>
    );
  }

  return (
    <div className="table-wrap expense-table-wrap">
      <table className="expense-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Category</th>
            <th>Description</th>
            <th className="amount-column">Amount</th>
            <th className="actions-column">Actions</th>
          </tr>
        </thead>
        <tbody>
          {expenses.map((expense) => {
            const category = getCategoryLabel(expense.category);

            return (
              <tr key={expense.id}>
                <td className="date-cell">{formatDate(expense.expenseDate)}</td>
                <td>
                  <div className="expense-category-cell">
                    <span className="category-icon category-icon--table">
                      <CategoryIcon category={category} />
                    </span>
                    <span className="category-badge">{category}</span>
                  </div>
                </td>
                <td className="description-cell">
                  {expense.description || 'No description'}
                </td>
                <td className="amount-cell">{formatCurrency(expense.amount)}</td>
                <td className="table-actions">
                  <button type="button" onClick={() => onEdit(expense)}>
                    Edit
                  </button>
                  <button
                    type="button"
                    className="table-delete-button"
                    onClick={() => onDelete(expense)}
                    disabled={isDeletingId === expense.id}
                  >
                    {isDeletingId === expense.id ? 'Deleting...' : 'Delete'}
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

function ExpenseTableSkeleton() {
  return (
    <div className="table-wrap expense-table-wrap" aria-label="Loading expenses">
      <table className="expense-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Category</th>
            <th>Description</th>
            <th className="amount-column">Amount</th>
            <th className="actions-column">Actions</th>
          </tr>
        </thead>
        <tbody>
          {Array.from({ length: 6 }).map((_, index) => (
            <tr key={index}>
              <td>
                <span className="skeleton skeleton-line skeleton-line--date" />
              </td>
              <td>
                <div className="expense-category-cell">
                  <span className="skeleton skeleton-icon skeleton-icon--small" />
                  <span className="skeleton skeleton-line skeleton-line--badge" />
                </div>
              </td>
              <td>
                <span className="skeleton skeleton-line" />
              </td>
              <td>
                <span className="skeleton skeleton-line skeleton-line--amount" />
              </td>
              <td>
                <div className="table-actions">
                  <span className="skeleton skeleton-button" />
                  <span className="skeleton skeleton-button" />
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

ExpenseTable.Skeleton = ExpenseTableSkeleton;

export default ExpenseTable;
