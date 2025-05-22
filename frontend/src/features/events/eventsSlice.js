import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import api from '../../api/apiConfig';
import { EVENTS } from '../../api/endpoints';

/**
 * Initial state for events slice
 */
const initialState = {
  events: [],
  selectedEvent: null,
  filters: {
    host: '',
    startDate: null,
    endDate: null,
    location: '',
    visibility: '',
  },
  pagination: {
    page: 0,
    size: 10,
    totalPages: 0,
    totalItems: 0,
  },
  status: 'idle', // 'idle' | 'loading' | 'succeeded' | 'failed'
  error: null,
};

/**
 * Async thunk to fetch all events with optional filtering
 * 
 * @param {Object} params - Query parameters for filtering
 */
export const fetchEvents = createAsyncThunk(
  'events/fetchEvents',
  async (params = {}, { rejectWithValue }) => {
    try {
      const response = await api.get(EVENTS.ALL, { params });
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to fetch events.'
      );
    }
  }
);

/**
 * Async thunk to fetch upcoming events with pagination
 * 
 * @param {Object} params - Pagination parameters
 */
export const fetchUpcomingEvents = createAsyncThunk(
  'events/fetchUpcomingEvents',
  async (params = {}, { rejectWithValue }) => {
    try {
      const response = await api.get(EVENTS.UPCOMING, { params });
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to fetch upcoming events.'
      );
    }
  }
);

/**
 * Async thunk to fetch a single event by ID
 * 
 * @param {string} id - Event ID
 */
export const fetchEventById = createAsyncThunk(
  'events/fetchEventById',
  async (id, { rejectWithValue }) => {
    try {
      const response = await api.get(EVENTS.DETAILS(id));
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to fetch event details.'
      );
    }
  }
);

/**
 * Async thunk to create a new event
 * 
 * @param {Object} eventData - Event data
 */
export const createEvent = createAsyncThunk(
  'events/createEvent',
  async (eventData, { rejectWithValue }) => {
    try {
      const response = await api.post(EVENTS.CREATE, eventData);
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to create event.'
      );
    }
  }
);

/**
 * Async thunk to update an existing event
 * 
 * @param {Object} params - Object with id and eventData
 */
export const updateEvent = createAsyncThunk(
  'events/updateEvent',
  async ({ id, eventData }, { rejectWithValue }) => {
    try {
      const response = await api.put(EVENTS.UPDATE(id), eventData);
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to update event.'
      );
    }
  }
);

/**
 * Async thunk to delete an event
 * 
 * @param {string} id - Event ID
 */
export const deleteEvent = createAsyncThunk(
  'events/deleteEvent',
  async (id, { rejectWithValue }) => {
    try {
      await api.delete(EVENTS.DELETE(id));
      return id; // Return the ID for state updates
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to delete event.'
      );
    }
  }
);

/**
 * Async thunk to attend an event
 * 
 * @param {string} id - Event ID
 */
export const attendEvent = createAsyncThunk(
  'events/attendEvent',
  async (id, { rejectWithValue }) => {
    try {
      const response = await api.post(EVENTS.ATTEND(id));
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to attend event.'
      );
    }
  }
);

/**
 * Async thunk to unattend an event
 * 
 * @param {string} id - Event ID
 */
export const unattendEvent = createAsyncThunk(
  'events/unattendEvent',
  async (id, { rejectWithValue }) => {
    try {
      const response = await api.post(EVENTS.UNATTEND(id));
      return response.data;
    } catch (error) {
      return rejectWithValue(
        error.response?.data?.message || 'Failed to unattend event.'
      );
    }
  }
);

/**
 * Events slice for Redux store
 * Manages events state and related actions
 */
const eventsSlice = createSlice({
  name: 'events',
  initialState,
  reducers: {
    // Set filter values
    setFilters: (state, action) => {
      state.filters = { ...state.filters, ...action.payload };
    },
    
    // Clear all filters
    clearFilters: (state) => {
      state.filters = initialState.filters;
    },
    
    // Clear selected event
    clearSelectedEvent: (state) => {
      state.selectedEvent = null;
    },
    
    // Set pagination
    setPagination: (state, action) => {
      state.pagination = { ...state.pagination, ...action.payload };
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch events cases
      .addCase(fetchEvents.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchEvents.fulfilled, (state, action) => {
        state.status = 'succeeded';
        
        // Handle both paginated and non-paginated responses
        if (action.payload.content) {
          // Paginated response
          state.events = action.payload.content;
          state.pagination = {
            page: action.payload.number,
            size: action.payload.size,
            totalPages: action.payload.totalPages,
            totalItems: action.payload.totalElements,
          };
        } else {
          // Non-paginated response
          state.events = action.payload;
        }
      })
      .addCase(fetchEvents.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      
      // Fetch upcoming events cases
      .addCase(fetchUpcomingEvents.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchUpcomingEvents.fulfilled, (state, action) => {
        state.status = 'succeeded';
        
        // Handle paginated response
        state.events = action.payload.content;
        state.pagination = {
          page: action.payload.number,
          size: action.payload.size,
          totalPages: action.payload.totalPages,
          totalItems: action.payload.totalElements,
        };
      })
      .addCase(fetchUpcomingEvents.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      
      // Fetch event by ID cases
      .addCase(fetchEventById.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(fetchEventById.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.selectedEvent = action.payload;
      })
      .addCase(fetchEventById.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      
      // Create event cases
      .addCase(createEvent.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(createEvent.fulfilled, (state, action) => {
        state.status = 'succeeded';
        state.events.push(action.payload);
      })
      .addCase(createEvent.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      
      // Update event cases
      .addCase(updateEvent.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(updateEvent.fulfilled, (state, action) => {
        state.status = 'succeeded';
        
        // Update in events array if it exists
        const index = state.events.findIndex(event => event.id === action.payload.id);
        if (index !== -1) {
          state.events[index] = action.payload;
        }
        
        // Update selected event if it's the same
        if (state.selectedEvent && state.selectedEvent.id === action.payload.id) {
          state.selectedEvent = action.payload;
        }
      })
      .addCase(updateEvent.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      
      // Delete event cases
      .addCase(deleteEvent.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(deleteEvent.fulfilled, (state, action) => {
        state.status = 'succeeded';
        
        // Remove from events array
        state.events = state.events.filter(event => event.id !== action.payload);
        
        // Clear selected event if it's the same
        if (state.selectedEvent && state.selectedEvent.id === action.payload) {
          state.selectedEvent = null;
        }
      })
      .addCase(deleteEvent.rejected, (state, action) => {
        state.status = 'failed';
        state.error = action.payload;
      })
      
      // Attend/unattend event cases
      .addCase(attendEvent.fulfilled, (state, action) => {
        // Update selected event if it's the same
        if (state.selectedEvent && state.selectedEvent.id === action.payload.id) {
          state.selectedEvent = action.payload;
        }
      })
      .addCase(unattendEvent.fulfilled, (state, action) => {
        // Update selected event if it's the same
        if (state.selectedEvent && state.selectedEvent.id === action.payload.id) {
          state.selectedEvent = action.payload;
        }
      });
  },
});

// Export synchronous actions
export const { 
  setFilters, 
  clearFilters, 
  clearSelectedEvent,
  setPagination 
} = eventsSlice.actions;

// Export selectors
export const selectAllEvents = (state) => state.events.events;
export const selectSelectedEvent = (state) => state.events.selectedEvent;
export const selectEventsStatus = (state) => state.events.status;
export const selectEventsError = (state) => state.events.error;
export const selectFilters = (state) => state.events.filters;
export const selectPagination = (state) => state.events.pagination;

// Export reducer
export default eventsSlice.reducer;