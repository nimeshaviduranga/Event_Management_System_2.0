import { configureStore } from '@reduxjs/toolkit';
import eventsReducer from '../features/events/eventsSlice';
import authReducer from '../features/auth/authSlice';

/**
 * Redux Store Configuration
 * 
 * This file sets up the Redux store for the application.
 *
 */
export const store = configureStore({
  reducer: {
    events: eventsReducer,
    auth: authReducer,

  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
  devTools: process.env.NODE_ENV !== 'production',
});
