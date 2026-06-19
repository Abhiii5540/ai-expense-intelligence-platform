import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { computeMonthlyTrend } from '../../utils/chartData';
import { formatCurrency, formatCurrencyAxis } from '../../utils/expenseFormat';

function ChartEmptyState() {
  return (
    <div className="empty-state empty-state--chart">
      <p>No monthly trend yet.</p>
      <span>Add expenses across dates to see how spending changes over time.</span>
    </div>
  );
}

function MonthlyExpenseTrend({ expenses }) {
  const data = computeMonthlyTrend(expenses);

  if (data.length === 0) {
    return (
      <section className="chart-panel chart-panel--wide">
        <h2>Monthly Expense Trend</h2>
        <ChartEmptyState />
      </section>
    );
  }

  return (
    <section className="chart-panel chart-panel--wide">
      <h2>Monthly Expense Trend</h2>
      <div className="chart-container">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={data} margin={{ top: 8, right: 16, left: 8, bottom: 8 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
            <XAxis
              dataKey="month"
              tick={{ fill: 'var(--text)', fontSize: 12 }}
              tickLine={{ stroke: 'var(--border)' }}
              axisLine={{ stroke: 'var(--border)' }}
            />
            <YAxis
              tickFormatter={formatCurrencyAxis}
              tick={{ fill: 'var(--text)', fontSize: 12 }}
              tickLine={{ stroke: 'var(--border)' }}
              axisLine={{ stroke: 'var(--border)' }}
              width={72}
            />
            <Tooltip
              formatter={(value) => formatCurrency(value)}
              labelStyle={{ color: 'var(--text-h)' }}
              contentStyle={{
                background: 'var(--bg)',
                border: '1px solid var(--border)',
                borderRadius: '8px',
              }}
            />
            <Line
              type="monotone"
              dataKey="total"
              name="Spending"
              stroke="var(--accent)"
              strokeWidth={2}
              dot={{ fill: 'var(--accent)', r: 4 }}
              activeDot={{ r: 6 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}

export default MonthlyExpenseTrend;
