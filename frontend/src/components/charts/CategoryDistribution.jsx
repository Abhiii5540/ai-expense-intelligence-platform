import {
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from 'recharts';
import { CHART_COLORS, computeCategoryDistribution } from '../../utils/chartData';
import { formatCurrency } from '../../utils/expenseFormat';

function ChartEmptyState() {
  return (
    <div className="empty-state empty-state--chart">
      <p>No category distribution yet.</p>
      <span>Add categorized expenses to reveal where your money is going.</span>
    </div>
  );
}

function renderLabel({ category, percentage }) {
  return `${category}: ${percentage.toFixed(1)}%`;
}

function CategoryDistribution({ expenses }) {
  const data = computeCategoryDistribution(expenses);

  if (data.length === 0) {
    return (
      <section className="chart-panel">
        <h2>Category Distribution</h2>
        <ChartEmptyState />
      </section>
    );
  }

  return (
    <section className="chart-panel">
      <h2>Category Distribution</h2>
      <div className="chart-container">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={data}
              dataKey="amount"
              nameKey="category"
              cx="50%"
              cy="50%"
              outerRadius="70%"
              label={renderLabel}
              labelLine={{ stroke: 'var(--text)' }}
            >
              {data.map((entry, index) => (
                <Cell
                  key={entry.category}
                  fill={CHART_COLORS[index % CHART_COLORS.length]}
                />
              ))}
            </Pie>
            <Tooltip
              formatter={(value, _name, item) => [
                `${formatCurrency(value)} (${item.payload.percentage.toFixed(1)}%)`,
                item.payload.category,
              ]}
              contentStyle={{
                background: 'var(--bg)',
                border: '1px solid var(--border)',
                borderRadius: '8px',
              }}
            />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}

export default CategoryDistribution;
