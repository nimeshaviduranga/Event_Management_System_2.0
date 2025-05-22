import PropTypes from 'prop-types';

/**
 * Card Component
 * 
 * A reusable card container for displaying content
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Card content
 * @param {string} props.title - Optional card title
 * @param {React.ReactNode} props.footer - Optional card footer
 * @param {boolean} props.hoverable - Whether card has hover effects
 * @param {Object} props.style - Additional inline styles
 * @param {string} props.className - Additional CSS classes
 */
const Card = ({
  children,
  title,
  footer,
  hoverable = false,
  style = {},
  className = '',
}) => {
  // Base styles
  const cardStyle = {
    backgroundColor: 'white',
    borderRadius: '4px',
    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
    transition: 'box-shadow 0.3s, transform 0.3s',
    overflow: 'hidden',
    ...style,
  };

  // Hover styles
  if (hoverable) {
    cardStyle.cursor = 'pointer';
    cardStyle[':hover'] = {
      boxShadow: '0 4px 8px rgba(0, 0, 0, 0.15)',
      transform: 'translateY(-2px)',
    };
  }

  // Title styles
  const titleStyle = {
    padding: '16px',
    borderBottom: title ? '1px solid var(--medium-gray)' : 'none',
    fontWeight: 'bold',
    fontSize: '1.1rem',
  };

  // Content styles
  const contentStyle = {
    padding: '16px',
  };

  // Footer styles
  const footerStyle = {
    padding: '16px',
    borderTop: '1px solid var(--medium-gray)',
    backgroundColor: 'var(--light-gray)',
  };

  return (
    <div className={`card ${className}`} style={cardStyle}>
      {title && <div className="card-title" style={titleStyle}>{title}</div>}
      <div className="card-content" style={contentStyle}>{children}</div>
      {footer && <div className="card-footer" style={footerStyle}>{footer}</div>}
    </div>
  );
};

// PropTypes for type checking
Card.propTypes = {
  children: PropTypes.node.isRequired,
  title: PropTypes.oneOfType([PropTypes.string, PropTypes.node]),
  footer: PropTypes.node,
  hoverable: PropTypes.bool,
  style: PropTypes.object,
  className: PropTypes.string,
};

export default Card;