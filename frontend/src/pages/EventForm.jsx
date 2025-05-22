import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useParams } from 'react-router-dom';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import {
  fetchEventById,
  createEvent,
  updateEvent,
  clearSelectedEvent
} from '../features/events/eventsSlice';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import Loader from '../components/common/Loader';
import Alert from '../components/common/Alert';

/**
 * EventForm Page
 * 
 * Page for creating or editing an event
 * Uses Formik for form handling and Yup for validation
 */
const EventForm = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { id } = useParams(); // Get event ID from URL if editing

  // Get event and status from redux store
  const { selectedEvent, status, error } = useSelector((state) => state.events);

  // Local state for API status and messages
  const [formStatus, setFormStatus] = useState({
    submitting: false,
    success: false,
    error: null,
  });

  // Determine if we're editing an existing event
  const isEditing = !!id;

  // Fetch event data if editing
  useEffect(() => {
    if (isEditing) {
      dispatch(fetchEventById(id));
    } else {
      // Clear selected event if creating a new one
      dispatch(clearSelectedEvent());
    }

    // Cleanup on unmount
    return () => {
      dispatch(clearSelectedEvent());
    };
  }, [dispatch, id, isEditing]);

  // Initial form values
  const getInitialValues = () => {
    if (isEditing && selectedEvent) {
      return {
        title: selectedEvent.title || '',
        description: selectedEvent.description || '',
        location: selectedEvent.location || '',
        startTime: selectedEvent.startTime
          ? new Date(selectedEvent.startTime).toISOString().substring(0, 16)
          : '',
        endTime: selectedEvent.endTime
          ? new Date(selectedEvent.endTime).toISOString().substring(0, 16)
          : '',
        visibility: selectedEvent.visibility || 'PUBLIC',
      };
    }

    return {
      title: '',
      description: '',
      location: '',
      startTime: '',
      endTime: '',
      visibility: 'PUBLIC',
    };
  };

  // Form validation schema
  const validationSchema = Yup.object({
    title: Yup.string()
      .required('Title is required')
      .max(100, 'Title must be 100 characters or less'),
    description: Yup.string()
      .required('Description is required')
      .max(500, 'Description must be 500 characters or less'),
    location: Yup.string()
      .required('Location is required')
      .max(100, 'Location must be 100 characters or less'),
    startTime: Yup.date()
      .required('Start time is required'),
    endTime: Yup.date()
      .required('End time is required')
      .min(
        Yup.ref('startTime'),
        'End time must be after start time'
      ),
    visibility: Yup.string()
      .required('Visibility is required')
      .oneOf(['PUBLIC', 'PRIVATE'], 'Visibility must be either PUBLIC or PRIVATE'),
  });

  // Form submission handler
  const handleSubmit = async (values, { setSubmitting, resetForm }) => {
    setFormStatus({
      submitting: true,
      success: false,
      error: null,
    });

    try {
      if (isEditing) {
        // Update existing event
        await dispatch(updateEvent({ id, eventData: values })).unwrap();
      } else {
        // Create new event
        await dispatch(createEvent(values)).unwrap();
        resetForm();
      }

      setFormStatus({
        submitting: false,
        success: true,
        error: null,
      });

      // Redirect to dashboard after short delay
      setTimeout(() => {
        navigate('/dashboard');
      }, 1500);
    } catch (err) {
      setFormStatus({
        submitting: false,
        success: false,
        error: err.message || 'Failed to save event',
      });
    } finally {
      setSubmitting(false);
    }
  };

  // Handle cancel button
  const handleCancel = () => {
    navigate(-1); // Go back to previous page
  };

  // Show loading state if fetching event data
  if (isEditing && status === 'loading') {
    return <Loader text="Loading event data..." />;
  }

  // Show error if event not found
  if (isEditing && status === 'failed') {
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
      <h1 className="mb-4">{isEditing ? 'Edit Event' : 'Create New Event'}</h1>

      {/* Success message */}
      {formStatus.success && (
        <Alert
          type="success"
          message={isEditing ? 'Event updated successfully!' : 'Event created successfully!'}
          autoClose={3000}
        />
      )}

      {/* Error message */}
      {formStatus.error && (
        <Alert
          type="error"
          message={formStatus.error}
        />
      )}

      <Card>
        <Formik
          initialValues={getInitialValues()}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
          enableReinitialize
        >
          {({ isSubmitting, dirty, isValid }) => (
            <Form>
              <Input
                label="Title"
                name="title"
                type="text"
                placeholder="Enter event title"
                required
              />

              <Input
                label="Description"
                name="description"
                type="textarea"
                placeholder="Enter event description"
                required
                as="textarea"
                rows="4"
                style={{ resize: 'vertical' }}
              />

              <Input
                label="Location"
                name="location"
                type="text"
                placeholder="Enter event location"
                required
              />

              <div className="row">
                <div className="col">
                  <Input
                    label="Start Time"
                    name="startTime"
                    type="datetime-local"
                    required
                  />
                </div>
                <div className="col">
                  <Input
                    label="End Time"
                    name="endTime"
                    type="datetime-local"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">
                  Visibility <span style={{ color: 'var(--error-color)' }}>*</span>
                </label>
                <div>
                  <label style={{ marginRight: '20px' }}>
                    <input
                      type="radio"
                      name="visibility"
                      value="PUBLIC"
                      style={{ marginRight: '5px' }}
                    />
                    Public
                  </label>
                  <label>
                    <input
                      type="radio"
                      name="visibility"
                      value="PRIVATE"
                      style={{ marginRight: '5px' }}
                    />
                    Private
                  </label>
                </div>
              </div>

              <div className="d-flex justify-end" style={{ marginTop: '20px' }}>
                <Button
                  type="button"
                  variant="outline"
                  onClick={handleCancel}
                  style={{ marginRight: '10px' }}
                >
                  Cancel
                </Button>
                <Button
                  type="submit"
                  variant="primary"
                  disabled={isSubmitting || !(dirty && isValid)}
                >
                  {isSubmitting ? 'Saving...' : isEditing ? 'Update Event' : 'Create Event'}
                </Button>
              </div>
            </Form>
          )}
        </Formik>
      </Card>
    </div>
  );
};

export default EventForm;