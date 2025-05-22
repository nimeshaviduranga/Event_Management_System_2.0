/**
 * API Endpoints
 * 
 * This file defines all the API endpoints used in the application.
 * Centralizing them makes maintenance easier and ensures consistency.
 */

export const AUTH = {
  LOGIN: '/auth/login',
  REGISTER: '/auth/register',
  LOGOUT: '/auth/logout',
  REFRESH_TOKEN: '/auth/refresh-token',
};

export const USERS = {
  PROFILE: '/users/profile',
  UPDATE_PROFILE: '/users/profile',
  USER_EVENTS: '/users/events',
};

export const EVENTS = {
  ALL: '/events',
  DETAILS: (id) => `/events/${id}`,
  CREATE: '/events',
  UPDATE: (id) => `/events/${id}`,
  DELETE: (id) => `/events/${id}`,
  ATTEND: (id) => `/events/${id}/attend`,
  UNATTEND: (id) => `/events/${id}/unattend`,
  UPCOMING: '/events/upcoming',
};

export const ATTENDANCE = {
  UPDATE_STATUS: (eventId, status) => `/events/${eventId}/attendance/${status}`,
  GET_ATTENDEES: (eventId) => `/events/${eventId}/attendees`,
};