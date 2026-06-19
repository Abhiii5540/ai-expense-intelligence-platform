import { Link } from 'react-router-dom';
import CategoryIcon from './CategoryIcon';
import { formatCurrency, formatDate } from '../utils/expenseFormat';

function RecentExpenses({ expenses }) {
  if (expenses.length === 0) {
    return (
      <section className="recent-expenses">
        <div className="section-header">
          <h2>Recent expenses</h2>
          <Link to="/expenses">Add expense</Link>
        </div>
        <div className="empty-state empty-state--panel">
          <p>No recent expenses yet.</p>
          <span>Add an expense to start filling your activity feed.</span>
        </div>
      </section>
    );
  }

  return (
    <section className="recent-expenses">
      <div className="section-header">
        <h2>Recent expenses</h2>
        <Link to="/expenses">View all</Link>
      </div>
      <ul className="recent-list">
        {expenses.map((expense) => (
          <li key={expense.id}>
            <div className="recent-expense-main">
              <span className="category-icon">
                <CategoryIcon category={expense.category} />
              </span>
              <div className="recent-expense-copy">
                <strong>{expense.description || 'No description'}</strong>
                <span>
                  {expense.category || 'Uncategorized'} - {formatDate(expense.expenseDate)}
                </span>
              </div>
            </div>
            <p className="recent-amount">{formatCurrency(expense.amount)}</p>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default RecentExpenses;
