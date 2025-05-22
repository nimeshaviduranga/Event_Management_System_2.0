import PropTypes from 'prop-types';

/**
 * Loader Component
 * 
 * Displays a loading spinner with optional text
 * 
 * @param {Object} props
 * @param {string} props.size - Size of the loader (small, medium, large)
 * @param {string} props.color - Color of the loader
 * @param {string} props.text - Optional text to display with the loader
 * @param {boolean} props.fullPage - Whether loader should take the full page
 */
const Loader = ({
  size = 'medium',
  color = 'var(--primary-color)',
  text = 'Loading...',
  fullPage = false
}) => {
  const sizeMap = {
    // Define sizes for the loader
    small: { width: '20px', height: '20px', borderWidth: '3px' },
    medium: { width: '30px', height: '30px', borderWidth: '4px' },
    large: { width: '40px', height: '40px', borderWidth: '5px' },
  };

  const sizeValues = sizeMap[size] || sizeMap.medium;
  // Fallback to medium size if invalid size is provided
  const spinnerStyle = {
    display: 'inline-block',
    width: sizeValues.width,
    height: sizeValues.height,
    border: `${sizeValues.borderWidth} solid rgba(0, 0, 0, 0.1)`,
    borderTopColor: color,
    borderRadius: '50%',
    animation: 'spin 1s linear infinite',
  };

  //Fallback to medium size if invalid size is provided
  const containerStyle = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px',
    ...(fullPage && {
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      backgroundColor: 'rgba(255, 255, 255, 0.8)',
      zIndex: 1000,
    }),
  };

  const styleTag = document.createElement('style');
  styleTag.innerHTML = `
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  `;
  document.head.appendChild(styleTag);

  return (
    <div style={containerStyle}>
      <div style={spinnerStyle}></div>
      {text && <p style={{ marginTop: '10px', color: 'var(--text-color)' }}>{text}</p>}
    </div>
  );
};

// PropTypes for type checking
Loader.propTypes = {
  size: PropTypes.oneOf(['small', 'medium', 'large']),
  color: PropTypes.string,
  text: PropTypes.string,
  fullPage: PropTypes.bool,
};

export default Loader;