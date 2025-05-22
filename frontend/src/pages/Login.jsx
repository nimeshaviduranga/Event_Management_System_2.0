import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { Formik, Form } from 'formik';
import * as Yup from 'yup';
import { login, clearError } from '../features/auth/authSlice';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Card from '../components/common/Card';
import Alert from '../components/common/Alert';

/**
 * Login Page
 * 
 * Handles user authentication with email and password
 * Uses Formik for form handling and validation
 */
const Login = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  // Get auth state from redux store
  const { isAuthenticated, status, error } = useSelector((state) => state.auth);

  // Get redirect path from location state or default to dashboard
  const from = location.state?.from || '/dashboard';

  // Local state for form submission
  const [formSubmitted, setFormSubmitted] = useState(false);

  // Clear any auth errors when component mounts
  useEffect(() => {
    dispatch(clearError());
  }, [dispatch]);

  useEffect(() => {
    if (isAuthenticated && formSubmitted) {
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, formSubmitted, navigate, from]);

  const initialValues = {
    email: '',
    password: '',
  };

  // Form validation schema
  const validationSchema = Yup.object({
    email: Yup.string()
      .email('Invalid email address')
      .required('Email is required'),
    password: Yup.string()
      .required('Password is required')
      .min(6, 'Password must be at least 6 characters'),
  });

  // Form submission handler
  const handleSubmit = async (values, { setSubmitting }) => {
    await dispatch(login(values));
    setFormSubmitted(true);
    setSubmitting(false);
  };

  return (
    <div className="container" style={{ maxWidth: '500px', margin: '40px auto' }}>
      <h1 className="text-center mb-4">Login</h1>

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

              <div className="d-flex justify-end mt-4">
                <Button
                  type="submit"
                  variant="primary"
                  fullWidth
                  disabled={isSubmitting || status === 'loading'}
                >
                  {isSubmitting || status === 'loading' ? 'Logging in...' : 'Login'}
                </Button>
              </div>
            </Form>
          )}
        </Formik>
      </Card>

      <div className="text-center mt-3">
        <p>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--primary-color)' }}>
            Register here
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;