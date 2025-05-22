import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import { register, clearError } from '../features/auth/authSlice';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import Alert from '../components/common/Alert';

/**
 * Register Page
 * 
 * Handles user registration with name, email and password
 * Uses Formik for form handling and validation
 */
const Register = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { isAuthenticated, status, error } = useSelector((state) => state.auth);

  const [formSubmitted, setFormSubmitted] = useState(false);

  useEffect(() => {
    dispatch(clearError());
  }, [dispatch]);

  // Redirect if registration successful and authenticated
  useEffect(() => {
    if (isAuthenticated && formSubmitted) {
      navigate('/dashboard', { replace: true });
    }
  }, [isAuthenticated, formSubmitted, navigate]);

  // Initial form values
  const initialValues = {
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
  };

  // Form validation schema
  const validationSchema = Yup.object({
    name: Yup.string()
      .required('Name is required')
      .min(2, 'Name must be at least 2 characters')
      .max(50, 'Name must be 50 characters or less'),
    email: Yup.string()
      .email('Invalid email address')
      .required('Email is required'),
    password: Yup.string()
      .required('Password is required')
      .min(6, 'Password must be at least 6 characters'),
    confirmPassword: Yup.string()
      .oneOf([Yup.ref('password'), null], 'Passwords must match')
      .required('Confirm password is required'),
  });

  // Form submission handler
  const handleSubmit = async (values, { setSubmitting }) => {
    // Remove confirmPassword before sending to API
    const { confirmPassword, ...userData } = values;

    await dispatch(register(userData));
    setFormSubmitted(true);
    setSubmitting(false);
  };

  return (
    <div className="container" style={{ maxWidth: '500px', margin: '40px auto' }}>
      <h1 className="text-center mb-4">Register</h1>

      {error && <Alert type="error" message={error} />}

      <Card>
        <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
        >
          {({ isSubmitting }) => (
            <Form>
              <Input
                label="Name"
                name="name"
                type="text"
                placeholder="Enter your name"
                required
              />

              <Input
                label="Email"
                name="email"
                type="email"
                placeholder="Enter your email"
                required
              />

              <Input
                label="Password"
                name="password"
                type="password"
                placeholder="Enter your password"
                required
              />

              <Input
                label="Confirm Password"
                name="confirmPassword"
                type="password"
                placeholder="Confirm your password"
                required
              />

              <div className="d-flex justify-end mt-4">
                <Button
                  type="submit"
                  variant="primary"
                  fullWidth
                  disabled={isSubmitting || status === 'loading'}
                >
                  {isSubmitting || status === 'loading' ? 'Registering...' : 'Register'}
                </Button>
              </div>
            </Form>
          )}
        </Formik>
      </Card>

      <div className="text-center mt-3">
        <p>
          Already have an account?{' '}
          <Link to="/login" style={{ color: 'var(--primary-color)' }}>
            Login here
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Register;