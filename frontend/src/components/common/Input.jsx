import { useField } from 'formik';
import PropTypes from 'prop-types';

/**
 * Input Component
 * 
 * A reusable input component integrated with Formik for form handling
 * 
 * @param {Object} props
 * @param {string} props.label - Input label
 * @param {string} props.type - Input type (text, email, password, etc.)
 * @param {string} props.name - Input name (for Formik)
 * @param {string} props.placeholder - Input placeholder
 * @param {boolean} props.required - Whether input is required
 * @param {Object} props.style - Additional inline styles
 * @param {string} props.className - Additional CSS classes
 * @param {Object} props.rest - Any other props to pass to the input element
 */
const Input = ({
  label,
  type = 'text',
  name,
  placeholder,
  required = false,
  style = {},
  className = '',
  ...rest
}) => {
  // Use Formik's useField hook to get field props and metadata
  const [field, meta] = useField(name);

  // Check if there is an error and if the field has been touched
  const hasError = meta.touched && meta.error;

  return (
    <div className="form-group">
      {label && (
        <label
          htmlFor={name}
          className="form-label"
          style={{ display: 'block', marginBottom: '4px' }}
        >
          {label} {required && <span style={{ color: 'var(--error-color)' }}>*</span>}
        </label>
      )}

      <input
        id={name}
        type={type}
        placeholder={placeholder}
        className={`form-control ${hasError ? 'is-invalid' : ''} ${className}`}
        style={{
          borderColor: hasError ? 'var(--error-color)' : 'var(--medium-gray)',
          ...style,
        }}
        {...field}
        {...rest}
      />

      {/* Display error message if there is one */}
      {hasError && (
        <div className="error-text" style={{ color: 'var(--error-color)', fontSize: '0.875rem', marginTop: '4px' }}>
          {meta.error}
        </div>
      )}
    </div>
  );
};

// PropTypes for type checking
Input.propTypes = {
  label: PropTypes.string,
  type: PropTypes.string,
  name: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  required: PropTypes.bool,
  style: PropTypes.object,
  className: PropTypes.string,
};

export default Input;