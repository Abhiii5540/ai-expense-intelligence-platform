import api from './api';

export async function fetchAiInsights() {
  const { data } = await api.get('/api/ai/insights');
  return data.insights ?? [];
}
