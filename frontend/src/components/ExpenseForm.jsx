import { useState } from 'react';
import { todayInputDate, toInputDate } from '../utils/expenseFormat';
import { buildExpensePayload, validateExpenseForm } from '../utils/expenseValidation';

const emptyForm = {
  amount: '',
  description: '',
  category: '',
  expenseDate: todayInputDate(),
};

function expenseToForm(expense) {
  if (!expense) return emptyForm;
  return {
    amount: String(expense.amount ?? ''),
    description: expense.description ?? '',
    category: expense.category ?? '',
    expenseDate: toInputDate(expense.expenseDate) || todayInputDate(),
  };
}

function ExpenseForm({ title, expense, onSubmit, onCancel, isSubmitting }) {
  const [form, setForm] = useState(() => expenseToForm(expense));
  const [fieldErrors, setFieldErrors] = useState({});
  const [formError, setFormError] = useState('');

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setFormError('');

    const errors = validateExpenseForm(form);
    setFieldErrors(errors);
    if (Object.keys(errors).length > 0) return;

    try {
      await onSubmit(buildExpensePayload(form));
    } catch (err) {
      setFormError(err.message || 'Failed to save expense');
    }
  }

  return (
    <section className="expense-form-panel">
      <h2>{title}</h2>

      {formError && <p className="form-error" role="alert">{formError}</p>}

      <form onSubmit={handleSubmit} noValidate>
        <div className="form-row">
          <label htmlFor="amount">Amount</label>
          <input
            id="amount"
            name="amount"
            type="number"
            min="0.01"
            step="0.01"
            value={form.amount}
            onChange={handleChange}
            aria-invalid={Boolean(fieldErrors.amount)}
          />
          {fieldErrors.amount && <p className="field-error" role="alert">{fieldErrors.amount}</p>}
        </div>

        <div className="form-row">
          <label htmlFor="expenseDate">Date</label>
          <input
            id="expenseDate"
            name="expenseDate"
            type="date"
            value={form.expenseDate}
            onChange={handleChange}
            aria-invalid={Boolean(fieldErrors.expenseDate)}
          />
          {fieldErrors.expenseDate && (
            <p className="field-error" role="alert">{fieldErrors.expenseDate}</p>
          )}
        </div>

        <div className="form-row">
          <label htmlFor="category">Category</label>
          <input
            id="category"
            name="category"
            type="text"
            value={form.category}
            onChange={handleChange}
            aria-invalid={Boolean(fieldErrors.category)}
          />
          {fieldErrors.category && (
            <p className="field-error" role="alert">{fieldErrors.category}</p>
          )}
        </div>

        <div className="form-row">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            rows={3}
            value={form.description}
            onChange={handleChange}
            aria-invalid={Boolean(fieldErrors.description)}
          />
          {fieldErrors.description && (
            <p className="field-error" role="alert">{fieldErrors.description}</p>
          )}
        </div>

        <div className="form-actions">
          <button type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Saving…' : 'Save'}
          </button>
          <button type="button" onClick={onCancel} disabled={isSubmitting}>
            Cancel
          </button>
        </div>
      </form>
    </section>
  );
}

export default ExpenseForm;
