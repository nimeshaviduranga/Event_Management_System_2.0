import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';

/**
 * Layout Component
 * This component serves as the main layout for the application.
 * @param {Object} props
 * @param {React.ReactNode} props.children - Child components to render in the layout
 */
const Layout = ({ children }) => {
  return (
    <div className="app-container">
      <header className="app-header">
        <div className="container d-flex justify-between align-center" style={{ padding: '16px 0' }}>
          <div className="logo">
            <Link to="/" style={{ color: 'var(--primary-color)', fontWeight: 'bold', fontSize: '1.5rem', textDecoration: 'none' }}>
              Event Management System
            </Link>
          </div>

          <nav className="main-nav">
            {/* Using Link instead of <a> to prevent page reload */}
            <Link to="/dashboard" className="nav-link" style={{ marginRight: '20px', color: 'var(--text-color)', textDecoration: 'none' }}>
              Dashboard
            </Link>
            <Link to="/events/create" className="nav-link" style={{ marginRight: '20px', color: 'var(--text-color)', textDecoration: 'none' }}>
              Create Event
            </Link>
            <Link to="/profile" className="nav-link" style={{ color: 'var(--text-color)', textDecoration: 'none' }}>
              Profile
            </Link>
          </nav>
        </div>
      </header>

      <main className="app-main" style={{ minHeight: 'calc(100vh - 120px)', padding: '20px 0' }}>
        {children}
      </main>

      <footer className="app-footer" style={{ backgroundColor: '#f5f5f5', padding: '20px 0', marginTop: '20px' }}>
        <div className="container">
          <p style={{ textAlign: 'center', color: 'var(--dark-gray)' }}>
            &copy; {new Date().getFullYear()} Event Management System
          </p>
        </div>
      </footer>
    </div>
  );
};

// PropTypes validation for component props
Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;