import React, { useState, useEffect, useRef } from 'react';
import './AdminPage.css';
import './LoadingStates.css';
import Navbar from './Navbar';
import { getAllSurveysForAdmin, createAdminUser, getStoredUser, deleteSurvey, getTotalUsersCount } from '../services/api';

const AdminPage = () => {
  const [surveys, setSurveys] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [isCreatingAdmin, setIsCreatingAdmin] = useState(false);
  const [adminMessage, setAdminMessage] = useState('');
  const [selectedSurvey, setSelectedSurvey] = useState(null);
  const [showViewModal, setShowViewModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [totalUsers, setTotalUsers] = useState(0);
  const hasFetchedSurveys = useRef(false);

  const currentUser = getStoredUser();

  // Check if current user is admin
  const isAdmin = currentUser && currentUser.role === 'ADMIN';

  useEffect(() => {
    // Check authentication and admin role on component mount
    if (!currentUser) {
      // No user logged in, redirect to login
      console.log('🚫 No user logged in, redirecting to login');
      window.location.href = '/';
      return;
    }
    
    if (!isAdmin) {
      // User is not admin, redirect to homepage
      console.log('🚫 User is not admin, redirecting to homepage');
      window.location.href = '/homepage';
      return;
    }
    
    // User is admin, fetch surveys and users only once
    if (!hasFetchedSurveys.current) {
      console.log('👑 Admin user authenticated, fetching surveys and users');
      hasFetchedSurveys.current = true;
      fetchAllSurveys();
      fetchTotalUsers();
    }
  }, []); // Empty dependency array - run only once on mount

  const fetchTotalUsers = async () => {
    try {
      console.log('👥 Admin fetching total users count...');
      const usersData = await getTotalUsersCount();
      
      if (usersData && usersData.totalUsers !== undefined) {
        setTotalUsers(usersData.totalUsers);
        console.log('✅ Total users count:', usersData.totalUsers);
      } else {
        console.warn('⚠️ No users data received:', usersData);
        setTotalUsers(0);
      }
    } catch (error) {
      console.error('❌ Failed to fetch total users count:', error);
      setTotalUsers(0);
    }
  };

  const fetchAllSurveys = async () => {
    try {
      console.log('👑 Admin fetching all surveys...');
      setIsLoading(true);
      setError('');
      
      const surveysData = await getAllSurveysForAdmin();
      
      console.log('📊 Admin surveys data:', surveysData);
      
      // Ensure we have valid data before updating state
      if (surveysData && surveysData.surveys) {
        setSurveys(surveysData.surveys);
        setError(''); // Clear any previous errors
      } else {
        console.warn('⚠️ No surveys data received:', surveysData);
        setSurveys([]);
        setError('No surveys data received from server');
      }
      
    } catch (error) {
      console.error('❌ Failed to fetch surveys for admin:', error);
      setError(`Failed to load surveys: ${error.message}`);
      setSurveys([]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateAdmin = async () => {
    try {
      setIsCreatingAdmin(true);
      setAdminMessage('');
      
      console.log('👑 Creating admin user...');
      const result = await createAdminUser();
      
      setAdminMessage(`Admin user created successfully! Email: ${result.adminEmail}, Password: ${result.adminPassword}`);
      
      // Auto-login as admin
      setTimeout(() => {
        window.location.reload();
      }, 2000);
      
    } catch (error) {
      console.error('❌ Failed to create admin user:', error);
      setAdminMessage(`Failed to create admin user: ${error.message}`);
    } finally {
      setIsCreatingAdmin(false);
    }
  };

  const handleViewSurvey = async (surveyId) => {
    try {
      console.log('👁️ Admin viewing survey:', surveyId);
      // For now, just show a basic modal. You can enhance this later
      const survey = surveys.find(s => s.id === surveyId);
      setSelectedSurvey(survey);
      setShowViewModal(true);
    } catch (error) {
      console.error('❌ Error viewing survey:', error);
      setError('Failed to load survey details');
    }
  };

  const handleDeleteSurvey = async (surveyId, surveyTitle) => {
    if (!window.confirm(`Are you sure you want to delete the survey "${surveyTitle}"? This action cannot be undone.`)) {
      return;
    }

    try {
      setIsDeleting(true);
      console.log('🗑️ Admin deleting survey:', surveyId);
      
      // Call the delete API using the existing deleteSurvey function
      await deleteSurvey(surveyId);
      
      console.log('✅ Survey deleted successfully');
      // Remove the survey from the local state
      setSurveys(prevSurveys => prevSurveys.filter(s => s.id !== surveyId));
      setAdminMessage(`Survey "${surveyTitle}" deleted successfully`);
      
      // Clear message after 3 seconds
      setTimeout(() => setAdminMessage(''), 3000);
    } catch (error) {
      console.error('❌ Error deleting survey:', error);
      setError(`Failed to delete survey: ${error.message}`);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    window.location.href = '/';
  };

  // Show loading state while checking authentication
  if (!currentUser) {
    return (
      <div className="admin-page">
        <div className="admin-container">
          <div className="loading-container">
            <div className="loading-spinner"></div>
            <p>Checking authentication...</p>
          </div>
        </div>
      </div>
    );
  }

  // Show access denied for non-admin users
  if (!isAdmin) {
    return (
      <div className="admin-page">
        <Navbar />
        <div className="admin-container">
          <div className="admin-access-denied">
            <h1>🚫 Access Denied</h1>
            <p>This page is only accessible to admin users.</p>
            
            <div className="admin-login-prompt">
              <p>You need admin privileges to access this page.</p>
              <button className="btn btn-secondary" onClick={handleLogout}>
                Logout and Login as Admin
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page">
      <Navbar />
      <div className="admin-container">
        <div className="admin-header">
          <div className="admin-header-content">
            <div className="admin-user-info">
              <div className="admin-avatar">
                👑
              </div>
              <div className="admin-user-details">
                <h1>Admin Dashboard</h1>
                <p>Welcome, {currentUser.firstName} {currentUser.lastName} ({currentUser.email})</p>
              </div>
            </div>
            <div className="admin-header-actions">
              <button className="admin-refresh-btn" onClick={fetchAllSurveys} title="Refresh Surveys">
                🔄 Refresh
              </button>
              <button className="admin-logout-btn" onClick={handleLogout}>
                🚪 Logout
              </button>
            </div>
          </div>
        </div>

        <div className="admin-main">
          <div className="admin-stats">
            <div className="stat-card">
              <h3>Total Surveys</h3>
              <span className="stat-number">{surveys.length}</span>
            </div>
            <div className="stat-card">
              <h3>Active Surveys</h3>
              <span className="stat-number">{surveys.filter(s => s.isActive).length}</span>
            </div>
            <div className="stat-card">
              <h3>Total Responses</h3>
              <span className="stat-number">{surveys.reduce((sum, s) => sum + (s.responseCount || 0), 0)}</span>
            </div>
            <div className="stat-card">
              <h3>Total Users</h3>
              <span className="stat-number">{totalUsers}</span>
            </div>
          </div>

          <div className="admin-content">
            <div className="admin-section">
              <h2>All Surveys in System</h2>
              
              {isLoading ? (
                <div className="loading-container">
                  <div className="loading-spinner"></div>
                  <p>Loading all surveys...</p>
                </div>
              ) : error && surveys.length === 0 ? (
                <div className="error-message">
                  <p>{error}</p>
                  <button className="btn btn-primary" onClick={fetchAllSurveys}>
                    🔄 Retry Loading Surveys
                  </button>
                </div>
              ) : surveys.length === 0 ? (
                <div className="empty-state">
                  <p>No surveys found in the system.</p>
                </div>
              ) : (
                <div className="admin-surveys-grid">
                  {surveys.map((survey) => (
                    <div key={survey.id} className={`admin-survey-card ${!survey.isActive ? 'inactive' : ''}`}>
                      <div className="survey-card-header">
                        <h3>{survey.title}</h3>
                        <div className="survey-status-badges">
                          <span className={`status-badge ${survey.isActive ? 'active' : 'inactive'}`}>
                            {survey.isActive ? '🟢 Active' : '🔴 Inactive'}
                          </span>
                          <span className="status-badge status">
                            {survey.status}
                          </span>
                        </div>
                      </div>
                      
                      <div className="survey-card-content">
                        <p className="survey-description">{survey.description || 'No description'}</p>
                        
                        <div className="survey-meta">
                          <div className="meta-item">
                            <span className="meta-label">Creator:</span>
                            <span className="meta-value">{survey.creatorEmail || 'Unknown'}</span>
                          </div>
                          <div className="meta-item">
                            <span className="meta-label">Questions:</span>
                            <span className="meta-value">{survey.questionCount || 0}</span>
                          </div>
                          <div className="meta-item">
                            <span className="meta-label">Responses:</span>
                            <span className="meta-value">{survey.responseCount || 0}</span>
                          </div>
                          <div className="meta-item">
                            <span className="meta-label">Unique Users:</span>
                            <span className="meta-value">{survey.uniqueUsers || 0}</span>
                          </div>
                          <div className="meta-item">
                            <span className="meta-label">Created:</span>
                            <span className="meta-value">
                              {survey.createdDate ? new Date(survey.createdDate).toLocaleDateString() : 'Unknown'}
                            </span>
                          </div>
                        </div>
                        
                        <div className="survey-actions">
                          <button 
                            className="action-btn view-btn" 
                            onClick={() => handleViewSurvey(survey.id)}
                            title="View Survey Details"
                          >
                            👁️ View
                          </button>
                          <button 
                            className="action-btn delete-btn" 
                            onClick={() => handleDeleteSurvey(survey.id, survey.title)}
                            title="Delete Survey"
                            disabled={isDeleting}
                          >
                            {isDeleting ? '⏳ Deleting...' : '🗑️ Delete'}
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
              
              {/* Show error message below surveys if there are surveys but also an error */}
              {error && surveys.length > 0 && (
                <div className="error-message below-content">
                  <p>⚠️ {error}</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
      
      {/* Survey View Modal */}
      {showViewModal && selectedSurvey && (
        <div className="modal-overlay" onClick={() => setShowViewModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Survey Details: {selectedSurvey.title}</h2>
              <button 
                className="modal-close-btn" 
                onClick={() => setShowViewModal(false)}
              >
                ✕
              </button>
            </div>
            
            <div className="modal-body">
              <div className="survey-detail-section">
                <h3>Basic Information</h3>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="detail-label">Title:</span>
                    <span className="detail-value">{selectedSurvey.title}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Description:</span>
                    <span className="detail-value">{selectedSurvey.description || 'No description'}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Status:</span>
                    <span className="detail-value">{selectedSurvey.status}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Active:</span>
                    <span className="detail-value">{selectedSurvey.isActive ? 'Yes' : 'No'}</span>
                  </div>
                </div>
              </div>
              
              <div className="survey-detail-section">
                <h3>Statistics</h3>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="detail-label">Questions:</span>
                    <span className="detail-value">{selectedSurvey.questionCount || 0}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Total Responses:</span>
                    <span className="detail-value">{selectedSurvey.responseCount || 0}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Unique Users:</span>
                    <span className="detail-value">{selectedSurvey.uniqueUsers || 0}</span>
                  </div>
                  <div className="detail-item">
                    <span className="detail-label">Created:</span>
                    <span className="detail-value">
                      {selectedSurvey.createdDate ? new Date(selectedSurvey.createdDate).toLocaleDateString() : 'Unknown'}
                    </span>
                  </div>
                </div>
              </div>
              
              <div className="survey-detail-section">
                <h3>Creator Information</h3>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="detail-label">Creator Email:</span>
                    <span className="detail-value">{selectedSurvey.creatorEmail || 'Unknown'}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div className="modal-footer">
              <button 
                className="btn btn-secondary" 
                onClick={() => setShowViewModal(false)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPage;
