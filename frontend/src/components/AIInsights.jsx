import { FaLightbulb } from 'react-icons/fa';
import { useState } from 'react';
import toast from 'react-hot-toast';
import { fetchAiInsights } from '../services/aiService';
import { getApiErrorMessage } from '../utils/getApiErrorMessage';

function AIInsights() {
  const [insights, setInsights] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleGenerate() {
    setLoading(true);
    setError('');

    try {
      const result = await fetchAiInsights();
      setInsights(result);
      toast.success('AI Insights Generated');
    } catch (err) {
      setInsights([]);
      setError(getApiErrorMessage(err));
      toast.error('Failed to Generate Insights');
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="ai-insights">
      <div className="section-header">
        <h2>AI Insights</h2>
        <button type="button" onClick={handleGenerate} disabled={loading}>
          Generate AI Insights
        </button>
      </div>

      {loading && <InsightsSkeleton />}

      {error && !loading && (
        <p className="form-error" role="alert">{error}</p>
      )}

      {!loading && !error && insights.length === 0 && (
        <div className="empty-state empty-state--panel">
          <p>No AI insights generated yet.</p>
          <span>Generate insights when you want a quick read on your spending patterns.</span>
        </div>
      )}

      {!loading && insights.length > 0 && (
        <ul className="ai-insights-grid">
          {insights.map((insight, index) => (
            <li key={`${index}-${insight}`} className="ai-insight-card">
              <span aria-hidden="true">
                <FaLightbulb />
              </span>
              <p>{insight}</p>
            </li>
          ))}
        </ul>
      )}
    </section>
  );
}

function InsightsSkeleton() {
  return (
    <div className="ai-insights-grid" aria-label="Generating AI insights">
      {Array.from({ length: 3 }).map((_, index) => (
        <div className="ai-insight-card skeleton-card" key={index}>
          <span className="skeleton skeleton-icon" />
          <div className="skeleton-lines">
            <span className="skeleton skeleton-line" />
            <span className="skeleton skeleton-line skeleton-line--short" />
          </div>
        </div>
      ))}
    </div>
  );
}

export default AIInsights;
