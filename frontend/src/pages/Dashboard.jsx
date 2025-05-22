import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { fetchEvents, setFilters, clearFilters } from '../features/events/eventsSlice';
import Loader from '../components/common/Loader';
import Alert from '../components/common/Alert';
import Card from '../components/common/Card';
import Button from '../components/common/Button';

/**
 * Dashboard Page
 * 
 * Main page showing a list of events with filtering options
 */
const Dashboard = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Get events data from redux store
  const { events, filters, status, error } = useSelector((state) => state.events);

  // Local state for filter inputs
  const [filterInputs, setFilterInputs] = useState({
    host: filters.host || '',
    location: filters.location || '',
    startDate: filters.startDate || '',
    endDate: filters.endDate || '',
  });

  // Load events on component mount
  useEffect(() => {
    dispatch(fetchEvents());
  }, [dispatch]);

  // Handle input change for filters
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilterInputs({
      ...filterInputs,
      [name]: value,
    });
  };

  // Apply filters
  const handleApplyFilters = () => {
    dispatch(setFilters(filterInputs));
    dispatch(fetchEvents(filterInputs));
  };

  // Clear filters
  const handleClearFilters = () => {
    setFilterInputs({
      host: '',
      location: '',
      startDate: '',
      endDate: '',
    });
    dispatch(clearFilters());
    dispatch(fetchEvents());
  };

  // Navigate to event details
  const handleViewEvent = (eventId) => {
    navigate(`/events/${eventId}`);
  };

  // Navigate to create event page
  const handleCreateEvent = () => {
    navigate('/events/create');
  };

  // Format date for display
  const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  return (
    <div className="container">
      <div className="d-flex justify-between align-center mb-4">
        <h1>Events Dashboard</h1>
        <Button variant="primary" onClick={handleCreateEvent}>
          Create New Event
        </Button>
      </div>

      {/* Filters section */}
      <Card className="mb-4">
        <h2 className="mb-3">Filters</h2>
        <div className="row mb-3">
          <div className="col">
            <label className="form-label">Host</label>
            <input
              type="text"
              name="host"
              className="form-control"
              placeholder="Filter by host"
              value={filterInputs.host}
              onChange={handleInputChange}
            />
          </div>
          <div className="col">
            <label className="form-label">Location</label>
            <input
              type="text"
              name="location"
              className="form-control"
              placeholder="Filter by location"
              value={filterInputs.location}
              onChange={handleInputChange}
            />
          </div>
        </div>
        <div className="row mb-3">
          <div className="col">
            <label className="form-label">Start Date</label>
            <input
              type="date"
              name="startDate"
              className="form-control"
              value={filterInputs.startDate}
              onChange={handleInputChange}
            />
          </div>
          <div className="col">
            <label className="form-label">End Date</label>
            <input
              type="date"
              name="endDate"
              className="form-control"
              value={filterInputs.endDate}
              onChange={handleInputChange}
            />
          </div>
        </div>
        <div className="d-flex justify-end">
          <Button
            variant="outline"
            onClick={handleClearFilters}
            style={{ marginRight: '10px' }}
          >
            Clear Filters
          </Button>
          <Button variant="primary" onClick={handleApplyFilters}>
            Apply Filters
          </Button>
        </div>
      </Card>

      {/* Loading state */}
      {status === 'loading' && (
        <Loader text="Loading events..." />
      )}

      {/* Error state */}
      {status === 'failed' && (
        <Alert type="error" message={error || 'Failed to load events'} />
      )}

      {/* Empty state */}
      {status === 'succeeded' && events.length === 0 && (
        <div className="text-center mt-4">
          <p>No events found. Adjust filters or create a new event.</p>
          <Button
            variant="primary"
            onClick={handleCreateEvent}
            style={{ marginTop: '10px' }}
          >
            Create New Event
          </Button>
        </div>
      )}

      {/* Events list */}
      {status === 'succeeded' && events.length > 0 && (
        <div>
          <h2 className="mb-3">Events ({events.length})</h2>
          {events.map((event) => (
            <Card
              key={event.id}
              hoverable
              className="mb-3"
              onClick={() => handleViewEvent(event.id)}
            >
              <div className="d-flex justify-between">
                <div>
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 'bold', marginBottom: '5px' }}>
                    {event.title}
                  </h3>
                  <p className="mb-2">{event.description}</p>
                  <div className="d-flex" style={{ color: 'var(--dark-gray)', fontSize: '0.9rem' }}>
                    <div style={{ marginRight: '20px' }}>
                      <strong>Location:</strong> {event.location}
                    </div>
                    <div>
                      <strong>Host:</strong> {event.hostName || 'Unknown'}
                    </div>
                  </div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{
                    display: 'inline-block',
                    padding: '4px 8px',
                    backgroundColor: event.visibility === 'PUBLIC' ? 'var(--success-color)' : 'var(--warning-color)',
                    color: 'white',
                    borderRadius: '4px',
                    fontSize: '0.8rem',
                    marginBottom: '10px'
                  }}>
                    {event.visibility}
                  </div>
                  <div style={{ fontSize: '0.9rem' }}>
                    <div><strong>Start:</strong> {formatDate(event.startTime)}</div>
                    <div><strong>End:</strong> {formatDate(event.endTime)}</div>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dashboard;