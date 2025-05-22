import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import {
  fetchEventById,
  attendEvent,
  unattendEvent,
  deleteEvent
} from '../features/events/eventsSlice';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import Loader from '../components/common/Loader';
import Alert from '../components/common/Alert';

/**
 * EventDetail Page
 * 
 * Displays detailed information about a specific event
 * Allows users to attend/unattend events and hosts to edit/delete
 */
const EventDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  // Get event data and auth state from redux store
  const { selectedEvent, status, error } = useSelector((state) => state.events);
  const { user, isAuthenticated } = useSelector((state) => state.auth);

  // Local state for attendance status and confirmations
  const [isAttending, setIsAttending] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [actionStatus, setActionStatus] = useState({
    loading: false,
    success: null,
    error: null,
  });

  // Fetch event on component mount
  useEffect(() => {
    dispatch(fetchEventById(id));
  }, [dispatch, id]);

  // Update attendance status when event data changes
  useEffect(() => {
    if (selectedEvent && user) {
      // Check if current user is in the attendees list
      setIsAttending(selectedEvent.attendees?.some(a => a.userId === user.id) || false);
    }
  }, [selectedEvent, user]);

  // Check if the current user is the host of the event
  const isHost = selectedEvent && user && selectedEvent.hostId === user.id;

  // Format date for display
  const formatDateTime = (dateString) => {
    if (!dateString) return '';
    const options = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  // Handle attend/unattend event
  const handleAttendanceToggle = async () => {
    if (!isAuthenticated) {
      // Redirect to login if not authenticated
      navigate('/login', { state: { from: `/events/${id}` } });
      return;
    }

    setActionStatus({ loading: true, success: null, error: null });

    try {
      if (isAttending) {
        await dispatch(unattendEvent(id)).unwrap();
        setActionStatus({
          loading: false,
          success: 'You are no longer attending this event',
          error: null
        });
      } else {
        await dispatch(attendEvent(id)).unwrap();
        setActionStatus({
          loading: false,
          success: 'You are now attending this event',
          error: null
        });
      }
      setIsAttending(!isAttending);
    } catch (err) {
      setActionStatus({
        loading: false,
        success: null,
        error: err.message || 'Failed to update attendance'
      });
    }
  };

  // Navigate to edit event page
  const handleEditEvent = () => {
    navigate(`/events/edit/${id}`);
  };

  // Handle event deletion
  const handleDeleteEvent = async () => {
    setActionStatus({ loading: true, success: null, error: null });

    try {
      await dispatch(deleteEvent(id)).unwrap();
      setActionStatus({
        loading: false,
        success: 'Event deleted successfully',
        error: null
      });

      // Redirect to dashboard after short delay
      setTimeout(() => {
        navigate('/dashboard');
      }, 1500);
    } catch (err) {
      setActionStatus({
        loading: false,
        success: null,
        error: err.message || 'Failed to delete event'
      });
      setShowDeleteConfirm(false);
    }
  };

  // Show loading state
  if (status === 'loading') {
    return <Loader text="Loading event details..." />;
  }

  // Show error state
  if (status === 'failed' || !selectedEvent) {
    return (
      <div className="container">
        <Alert
          type="error"
          message={error || 'Event not found'}
          dismissible={false}
        />
        <Button
          variant="primary"
          onClick={() => navigate('/dashboard')}
          style={{ marginTop: '20px' }}
        >
          Back to Dashboard
        </Button>
      </div>
    );
  }

  return (
    <div className="container">
      {/* Success/error messages */}
      {actionStatus.success && (
        <Alert
          type="success"
          message={actionStatus.success}
          autoClose={3000}
        />
      )}

      {actionStatus.error && (
        <Alert
          type="error"
          message={actionStatus.error}
        />
      )}

      {/* Event header with actions */}
      <div className="d-flex justify-between align-center mb-4">
        <h1>{selectedEvent.title}</h1>
        <div>
          {isHost ? (
            <div>
              <Button
                variant="outline"
                onClick={handleEditEvent}
                style={{ marginRight: '10px' }}
              >
                Edit Event
              </Button>
              <Button
                variant="danger"
                onClick={() => setShowDeleteConfirm(true)}
              >
                Delete Event
              </Button>
            </div>
          ) : (
            <Button
              variant={isAttending ? 'outline' : 'primary'}
              onClick={handleAttendanceToggle}
              disabled={actionStatus.loading}
            >
              {actionStatus.loading ? 'Processing...' : isAttending ? 'Cancel Attendance' : 'Attend Event'}
            </Button>
          )}
        </div>
      </div>

      {/* Event details card */}
      <Card className="mb-4">
        <div className="row mb-4">
          <div className="col">
            <h3 className="mb-2">Details</h3>
            <p className="mb-3">{selectedEvent.description}</p>

            <div className="mb-3">
              <strong>Location:</strong> {selectedEvent.location}
            </div>

            <div className="mb-3">
              <strong>Start Time:</strong> {formatDateTime(selectedEvent.startTime)}
            </div>

            <div className="mb-3">
              <strong>End Time:</strong> {formatDateTime(selectedEvent.endTime)}
            </div>

            <div className="mb-3">
              <strong>Visibility:</strong>{' '}
              <span style={{
                display: 'inline-block',
                padding: '2px 8px',
                backgroundColor: selectedEvent.visibility === 'PUBLIC' ? 'var(--success-color)' : 'var(--warning-color)',
                color: 'white',
                borderRadius: '4px',
                fontSize: '0.8rem'
              }}>
                {selectedEvent.visibility}
              </span>
            </div>

            <div>
              <strong>Host:</strong> {selectedEvent.hostName || 'Unknown'}
            </div>
          </div>
        </div>

        {/* Attendees section */}
        <div>
          <h3 className="mb-2">Attendees ({selectedEvent.attendees?.length || 0})</h3>
          {selectedEvent.attendees && selectedEvent.attendees.length > 0 ? (
            <div>
              {selectedEvent.attendees.map((attendee) => (
                <div
                  key={attendee.userId}
                  className="d-flex justify-between align-center"
                  style={{
                    padding: '10px',
                    borderBottom: '1px solid var(--medium-gray)',
                  }}
                >
                  <div>{attendee.userName || 'Anonymous User'}</div>
                  <div style={{
                    padding: '2px 8px',
                    backgroundColor:
                      attendee.status === 'GOING' ? 'var(--success-color)' :
                        attendee.status === 'MAYBE' ? 'var(--warning-color)' :
                          'var(--error-color)',
                    color: 'white',
                    borderRadius: '4px',
                    fontSize: '0.8rem'
                  }}>
                    {attendee.status}
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p>No attendees yet. Be the first to attend!</p>
          )}
        </div>
      </Card>

      {/* Delete confirmation modal */}
      {showDeleteConfirm && (
        <div className="modal-overlay" style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000
        }}>
          <div className="modal-content" style={{
            backgroundColor: 'white',
            padding: '20px',
            borderRadius: '4px',
            width: '90%',
            maxWidth: '500px'
          }}>
            <h3 className="mb-3">Confirm Delete</h3>
            <p className="mb-4">Are you sure you want to delete this event? This action cannot be undone.</p>
            <div className="d-flex justify-end">
              <Button
                variant="outline"
                onClick={() => setShowDeleteConfirm(false)}
                style={{ marginRight: '10px' }}
              >
                Cancel
              </Button>
              <Button
                variant="danger"
                onClick={handleDeleteEvent}
                disabled={actionStatus.loading}
              >
                {actionStatus.loading ? 'Deleting...' : 'Delete Event'}
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default EventDetail;