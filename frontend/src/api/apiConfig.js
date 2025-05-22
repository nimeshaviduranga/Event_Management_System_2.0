import axios from 'axios';

/**
 * API base URL
 */
export const API_BASE_URL = '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});


api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/** 
 * Handles common API error responses:
 * - 401 Unauthorized: Redirects to login page
 * - Other errors: Passes to the calling code to handle appropriately
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');

      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);

export default api;