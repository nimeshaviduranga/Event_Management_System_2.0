import PropTypes from 'prop-types';
import { useState, useEffect } from 'react';

/**
 * Alert Component
 * 
 * Displays alert messages with different types and auto-dismiss option
 * 
 * @param {Object} props
 * @param {string} props.type - Alert type (success, error, warning, info)
 * @param {string} props.message - Alert message
 * @param {boolean} props.dismissible - Whether alert can be dismissed
 * @param {number} props.autoClose - Time in ms to auto-close the alert (0 means no auto-close)
 * @param {function} props.onClose - Function to call when alert is closed
 */
const Alert = ({
  type = 'info',
  message,
  dismissible = true,
  autoClose = 0,
  onClose = () => { },
}) => {
  // State to track if the alert is visible
  const [visible, setVisible] = useState(true);

  // Type to color mapping
  const typeColors = {
    success: {
      bg: '#d4edda',
      text: '#155724',
      border: '#c3e6cb',
      icon: '✓',
    },
    error: {
      bg: '#f8d7da',
      text: '#721c24',
      border: '#f5c6cb',
      icon: '✕',
    },
    warning: {
      bg: '#fff3cd',
      text: '#856404',
      border: '#ffeeba',
      icon: '⚠',
    },
    info: {
      bg: '#d1ecf1',
      text: '#0c5460',
      border: '#bee5eb',
      icon: 'ℹ',
    },
  };

  // Get color scheme based on type
  const colors = typeColors[type] || typeColors.info;

  // Auto-close effect
  useEffect(() => {
    if (autoClose > 0) {
      const timer = setTimeout(() => {
        handleClose();
      }, autoClose);

      // Clean up timer on unmount
      return () => clearTimeout(timer);
    }
  }, [autoClose]);

  // Handle close
  const handleClose = () => {
    setVisible(false);
    onClose();
  };

  // If not visible, don't render
  if (!visible) return null;

  return (
    <div
      style={{
        backgroundColor: colors.bg,
        color: colors.text,
        border: `1px solid ${colors.border}`,
        borderRadius: '4px',
        padding: '12px 16px',
        marginBottom: '16px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}
      role="alert"
    >
      <div style={{ display: 'flex', alignItems: 'center' }}>
        <span style={{ marginRight: '8px', fontWeight: 'bold' }}>{colors.icon}</span>
        <span>{message}</span>
      </div>

      {dismissible && (
        <button
          type="button"
          onClick={handleClose}
          style={{
            background: 'transparent',
            border: 'none',
            cursor: 'pointer',
            fontSize: '1.2rem',
            color: colors.text,
            opacity: 0.7,
          }}
          aria-label="Close"
        >
          &times;
        </button>
      )}
    </div>
  );
};

// PropTypes for type checking
//
Alert.propTypes = {
  type: PropTypes.oneOf(['success', 'error', 'warning', 'info']),
  message: PropTypes.string.isRequired,
  dismissible: PropTypes.bool,
  autoClose: PropTypes.number,
  onClose: PropTypes.func,
};

export default Alert;