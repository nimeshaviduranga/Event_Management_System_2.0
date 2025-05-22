import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layouts/Layout';
import { lazy, Suspense } from 'react';
import Loader from './components/common/Loader';


/**
 * Lazy load components to improve initial load performance
 * Components will only be loaded when they are needed
 */
const Dashboard = lazy(() => import('./pages/Dashboard'));
const EventDetail = lazy(() => import('./pages/EventDetail'));
const EventForm = lazy(() => import('./pages/EventForm'));
const UserProfile = lazy(() => import('./pages/UserProfile'));
const Login = lazy(() => import('./pages/Login'));
const Register = lazy(() => import('./pages/Register'));
const NotFound = () => <div className="container">404 - Page Not Found</div>;

/**
 * Main App Component
 * Sets up the routing structure for the entire application
 * Uses the Layout component to provide consistent UI around all routes
 */
function App() {
  return (
    <Layout>
      <Suspense fallback={<Loader />}>
        <Routes>
          {/* Redirect root path to dashboard */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />

          {/* Authentication routes */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Main application routes */}
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/events/:id" element={<EventDetail />} />
          <Route path="/events/create" element={<EventForm />} />
          <Route path="/events/edit/:id" element={<EventForm />} />
          <Route path="/profile" element={<UserProfile />} />

          {/* Catch-all route for 404 errors */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </Suspense>
    </Layout>
  );
}

export default App;