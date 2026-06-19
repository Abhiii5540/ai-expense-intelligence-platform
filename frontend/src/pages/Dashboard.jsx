import {
  FaChartLine,
  FaHashtag,
  FaReceipt,
  FaRupeeSign,
  FaWallet,
} from 'react-icons/fa';
import AIInsights from '../components/AIInsights';
import DashboardCharts from '../components/DashboardCharts';
import RecentExpenses from '../components/RecentExpenses';
import StatCard from '../components/StatCard';
import CategoryIcon from '../components/CategoryIcon';
import { useExpenses } from '../hooks/useExpenses';
import { formatCurrency } from '../utils/expenseFormat';
import { computeExpenseStats } from '../utils/expenseStats';

function Dashboard() {
  const { expenses, loading, error } = useExpenses();

  const stats = computeExpenseStats(expenses);

  return (
    <div className="page">
      <header className="dashboard-welcome">
        <div>
          <p className="dashboard-kicker">Dashboard</p>
          <h1>Welcome back 👋</h1>
          <p>Here's your spending overview.</p>
        </div>
      </header>

      {loading && <DashboardSkeleton />}
      {error && !loading && <p className="form-error" role="alert">{error}</p>}

      {!loading && !error && (
        <>
          <section className="stat-grid">
            <StatCard
              icon={<FaWallet />}
              label="Total expenses"
              value={formatCurrency(stats.totalAmount)}
            />
            <StatCard
              icon={<FaChartLine />}
              label="This month"
              value={formatCurrency(stats.currentMonthAmount)}
            />
            <StatCard
              icon={<FaReceipt />}
              label="Transactions"
              value={String(stats.transactionCount)}
            />
            <StatCard
              icon={<FaRupeeSign />}
              label="Highest Expense"
              value={stats.highestExpense ? formatCurrency(stats.highestExpense.amount) : 'No data'}
            />
            <StatCard
              icon={<FaHashtag />}
              label="Average Expense"
              value={formatCurrency(stats.averageExpense)}
            />
            <StatCard
              icon={<CategoryIcon category={stats.mostUsedCategory} />}
              label="Most Used Category"
              value={stats.mostUsedCategory || 'No data'}
            />
          </section>

          <DashboardCharts expenses={expenses} />

          <RecentExpenses expenses={stats.recentExpenses} />

          <AIInsights />
        </>
      )}
    </div>
  );
}

function DashboardSkeleton() {
  return (
    <div className="dashboard-skeleton" aria-label="Loading dashboard">
      <section className="stat-grid">
        {Array.from({ length: 6 }).map((_, index) => (
          <article className="stat-card skeleton-card" key={index}>
            <span className="skeleton skeleton-icon" />
            <div className="skeleton-lines">
              <span className="skeleton skeleton-line skeleton-line--short" />
              <span className="skeleton skeleton-line" />
            </div>
          </article>
        ))}
      </section>

      <section className="charts-section">
        <div className="chart-panel chart-panel--wide">
          <span className="skeleton skeleton-title" />
          <span className="skeleton skeleton-chart" />
        </div>
        <div className="charts-grid">
          {Array.from({ length: 2 }).map((_, index) => (
            <div className="chart-panel" key={index}>
              <span className="skeleton skeleton-title" />
              <span className="skeleton skeleton-chart" />
            </div>
          ))}
        </div>
      </section>

      <section className="recent-expenses">
        <span className="skeleton skeleton-title" />
        {Array.from({ length: 4 }).map((_, index) => (
          <div className="recent-skeleton-row" key={index}>
            <span className="skeleton skeleton-icon" />
            <div className="skeleton-lines">
              <span className="skeleton skeleton-line" />
              <span className="skeleton skeleton-line skeleton-line--short" />
            </div>
            <span className="skeleton skeleton-amount" />
          </div>
        ))}
      </section>
    </div>
  );
}

export default Dashboard;
