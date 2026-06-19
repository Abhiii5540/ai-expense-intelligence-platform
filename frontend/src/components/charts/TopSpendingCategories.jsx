import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { CHART_COLORS, computeTopCategories } from '../../utils/chartData';
import { formatCurrency, formatCurrencyAxis } from '../../utils/expenseFormat';

function ChartEmptyState() {
  return (
    <div className="empty-state empty-state--chart">
      <p>No top categories yet.</p>
      <span>Your largest spending categories will appear here after expenses are added.</span>
    </div>
  );
}

function TopSpendingCategories({ expenses }) {
  const data = computeTopCategories(expenses);

  if (data.length === 0) {
    return (
      <section className="chart-panel">
        <h2>Top Spending Categories</h2>
        <ChartEmptyState />
      </section>
    );
  }

  return (
    <section className="chart-panel">
      <h2>Top Spending Categories</h2>
      <div className="chart-container">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart
            data={data}
            layout="vertical"
            margin={{ top: 8, right: 16, left: 8, bottom: 8 }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="var(--border)" />
            <XAxis
              type="number"
              tickFormatter={formatCurrencyAxis}
              tick={{ fill: 'var(--text)', fontSize: 12 }}
              tickLine={{ stroke: 'var(--border)' }}
              axisLine={{ stroke: 'var(--border)' }}
            />
            <YAxis
              type="category"
              dataKey="category"
              width={100}
              tick={{ fill: 'var(--text)', fontSize: 12 }}
              tickLine={{ stroke: 'var(--border)' }}
              axisLine={{ stroke: 'var(--border)' }}
            />
            <Tooltip
              formatter={(value, _name, item) => [
                `${formatCurrency(value)} (${item.payload.percentage.toFixed(1)}%)`,
                'Spending',
              ]}
              contentStyle={{
                background: 'var(--bg)',
                border: '1px solid var(--border)',
                borderRadius: '8px',
              }}
            />
            <Bar dataKey="amount" name="Spending" radius={[0, 4, 4, 0]}>
              {data.map((entry, index) => (
                <Cell
                  key={entry.category}
                  fill={CHART_COLORS[index % CHART_COLORS.length]}
                />
              ))}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}

export default TopSpendingCategories;
