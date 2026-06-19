import { useCallback, useEffect, useState } from 'react';
import { listExpenses } from '../services/expenseService';
import { getApiErrorMessage } from '../utils/getApiErrorMessage';

const DEFAULT_LIMIT = 100;

export function useExpenses({ limit = DEFAULT_LIMIT, enabled = true } = {}) {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(Boolean(enabled));
  const [error, setError] = useState('');

  const refetch = useCallback(async () => {
    if (!enabled) return;

    setLoading(true);
    setError('');

    try {
      const result = await listExpenses({ limit, page: 1 });
      setExpenses(result.items ?? []);
    } catch (err) {
      setError(getApiErrorMessage(err));
      setExpenses([]);
    } finally {
      setLoading(false);
    }
  }, [enabled, limit]);

  useEffect(() => {
    const timerId = setTimeout(() => {
      refetch();
    }, 0);

    return () => clearTimeout(timerId);
  }, [refetch]);

  return { expenses, loading, error, refetch, setExpenses };
}
