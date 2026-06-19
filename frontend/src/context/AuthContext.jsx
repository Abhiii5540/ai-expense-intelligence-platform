import { useCallback, useState } from 'react';
import { TOKEN_STORAGE_KEY } from '../constants/auth';
import { AuthContext } from './authContextValue';

function readStoredToken() {
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(readStoredToken);

  const login = useCallback((newToken) => {
    localStorage.setItem(TOKEN_STORAGE_KEY, newToken);
    setToken(newToken);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    setToken(null);
  }, []);

  const value = {
    token,
    login,
    logout,
    isAuthenticated: Boolean(token),
  };

  return (
    <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
  );
}
