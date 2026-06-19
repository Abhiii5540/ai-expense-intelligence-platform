import api from './api';

export async function loginRequest(credentials) {
  const { data } = await api.post('/api/auth/login', credentials);
  return data.data;
}

export async function signupRequest(payload) {
  const { data } = await api.post('/api/auth/signup', payload);
  return data.data;
}
