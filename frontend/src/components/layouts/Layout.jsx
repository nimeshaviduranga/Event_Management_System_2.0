import { Link, useNavigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../../features/auth/authSlice';
import Button from '../common/Button';
/**
 * Layout Component
 * This component serves as the main layout for the application.
 * @param {Object} props
 * @param {React.ReactNode} props.children - Child components to render in the layout
 */
const Layout = ({ children }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  // Get authentication state from Redux store
  const { isAuthenticated, user } = useSelector((state) => state.auth);

  // Handle logout
  const handleLogout = () => {
    dispatch(logout());
    navigate('/login');
  };
  return (
    <div className="app-container">
      <header className="app-header" style={{
        borderBottom: '1px solid var(--medium-gray)',
        marginBottom: '20px'
      }}>
        <div className="container d-flex justify-between align-center" style={{ padding: '16px 0' }}>
          <div className="logo">
            <Link to="/" style={{ color: 'var(--primary-color)', fontWeight: 'bold', fontSize: '1.5rem', textDecoration: 'none' }}>
              Event Management System
            </Link>
          </div>

          <nav className="main-nav">

            {/* Navigation for authenticated users */}
            {isAuthenticated ? (
              <div className="d-flex align-center">
                <Link to="/dashboard" className="nav-link" style={{ marginRight: '20px', color: 'var(--text-color)', textDecoration: 'none' }}>
                  Dashboard
                </Link>
                <Link to="/events/create" className="nav-link" style={{ marginRight: '20px', color: 'var(--text-color)', textDecoration: 'none' }}>
                  Create Event
                </Link>
                <Link to="/profile" className="nav-link" style={{ color: 'var(--text-color)', textDecoration: 'none' }}>
                  Profile
                </Link>
                <div style={{ display: 'flex', alignItems: 'center' }}>
                  <span style={{ marginRight: '10px', color: 'var(--text-color)' }}>
                    {user?.name || 'User'}
                  </span>
                  <Button variant="outline" onClick={handleLogout}>
                    Logout
                  </Button>
                </div>
              </div>
            ) : (
              /* Navigation for non-authenticated users */
              <div>
                <Link to="/login" className="nav-link" style={{ marginRight: '20px', color: 'var(--text-color)', textDecoration: 'none' }}>
                  Login
                </Link>
                <Link to="/register" className="nav-link" style={{ color: 'var(--text-color)', textDecoration: 'none' }}>
                  Register
                </Link>
              </div>
            )}
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