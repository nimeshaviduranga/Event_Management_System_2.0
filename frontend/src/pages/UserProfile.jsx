import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { USERS } from '../api/apiEndpoints.js';
import api from '../api/apiConfig';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Loader from '../components/common/Loader';
import Alert from '../components/common/Alert';

/**
 * UserProfile Page
 * 
 * Displays user profile information and events the user is attending
 */
const UserProfile = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  // Get auth state from redux store
  const { user, isAuthenticated } = useSelector((state) => state.auth);

  // Local state for user events and loading status
  const [userEvents, setUserEvents] = useState({
    hosting: [],
    attending: [],
  });
  const [status, setStatus] = useState({
    loading: false,
    error: null,
  });

  // Redirect to login if not authenticated
  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: '/profile' } });
    }
  }, [isAuthenticated, navigate]);

  // Fetch user events on component mount
  useEffect(() => {
    if (isAuthenticated && user) {
      fetchUserEvents();
    }
  }, [isAuthenticated, user]);

  // Fetch user events from API
  const fetchUserEvents = async () => {
    setStatus({ loading: true, error: null });

    try {
      const response = await api.get(USERS.USER_EVENTS);
      setUserEvents({
        hosting: response.data.hosting || [],
        attending: response.data.attending || [],
      });
      setStatus({ loading: false, error: null });
    } catch (err) {
      setStatus({
        loading: false,
        error: err.response?.data?.message || 'Failed to load user events',
      });
    }
  };

  // Format date for display
  const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  // Navigate to event details
  const navigateToEvent = (eventId) => {
    navigate(`/events/${eventId}`);
  };

  // Show loading state
  if (status.loading) {
    return <Loader text="Loading profile data..." />;
  }

  // Show placeholder state if not authenticated or no user data
  if (!isAuthenticated || !user) {
    return (
      <div className="container">
        <p>Please log in to view your profile.</p>
      </div>
    );
  }

  return (
    <div className="container">
      <h1 className="mb-4">User Profile</h1>

      {/* Error message */}
      {status.error && (
        <Alert
          type="error"
          message={status.error}
        />
      )}

      {/* User info card */}
      <Card className="mb-4">
        <h2 className="mb-3">Profile Information</h2>
        <div className="mb-3">
          <strong>Name:</strong> {user.name}
        </div>
        <div className="mb-3">
          <strong>Email:</strong> {user.email}
        </div>
        <div className="mb-3">
          <strong>Role:</strong> {user.role}
        </div>
        <div>
          <strong>Member Since:</strong> {formatDate(user.createdAt)}
        </div>
      </Card>

      {/* Events user is hosting */}
      <h2 className="mb-3">Events You're Hosting</h2>
      {userEvents.hosting.length > 0 ? (
        <div className="mb-4">
          {userEvents.hosting.map((event) => (
            <Card
              key={event.id}
              hoverable
              className="mb-3"
              onClick={() => navigateToEvent(event.id)}
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
                      <strong>Attendees:</strong> {event.attendeeCount || 0}
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
                    <div><strong>Date:</strong> {formatDate(event.startTime)}</div>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      ) : (
        <p className="mb-4">You're not hosting any events yet.</p>
      )}

      {/* Events user is attending */}
      <h2 className="mb-3">Events You're Attending</h2>
      {userEvents.attending.length > 0 ? (
        <div>
          {userEvents.attending.map((event) => (
            <Card
              key={event.id}
              hoverable
              className="mb-3"
              onClick={() => navigateToEvent(event.id)}
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
                    backgroundColor: 'var(--primary-color)',
                    color: 'white',
                    borderRadius: '4px',
                    fontSize: '0.8rem',
                    marginBottom: '10px'
                  }}>
                    {event.attendanceStatus || 'GOING'}
                  </div>
                  <div style={{ fontSize: '0.9rem' }}>
                    <div><strong>Date:</strong> {formatDate(event.startTime)}</div>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>
      ) : (
        <p>You're not attending any events yet.</p>
      )}

      <div className="mt-4">
        <Button variant="primary" onClick={() => navigate('/dashboard')}>
          Explore Events
        </Button>
      </div>
    </div>
  );
};

export default UserProfile;