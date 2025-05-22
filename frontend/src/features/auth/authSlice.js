import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '../../api/apiConfig';
import { AUTH } from '../../api/apiEndpoints';

/**
 * Initial state for auth slice
 */
const initialState = {
  user: null,
  token: localStorage.getItem('token') || null,
  isAuthenticated: !!localStorage.getItem('token'),
  status: 'idle', // 'idle' | 'loading' | 'succeeded' | 'failed'
  error: null,
};

/**
 * Async thunk for user login
 * 
 * @param {Object} credentials - User credentials (email, password)
 */
export const login = createAsyncThunk(
  'auth/login',
  async (credentials, { rejectWithValue }) => {
    try {
      // Make API call to login endpoint
      const response = await api.post(AUTH.LOGIN, credentials);

      // Store token in localStorage for persistent auth
      localStorage.setItem('token', response.data.token);

      return response.data;
    } catch (error) {
      // Handle and return errors
      return rejectWithValue(
        error.response?.data?.message || 'Login failed. Please try again.'
      );
    }
  }
);

/**
 * Async thunk for user registration
 * 
 * @param {Object} userData - User registration data
 */
export const register = createAsyncThunk(
  'auth/register',
  async (userData, { rejectWithValue }) => {
    try {
      // Make API call to register endpoint
      const response = await api.post(AUTH.REGISTER, userData);

      // Store token in localStorage
      localStorage.setItem('token', response.data.token);

      return response.data;
    } catch (error) {
      // Handle and return errors
      return rejectWithValue(
        error.response?.data?.message || 'Registration failed. Please try again.'
      );
    }
  }
);

/**
 * Async thunk for user logout
 */
export const logout = createAsyncThunk(
  'auth/logout',
  async (_, { rejectWithValue }) => {
    try {
      // Only call logout API if we're authenticated
      if (localStorage.getItem('token')) {
        await api.post(AUTH.LOGOUT);
      }

      // Always remove token regardless of API call success
      localStorage.removeItem('token');

      return null;
    } catch (error) {
      // Still remove token on error
      localStorage.removeItem('token');

      return rejectWithValue(
        error.response?.data?.message || 'Logout failed.'
      );
    }
  }
);

/**
 * Auth slice for Redux store
 * Manages authentication state and related actions
 */
const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    // Synchronous action to check auth status
    checkAuthStatus: (state) => {
      const token = localStorage.getItem('token');
      state.isAuthenticated = !!token;
      state.token = token;
    },

    // Clear any auth errors
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Login cases
      .addCase(login.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.isAuthenticated = true;
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.error = null;
      })
      .addCase(login.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })

      // Register cases
      .addCase(register.pending, (state) => {
        state.status = 'loading';
        state.error = null;
      })
      .addCase(register.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.isAuthenticated = true;
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.error = null;
      })
      .addCase(register.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })

      // Logout cases
      .addCase(logout.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(logout.fulfilled, (state) => {
        // Reset state to initial values
        state.status = 'idle';
        state.isAuthenticated = false;
        state.user = null;
        state.token = null;
        state.error = null;
      })
      .addCase(logout.rejected, (state, action) => {
        state.status = 'idle';
        state.isAuthenticated = false;
        state.user = null;
        state.token = null;
        state.error = action.payload;
      });
  },
});

// Export synchronous actions
export const { checkAuthStatus, clearError } = authSlice.actions;

// Export selectors
export const selectIsAuthenticated = (state) => state.auth.isAuthenticated;
export const selectUser = (state) => state.auth.user;
export const selectAuthError = (state) => state.auth.error;
export const selectAuthStatus = (state) => state.auth.status;

// Export reducer
export default authSlice.reducer;