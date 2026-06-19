import api from './api';

export async function listExpenses(params = {}) {
  const { data } = await api.get('/api/expenses', { params });
  return data.data;
}

export async function createExpense(payload) {
  const { data } = await api.post('/api/expenses', payload);
  return data.data.expense;
}

export async function updateExpense(id, payload) {
  const { data } = await api.put(`/api/expenses/${id}`, payload);
  return data.data.expense;
}

export async function deleteExpense(id) {
  await api.delete(`/api/expenses/${id}`);
}
