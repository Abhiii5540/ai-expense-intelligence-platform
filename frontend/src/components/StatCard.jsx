function StatCard({ icon, label, value }) {
  return (
    <article className="stat-card">
      {icon && (
        <span className="stat-icon" aria-hidden="true">
          {icon}
        </span>
      )}
      <div>
        <p className="stat-label">{label}</p>
        <p className="stat-value">{value}</p>
      </div>
    </article>
  );
}

export default StatCard;
