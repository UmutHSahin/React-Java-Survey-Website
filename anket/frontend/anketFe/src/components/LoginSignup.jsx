import React, { useState } from 'react';
import { Eye, EyeOff, Mail, Lock, ArrowLeft } from 'lucide-react';
import Navbar from '../components/Navbar';
import { loginUser, registerUser, createAdminUser } from '../services/api';
import './LoginSignup.css';

// Login Page Component
const LoginPage = ({ onSwitchToSignup }) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState(''); // 'success', 'error', or 'info'

  const showMessage = (text, type) => {
    setMessage(text);
    setMessageType(type);
    setTimeout(() => {
      setMessage('');
      setMessageType('');
    }, 4000);
  };

  const handleLogin = async () => {
    // Clear previous messages
    setMessage('');
    
    if (!email || !password) {
      showMessage('Please fill in all fields', 'error');
      return;
    }

    // Basic email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      showMessage('Please enter a valid email address', 'error');
      return;
    }

    setIsLoading(true);
    showMessage('Signing in...', 'info');
    
    try {
      // Real API call to backend
      const response = await loginUser(email, password);
      
      showMessage('Login successful! Redirecting...', 'success');
      
      // Check if user is admin and redirect accordingly
      setTimeout(() => {
        if (response.user && response.user.role === 'ADMIN') {
          console.log('👑 Admin user detected, redirecting to admin panel');
          console.log('User role:', response.user.role);
          window.location.href = '/admin';
        } else {
          console.log('👤 Regular user, redirecting to homepage');
          console.log('User role:', response.user?.role);
          window.location.href = '/homepage';
        }
      }, 1000);
      
    } catch (error) {
      console.error('Login error:', error);
      showMessage(error.message || 'Login failed. Please try again.', 'error');
      setIsLoading(false);
    }
  };

  const handleForgotPassword = () => {
    showMessage('Forgot password functionality will be implemented later', 'info');
  };

  const handleAdminSetup = async () => {
    setIsLoading(true);
    showMessage('Creating admin user...', 'info');
    
    try {
      const result = await createAdminUser();
      
      showMessage(`Admin user created successfully! Email: ${result.adminEmail}, Password: ${result.adminPassword}`, 'success');
      
      // Auto-fill the login form with admin credentials
      setEmail(result.adminEmail);
      setPassword(result.adminPassword);
      
      // Show instructions
      setTimeout(() => {
        showMessage('Admin user created! You can now login with the credentials above.', 'success');
      }, 2000);
      
    } catch (error) {
      console.error('Admin setup error:', error);
      showMessage(`Failed to create admin user: ${error.message}`, 'error');
    } finally {
      setIsLoading(false);
    }
  };

  // Handle Enter key press for login
  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleLogin();
    }
  };

  return (
    <div className="page-background">
      <div className="main-container">
        <div className="page-header">
          <h2 className="page-title">survet</h2>
          <p className="page-subtitle">Sign in to your account</p>
        </div>
        
        <div className="card">
          {/* Message Display */}
          {message && (
            <div className={`message ${
              messageType === 'success' ? 'message-success' :
              messageType === 'error' ? 'message-error' :
              'message-info'
            }`}>
              {message}
            </div>
          )}

          <div className="form-container">
            {/* Admin Setup Button - Only show if no users exist */}
            <div className="admin-setup-section">
              <button 
                type="button"
                className="admin-setup-btn"
                onClick={handleAdminSetup}
              >
                👑 Setup Admin User
              </button>
              <p className="admin-setup-text">Create the first admin user for the system</p>
            </div>

            {/* Email Field */}
            <div className="input-group">
              <label htmlFor="email" className="input-label">
                Email Address
              </label>
              <div className="input-container">
                <div className="input-icon">
                  <Mail />
                </div>
                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  onKeyPress={handleKeyPress}
                  className="form-input form-input-with-icon"
                  placeholder="Enter your email"
                />
              </div>
            </div>

            {/* Password Field */}
            <div className="input-group">
              <label htmlFor="password" className="input-label">
                Password
              </label>
              <div className="input-container">
                <div className="input-icon">
                  <Lock />
                </div>
                <input
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  onKeyPress={handleKeyPress}
                  className="form-input form-input-with-icon form-input-with-toggle"
                  placeholder="Enter your password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="password-toggle"
                >
                  {showPassword ? <EyeOff /> : <Eye />}
                </button>
              </div>
            </div>

            {/* Forgot Password Link */}
            <div className="text-right">
              <button
                type="button"
                onClick={handleForgotPassword}
                className="link"
              >
                I forgot my password
              </button>
            </div>

            {/* Login Button */}
            <button
              type="button"
              onClick={handleLogin}
              disabled={isLoading}
              className="btn btn-primary"
            >
              {isLoading ? 'Signing in...' : 'Log in'}
            </button>

            {/* Create Account Button */}
            <button
              type="button"
              onClick={onSwitchToSignup}
              className="btn btn-secondary"
            >
              Create account
            </button>
          </div>
        </div>

        {/* Backend Integration Info */}
        <div className="text-center">
          <p className="text-xs text-gray-500">
            Connected to Backend API
          </p>
        </div>
      </div>
    </div>
  );
};

// Create Account Page Component
const CreateAccountPage = ({ onSwitchToLogin }) => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const showMessage = (text, type) => {
    setMessage(text);
    setMessageType(type);
    setTimeout(() => {
      setMessage('');
      setMessageType('');
    }, 4000);
  };

  const handleInputChange = (field, value) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleCreateAccount = async () => {
    setMessage('');
    
    const { firstName, lastName, email, password, confirmPassword } = formData;

    // Validation
    if (!firstName || !lastName || !email || !password || !confirmPassword) {
      showMessage('Please fill in all fields', 'error');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      showMessage('Please enter a valid email address', 'error');
      return;
    }

    if (password.length < 6) {
      showMessage('Password must be at least 6 characters long', 'error');
      return;
    }

    if (password !== confirmPassword) {
      showMessage('Passwords do not match', 'error');
      return;
    }

    setIsLoading(true);
    showMessage('Creating account...', 'info');
    
    try {
      // Real API call to backend
      const response = await registerUser({
        firstName,
        lastName,
        email,
        password,
        confirmPassword
      });
      
      showMessage('Account created successfully! Redirecting...', 'success');
      
      // Redirect to home page after successful registration
      setTimeout(() => {
        window.location.href = '/homepage';
      }, 1000);
      
    } catch (error) {
      console.error('Registration error:', error);
      showMessage(error.message || 'Registration failed. Please try again.', 'error');
      setIsLoading(false);
    }
  };

  return (
    <div className="page-background">
      <div className="main-container">
        <div className="page-header">
          <h2 className="page-title">survet</h2>
          <p className="page-subtitle">Create your account</p>
        </div>
        
        <div className="card">
          {/* Back to Login Button */}
          <button
            onClick={onSwitchToLogin}
            className="back-button"
          >
            <ArrowLeft />
            Back to Login
          </button>

          {/* Message Display */}
          {message && (
            <div className={`message ${
              messageType === 'success' ? 'message-success' :
              messageType === 'error' ? 'message-error' :
              'message-info'
            }`}>
              {message}
            </div>
          )}

          <div className="form-container">
            {/* First Name Field */}
            <div className="input-group">
              <label htmlFor="firstName" className="input-label">
                First Name
              </label>
              <input
                id="firstName"
                type="text"
                value={formData.firstName}
                onChange={(e) => handleInputChange('firstName', e.target.value)}
                className="form-input"
                placeholder="Enter your first name"
              />
            </div>

            {/* Last Name Field */}
            <div className="input-group">
              <label htmlFor="lastName" className="input-label">
                Last Name
              </label>
              <input
                id="lastName"
                type="text"
                value={formData.lastName}
                onChange={(e) => handleInputChange('lastName', e.target.value)}
                className="form-input"
                placeholder="Enter your last name"
              />
            </div>

            {/* Email Field */}
            <div className="input-group">
              <label htmlFor="signupEmail" className="input-label">
                Email Address
              </label>
              <div className="input-container">
                <div className="input-icon">
                  <Mail />
                </div>
                <input
                  id="signupEmail"
                  type="email"
                  value={formData.email}
                  onChange={(e) => handleInputChange('email', e.target.value)}
                  className="form-input form-input-with-icon"
                  placeholder="Enter your email"
                />
              </div>
            </div>

            {/* Password Field */}
            <div className="input-group">
              <label htmlFor="signupPassword" className="input-label">
                Password
              </label>
              <div className="input-container">
                <div className="input-icon">
                  <Lock />
                </div>
                <input
                  id="signupPassword"
                  type={showPassword ? 'text' : 'password'}
                  value={formData.password}
                  onChange={(e) => handleInputChange('password', e.target.value)}
                  className="form-input form-input-with-icon form-input-with-toggle"
                  placeholder="Enter your password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="password-toggle"
                >
                  {showPassword ? <EyeOff /> : <Eye />}
                </button>
              </div>
            </div>

            {/* Confirm Password Field */}
            <div className="input-group">
              <label htmlFor="confirmPassword" className="input-label">
                Confirm Password
              </label>
              <div className="input-container">
                <div className="input-icon">
                  <Lock />
                </div>
                <input
                  id="confirmPassword"
                  type={showConfirmPassword ? 'text' : 'password'}
                  value={formData.confirmPassword}
                  onChange={(e) => handleInputChange('confirmPassword', e.target.value)}
                  className="form-input form-input-with-icon form-input-with-toggle"
                  placeholder="Confirm your password"
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                  className="password-toggle"
                >
                  {showConfirmPassword ? <EyeOff /> : <Eye />}
                </button>
              </div>
            </div>

            {/* Create Account Button */}
            <button
              type="button"
              onClick={handleCreateAccount}
              disabled={isLoading}
              className="btn btn-primary"
            >
              {isLoading ? 'Creating Account...' : 'Create Account'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

// Main App Component
const LoginSignup = () => {
  const [currentPage, setCurrentPage] = useState('login'); // 'login' or 'signup'

  return (
    <div className="min-h-screen">
      <Navbar />
      
      {currentPage === 'login' ? (
        <LoginPage onSwitchToSignup={() => setCurrentPage('signup')} />
      ) : (
        <CreateAccountPage onSwitchToLogin={() => setCurrentPage('login')} />
      )}
    </div>
  );
};

export default LoginSignup;