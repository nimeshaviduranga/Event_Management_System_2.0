import PropTypes from 'prop-types';

/**
 * Button Component
 * 
 * A reusable button component with different variants
 * 
 * @param {Object} props
 * @param {string} props.type  - Button type (button, submit, reset)
 * @param {string} props.variant - Button variant (primary, secondary, outline, danger, success)
 * @param {boolean} props.fullWidth - Whether button should take up full container width
 * @param {boolean} props.disabled - Whether button is disabled
 * @param {function} props.onClick - Click handler function
 * @param {React.ReactNode} props.children - Button content
 * @param {Object} props.style - Additional inline styles
 * @param {string} props.className - Additional CSS classes
 */

// Default export for the Button component
const Button = ({
  type = 'button',
  variant = 'primary',
  fullWidth = false,
  disabled = false,
  onClick,
  children,
  style = {},
  className = '',
}) => {
  const baseClasses = 'btn';

  const variantClass = `btn-${variant}`;

  const widthClass = fullWidth ? 'w-100' : '';

  const combinedClasses = `${baseClasses} ${variantClass} ${widthClass} ${className}`.trim();

  const combinedStyles = {
    width: fullWidth ? '100%' : 'auto',
    opacity: disabled ? 0.7 : 1,
    cursor: disabled ? 'not-allowed' : 'pointer',
    ...style,
  };

  return (
    <button
      type={type}
      className={combinedClasses}
      disabled={disabled}
      onClick={onClick}
      style={combinedStyles}
    >
      {children}
    </button>
  );
};

// PropTypes for type checking
Button.propTypes = {
  type: PropTypes.oneOf(['button', 'submit', 'reset']),
  variant: PropTypes.oneOf(['primary', 'secondary', 'outline', 'danger', 'success']),
  fullWidth: PropTypes.bool,
  disabled: PropTypes.bool,
  onClick: PropTypes.func,
  children: PropTypes.node.isRequired,
  style: PropTypes.object,
  className: PropTypes.string,
};

export default Button;