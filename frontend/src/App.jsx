import { Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layouts/Layout';

/**
 * These temporary components help to ensure routing works
 */
const Dashboard = () => <div className="container">Dashboard Page</div>;
const EventDetail = () => <div className="container">Event Detail Page</div>;
const EventForm = () => <div className="container">Create/Edit Event Form</div>;
const UserProfile = () => <div className="container">User Profile Page</div>;
const NotFound = () => <div className="container">404 - Page Not Found</div>;

/**
 * Main App Component
 * Sets up the routing structure for the entire application
 * Uses the Layout component to provide consistent UI around all routes
 */
function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />

        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/events/:id" element={<EventDetail />} />
        <Route path="/events/create" element={<EventForm />} />
        <Route path="/events/edit/:id" element={<EventForm />} />
        <Route path="/profile" element={<UserProfile />} />

        <Route path="*" element={<NotFound />} />
      </Routes>
    </Layout>
  );
}

export default App;