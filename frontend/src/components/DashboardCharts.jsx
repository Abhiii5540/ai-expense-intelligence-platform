import CategoryDistribution from './charts/CategoryDistribution';
import MonthlyExpenseTrend from './charts/MonthlyExpenseTrend';
import TopSpendingCategories from './charts/TopSpendingCategories';

function DashboardCharts({ expenses }) {
  return (
    <section className="charts-section">
      <MonthlyExpenseTrend expenses={expenses} />
      <div className="charts-grid">
        <CategoryDistribution expenses={expenses} />
        <TopSpendingCategories expenses={expenses} />
      </div>
    </section>
  );
}

export default DashboardCharts;
