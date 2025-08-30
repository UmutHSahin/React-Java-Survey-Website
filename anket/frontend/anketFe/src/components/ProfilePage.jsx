import React, { useState, useEffect } from 'react';
import './ProfilePage.css';
import './LoadingStates.css';
import Navbar from './Navbar';
import { getMySurveys, inspectDatabase, createSurvey, updateSurvey, deleteSurvey, getStoredUser, getSurveyStatistics } from '../services/api';

const ProfilePage = () => {
  const [surveys, setSurveys] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [isSavingSurvey, setIsSavingSurvey] = useState(false);
  const [saveSuccess, setSaveSuccess] = useState('');

  const [showCategoryModal, setShowCategoryModal] = useState(false);
  const [showSurveyBuilder, setShowSurveyBuilder] = useState(false);
  const [showCancelConfirm, setShowCancelConfirm] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [showStatisticsModal, setShowStatisticsModal] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [surveyToDelete, setSurveyToDelete] = useState(null);
  const [editingSurvey, setEditingSurvey] = useState(null);
  const [surveyStatistics, setSurveyStatistics] = useState(null);
  const [isLoadingStatistics, setIsLoadingStatistics] = useState(false);

  const [surveyForm, setSurveyForm] = useState({
    title: '',
    questionCount: 5,
    questions: []
  });

  const categories = [
    { id: 'sports', name: 'Sports', icon: '⚽' },
    { id: 'politics', name: 'Politics', icon: '🏛️' },
    { id: 'health', name: 'Health', icon: '🏥' },
    { id: 'work', name: 'Work', icon: '💼' },
    { id: 'environment', name: 'Environment', icon: '🌱' },
    { id: 'technology', name: 'Technology', icon: '💻' }
  ];

  // Fetch surveys from backend on component mount
  useEffect(() => {
    const fetchSurveys = async () => {
      try {
        setIsLoading(true);
        setError('');
        
        console.log('🔄 Fetching surveys from backend...');
        
        // Fetch surveys from backend
        const surveysData = await getMySurveys();
        
        console.log('📊 Backend response:', surveysData);
        
        // Transform backend data to frontend format
        const transformedSurveys = surveysData.surveys.map(survey => ({
          id: survey.id,
          title: survey.title,
          category: survey.description ? (
            survey.description.toLowerCase().includes('sport') ? 'sports' : 
            survey.description.toLowerCase().includes('health') ? 'health' :
            survey.description.toLowerCase().includes('work') ? 'work' :
            survey.description.toLowerCase().includes('environment') ? 'environment' :
            survey.description.toLowerCase().includes('politic') ? 'politics' : 'technology'
          ) : 'technology',
          questions: survey.questionCount || 0,
          responses: survey.responseCount || 0,
          createdAt: survey.createdDate ? new Date(survey.createdDate).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
          status: survey.status ? survey.status.toLowerCase() : 'draft'
        }));
        
        setSurveys(transformedSurveys);
        
        console.log(`✅ Loaded ${transformedSurveys.length} surveys from backend`);
        
      } catch (error) {
        console.error('❌ Failed to fetch surveys:', error);
        setError('Failed to load surveys from backend. Showing empty list.');
        setSurveys([]); // Show empty list instead of mock data
      } finally {
        setIsLoading(false);
      }
    };

    fetchSurveys();
  }, []);

  // Function to refresh surveys from backend
  const refreshSurveys = async () => {
    try {
      console.log('🔄 Fetching fresh surveys from backend...');
      const surveysData = await getMySurveys();
      console.log('📊 Raw survey data from backend:', surveysData);
      
      const transformedSurveys = surveysData.surveys.map(survey => ({
        id: survey.id,
        title: survey.title,
        category: survey.description ? (
          survey.description.toLowerCase().includes('sport') ? 'sports' : 
          survey.description.toLowerCase().includes('health') ? 'health' :
          survey.description.toLowerCase().includes('work') ? 'work' :
          survey.description.toLowerCase().includes('environment') ? 'environment' :
          survey.description.toLowerCase().includes('politic') ? 'politics' : 'technology'
        ) : 'technology',
        questions: survey.questionCount || 0,
        responses: survey.responseCount || 0,
        createdAt: survey.createdDate ? new Date(survey.createdDate).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
        status: survey.status ? survey.status.toLowerCase() : 'draft'
      }));
      
      console.log('🔄 Transformed surveys:', transformedSurveys);
      setSurveys(transformedSurveys);
      console.log(`✅ State updated with ${transformedSurveys.length} surveys`);
    } catch (error) {
      console.error('❌ Failed to refresh surveys:', error);
    }
  };

  const handleAddSurvey = () => {
    setShowCategoryModal(true);
    setEditingSurvey(null);
    setSurveyForm({ title: '', questionCount: 5, questions: [] });
  };

  const handleEditSurvey = async (survey) => {
    console.log('✏️ Editing survey:', survey);
    setEditingSurvey(survey);
    setSelectedCategory(survey.category);
    
    try {
      // Fetch the actual questions from the backend
      console.log('🔍 Fetching questions for survey ID:', survey.id);
      const response = await fetch(`http://localhost:8080/api/survey-details/${survey.id}`);
      const data = await response.json();
      
      if (data.success && data.survey.questions) {
        console.log('📋 Loaded questions:', data.survey.questions);
        
        // Convert backend questions to frontend format
        const questions = data.survey.questions.map(q => ({
          question: q.question,
          options: q.options && q.options.length > 0 ? q.options : ['', '', '', '']
        }));
        
        setSurveyForm({
          title: survey.title,
          questionCount: questions.length || 5,
          questions: questions.length > 0 ? questions : Array(5).fill().map(() => ({
            question: '',
            options: ['', '', '', '']
          }))
        });
      } else {
        // Fallback if no questions found
        console.log('⚠️ No questions found, using default structure');
        setSurveyForm({
          title: survey.title,
          questionCount: survey.questions || 5,
          questions: Array(survey.questions || 5).fill().map(() => ({
            question: '',
            options: ['', '', '', '']
          }))
        });
      }
    } catch (error) {
      console.error('❌ Error loading survey questions:', error);
      // Fallback on error
      setSurveyForm({
        title: survey.title,
        questionCount: survey.questions || 5,
        questions: Array(survey.questions || 5).fill().map(() => ({
          question: '',
          options: ['', '', '', '']
        }))
      });
    }
    
    setShowSurveyBuilder(true);
  };

  const handleDeleteSurvey = (survey) => {
    setSurveyToDelete(survey);
    setShowDeleteConfirm(true);
  };

  const confirmDelete = async () => {
    try {
      console.log('🗑️ Confirming deletion of survey:', surveyToDelete.id);
      
      // Call backend to delete survey
      const response = await deleteSurvey(surveyToDelete.id);
      
      if (response.success) {
        console.log('✅ Survey deleted from backend successfully');
        
        // Update local state
        setSurveys(surveys.filter(s => s.id !== surveyToDelete.id));
        
        // Show success message
        setSaveSuccess('Survey deleted successfully! ✅');
        setTimeout(() => setSaveSuccess(''), 3000);
        
        // Close modal and reset state
        setShowDeleteConfirm(false);
        setSurveyToDelete(null);
        
        console.log('✅ Survey deleted from frontend state');
      } else {
        throw new Error(response.message || 'Failed to delete survey');
      }
    } catch (error) {
      console.error('❌ Error deleting survey:', error);
      alert(`Failed to delete survey: ${error.message}`);
    }
  };

  const handleCategorySelect = (category) => {
    setSelectedCategory(category);
    setShowCategoryModal(false);
    setShowSurveyBuilder(true);
    // Initialize questions array
    setSurveyForm(prev => ({
      ...prev,
      questions: Array(prev.questionCount).fill().map((_, i) => ({
        question: '',
        options: ['', '', '', '']
      }))
    }));
  };

  const handleCancel = () => {
    setShowCancelConfirm(true);
  };

  const confirmCancel = () => {
    setShowCategoryModal(false);
    setShowSurveyBuilder(false);
    setShowCancelConfirm(false);
    setSelectedCategory('');
    setSurveyForm({ title: '', questionCount: 5, questions: [] });
    setEditingSurvey(null);
  };

  const handleQuestionCountChange = (count) => {
    setSurveyForm(prev => ({
      ...prev,
      questionCount: count,
      questions: Array(count).fill().map((_, i) => 
        prev.questions[i] || { question: '', options: ['', '', '', ''] }
      )
    }));
  };

  const handleShowStatistics = async (surveyId) => {
    try {
      setIsLoadingStatistics(true);
      setShowStatisticsModal(true);
      
      console.log('📊 Fetching statistics for survey:', surveyId);
      const stats = await getSurveyStatistics(surveyId);
      setSurveyStatistics(stats);
      
      console.log('✅ Statistics loaded:', stats);
    } catch (error) {
      console.error('❌ Failed to load statistics:', error);
      alert(`Failed to load survey statistics: ${error.message}`);
      setShowStatisticsModal(false);
    } finally {
      setIsLoadingStatistics(false);
    }
  };

  const updateQuestion = (index, field, value) => {
    console.log(`📝 Updating question ${index}, field: ${field}, value:`, value);
    setSurveyForm(prev => {
      const updated = {
        ...prev,
        questions: prev.questions.map((q, i) => 
          i === index ? { ...q, [field]: value } : q
        )
      };
      console.log('📊 Updated surveyForm questions:', updated.questions);
      return updated;
    });
  };

  const updateOption = (questionIndex, optionIndex, value) => {
    console.log(`📝 Updating option ${questionIndex}:${optionIndex}, value:`, value);
    setSurveyForm(prev => {
      const updated = {
        ...prev,
        questions: prev.questions.map((q, i) => 
          i === questionIndex 
            ? { ...q, options: q.options.map((opt, oi) => oi === optionIndex ? value : opt) }
            : q
        )
      };
      console.log('📊 Updated surveyForm questions after option change:', updated.questions);
      return updated;
    });
  };

  const handleSaveSurvey = async () => {
    if (!surveyForm.title || surveyForm.title.trim() === '') {
      alert('Please enter a survey title');
      return;
    }

    setIsSavingSurvey(true);
    
    try {
      if (editingSurvey) {
        // Update existing survey
        console.log('🔄 Updating survey:', editingSurvey.id);
        console.log('📊 Full surveyForm:', surveyForm);
        console.log('📊 Survey form questions:', surveyForm.questions);
        console.log('📊 Questions length:', surveyForm.questions.length);
        
        const filteredQuestions = surveyForm.questions.filter(q => q.question && q.question.trim() !== '');
        console.log('📋 Filtered questions to send:', filteredQuestions);
        console.log('📋 Filtered questions count:', filteredQuestions.length);
        
        const updateData = {
          title: surveyForm.title.trim(),
          description: `Category: ${selectedCategory}`,
          category: selectedCategory,
          questions: filteredQuestions
        };
        
        const response = await updateSurvey(editingSurvey.id, updateData);
        
        if (response.success) {
          console.log('✅ Survey updated successfully:', response);
          console.log('🔄 Refreshing surveys from backend...');
          
          // Force a delay to ensure backend has processed the update
          await new Promise(resolve => setTimeout(resolve, 500));
          
          await refreshSurveys(); // Refresh the list from backend
          console.log('✅ Surveys refreshed, current count:', surveys.length);
          
          // Show success message
          setSaveSuccess('Survey updated successfully! ✅');
          setTimeout(() => setSaveSuccess(''), 3000); // Clear after 3 seconds
        } else {
          throw new Error(response.message || 'Failed to update survey');
        }
        
      } else {
        // Create new survey
        console.log('🔄 Creating new survey...');
        console.log('📊 Full surveyForm:', surveyForm);
        console.log('📊 Survey form questions:', surveyForm.questions);
        console.log('📊 Questions length:', surveyForm.questions.length);
        
        const currentUser = getStoredUser();
        const filteredQuestions = surveyForm.questions.filter(q => q.question && q.question.trim() !== '');
        console.log('📋 Filtered questions to send:', filteredQuestions);
        console.log('📋 Filtered questions count:', filteredQuestions.length);
        
        const surveyData = {
          title: surveyForm.title.trim(),
          description: `Category: ${selectedCategory}`,
          category: selectedCategory,
          creatorEmail: currentUser ? currentUser.email : undefined,
          questions: filteredQuestions
        };
        
        console.log('📝 Survey data being sent:', surveyData);
        
        const response = await createSurvey(surveyData);
        
        if (response.success) {
          console.log('✅ Survey created successfully:', response.survey);
          await refreshSurveys(); // Refresh the list from backend
          
          // Show success message
          setSaveSuccess('Survey created successfully! ✅');
          setTimeout(() => setSaveSuccess(''), 3000); // Clear after 3 seconds
        } else {
          throw new Error(response.message || 'Failed to create survey');
        }
      }
      
      // Close modal and reset form
      setShowSurveyBuilder(false);
      setSelectedCategory('');
      setSurveyForm({ title: '', questionCount: 5, questions: [] });
      setEditingSurvey(null);
      
    } catch (error) {
      console.error('❌ Failed to save survey:', error);
      alert(`Failed to save survey: ${error.message}`);
    } finally {
      setIsSavingSurvey(false);
    }
  };

  return (
    <div className="profile-page">
                <Navbar visibleButtons={['profile','admin','logout']} />

      {/* Header */}
      <div className="profile-header">
        <div className="profile-header-container">
          <div className="profile-header-content">
            <div className="profile-user-info">
              <div className="profile-avatar">
                👤
              </div>
              <div className="profile-user-details">
                <h1>Your Profile</h1>
                <p>Manage your surveys and view responses</p>
              </div>
            </div>
            <button className="create-survey-btn" onClick={handleAddSurvey}>
              <span>➕</span>
              <span>Create Survey</span>
            </button>
          </div>
        </div>
      </div>

      {/* Success Message */}
      {saveSuccess && (
        <div className="success-message">
          {saveSuccess}
        </div>
      )}

      {/* Main Content */}
      <div className="profile-main">
        {/* Stats */}
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-card-content">
              <div className="stat-info">
                <h3>Total Surveys</h3>
                <p>{surveys.length}</p>
              </div>
              <div className="stat-icon blue">
                📊
              </div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-card-content">
              <div className="stat-info">
                <h3>Total Responses</h3>
                <p>{surveys.reduce((acc, s) => acc + s.responses, 0)}</p>
              </div>
              <div className="stat-icon green">
                👥
              </div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-card-content">
              <div className="stat-info">
                <h3>Active Surveys</h3>
                <p>{surveys.filter(s => s.status === 'active').length}</p>
              </div>
              <div className="stat-icon yellow">
                📅
              </div>
            </div>
          </div>
        </div>

        {/* Surveys List */}
        <div className="surveys-section">
          <div className="surveys-header">
            <h2>Your Surveys</h2>
          </div>
          <div className="surveys-content">
            {isLoading ? (
              <div className="loading-state">
                <div className="loading-spinner"></div>
                <p>Loading surveys from backend...</p>
              </div>
            ) : error ? (
              <div className="error-state">
                <div className="error-icon">❌</div>
                <h3>Failed to load surveys</h3>
                <p>{error}</p>
                <button className="retry-btn" onClick={() => window.location.reload()}>
                  Retry
                </button>
              </div>
            ) : surveys.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">📊</div>
                <h3>No surveys found</h3>
                <p>All surveys have been removed or no surveys exist yet. Create your first survey to get started!</p>
                <button className="empty-state-btn" onClick={handleAddSurvey}>
                  Create Survey
                </button>
              </div>
            ) : (
              <div className="surveys-list">
                {surveys.map((survey) => (
                  <div key={survey.id} className="survey-item">
                    <div className="survey-item-content">
                      <div className="survey-item-main">
                        <div className="survey-badges">
                          <span className={`survey-category ${survey.category}`}>
                            {categories.find(c => c.id === survey.category)?.icon} {survey.category}
                          </span>
                          <span className={`survey-status ${survey.status}`}>
                            {survey.status}
                          </span>
                        </div>
                        <h3>{survey.title}</h3>
                        <div className="survey-meta">
                          <span>{survey.questions} questions</span>
                          <span>{survey.responses} responses</span>
                          <span>Created {new Date(survey.createdAt).toLocaleDateString()}</span>
                        </div>
                      </div>
                      <div className="survey-actions">
                        <button
                          className="action-btn statistics"
                          onClick={() => handleShowStatistics(survey.id)}
                          title="View Statistics"
                        >
                          📊
                        </button>
                        <button
                          className="action-btn edit"
                          onClick={() => handleEditSurvey(survey)}
                          title="Edit Survey"
                        >
                          ✏️
                        </button>
                        <button
                          className="action-btn delete"
                          onClick={() => handleDeleteSurvey(survey)}
                          title="Delete Survey"
                        >
                          🗑️
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Category Selection Modal */}
      {showCategoryModal && (
        <div className="modal-overlay">
          <div className="modal medium">
            <div className="modal-header">
              <h2>Select Category</h2>
              <button className="modal-close" onClick={handleCancel}>
                ✕
              </button>
            </div>
            <div className="category-grid">
              {categories.map((category) => (
                <button
                  key={category.id}
                  className="category-option"
                  onClick={() => handleCategorySelect(category.id)}
                >
                  <div className="category-icon">{category.icon}</div>
                  <div className="category-name">{category.name}</div>
                </button>
              ))}
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={handleCancel}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Survey Builder Modal */}
      {showSurveyBuilder && (
        <div className="modal-overlay">
          <div className="modal large">
            <div className="modal-header">
              <h2>{editingSurvey ? 'Edit Survey' : 'Create Survey'}</h2>
              <button className="modal-close" onClick={handleCancel}>
                ✕
              </button>
            </div>

            <div>
              {/* Survey Title */}
              <div className="form-group">
                <label className="form-label">Survey Title</label>
                <input
                  type="text"
                  className="form-input"
                  value={surveyForm.title}
                  onChange={(e) => setSurveyForm(prev => ({ ...prev, title: e.target.value }))}
                  placeholder="Enter your survey title..."
                />
              </div>

              {/* Question Count */}
              <div className="form-group">
                <label className="form-label">Number of Questions</label>
                <select
                  className="form-select"
                  value={surveyForm.questionCount}
                  onChange={(e) => handleQuestionCountChange(parseInt(e.target.value))}
                >
                  {[...Array(15)].map((_, i) => (
                    <option key={i + 1} value={i + 1}>
                      {i + 1} Question{i > 0 ? 's' : ''}
                    </option>
                  ))}
                </select>
              </div>

              {/* Questions */}
              <div className="questions-section">
                <h3>Questions</h3>
                {surveyForm.questions.map((question, qIndex) => (
                  <div key={qIndex} className="question-card">
                    <div className="question-header">
                      <label className="form-label">
                        Question {qIndex + 1}
                      </label>
                      <input
                        type="text"
                        className="form-input"
                        value={question.question}
                        onChange={(e) => updateQuestion(qIndex, 'question', e.target.value)}
                        placeholder="Enter your question..."
                      />
                    </div>
                    <div className="options-section">
                      <label className="form-label">Answer Options</label>
                      {question.options.map((option, oIndex) => (
                        <input
                          key={oIndex}
                          type="text"
                          className="option-input"
                          value={option}
                          onChange={(e) => updateOption(qIndex, oIndex, e.target.value)}
                          placeholder={`Option ${oIndex + 1}`}
                        />
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={handleCancel}>
                Cancel
              </button>
              <button
                className="btn btn-primary"
                onClick={handleSaveSurvey}
                disabled={!surveyForm.title.trim() || isSavingSurvey}
              >
                {isSavingSurvey ? 
                  (editingSurvey ? 'Updating...' : 'Creating...') : 
                  (editingSurvey ? 'Update Survey' : 'Create Survey')
                }
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Cancel Confirmation Modal */}
      {showCancelConfirm && (
        <div className="modal-overlay">
          <div className="modal small">
            <div className="modal-header">
              <h2>Are you sure?</h2>
            </div>
            <p>Any unsaved changes will be lost. Do you want to continue?</p>
            <div className="modal-footer">
              <button
                className="btn btn-secondary"
                onClick={() => setShowCancelConfirm(false)}
              >
                No
              </button>
              <button className="btn btn-danger" onClick={confirmCancel}>
                Yes, discard changes
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteConfirm && (
        <div className="modal-overlay">
          <div className="modal small">
            <div className="modal-header">
              <h2>Delete Survey</h2>
            </div>
            <p>
              Are you sure you want to delete "{surveyToDelete?.title}"? This action cannot be undone.
            </p>
            <div className="modal-footer">
              <button
                className="btn btn-secondary"
                onClick={() => setShowDeleteConfirm(false)}
              >
                Cancel
              </button>
              <button className="btn btn-danger" onClick={confirmDelete}>
                Delete Survey
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Statistics Modal */}
      {showStatisticsModal && (
        <div className="modal-overlay">
          <div className="modal large">
            <div className="modal-header">
              <h2>📊 Survey Statistics</h2>
              <button className="modal-close" onClick={() => setShowStatisticsModal(false)}>
                ✕
              </button>
            </div>
            
            {isLoadingStatistics ? (
              <div className="loading-container">
                <div className="loading-spinner"></div>
                <p>Loading statistics...</p>
              </div>
            ) : surveyStatistics ? (
              <div className="statistics-content">
                <div className="survey-overview">
                  <h3>{surveyStatistics.surveyTitle}</h3>
                  <p className="survey-description">{surveyStatistics.surveyDescription}</p>
                  
                  <div className="stats-grid">
                    <div className="stat-item">
                      <span className="stat-label">Total Questions</span>
                      <span className="stat-value">{surveyStatistics.totalQuestions}</span>
                    </div>
                    <div className="stat-item">
                      <span className="stat-label">Total Responses</span>
                      <span className="stat-value">{surveyStatistics.totalResponses}</span>
                    </div>
                    <div className="stat-item">
                      <span className="stat-label">Unique Users</span>
                      <span className="stat-value">{surveyStatistics.uniqueUsers}</span>
                    </div>
                    <div className="stat-item">
                      <span className="stat-label">Completion Rate</span>
                      <span className="stat-value">{surveyStatistics.completionRate.toFixed(1)}%</span>
                    </div>
                  </div>
                </div>

                <div className="questions-statistics">
                  <h4>Question Statistics</h4>
                  {surveyStatistics.questionStatistics.map((question, index) => (
                    <div key={question.id} className="question-stat-item">
                      <div className="question-header">
                        <span className="question-number">Q{index + 1}</span>
                        <span className="question-text">{question.text}</span>
                        <span className="question-type">{question.type}</span>
                      </div>
                      
                      <div className="options-statistics">
                        {question.options.map((option) => (
                          <div key={option.id} className="option-stat-item">
                            <span className="option-text">{option.text}</span>
                            <span className="option-count">{option.responseCount} responses</span>
                            <div className="option-bar">
                              <div 
                                className="option-bar-fill" 
                                style={{ 
                                  width: `${surveyStatistics.uniqueUsers > 0 ? (option.responseCount / surveyStatistics.uniqueUsers) * 100 : 0}%` 
                                }}
                              ></div>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ) : (
              <div className="error-message">
                <p>Failed to load statistics</p>
              </div>
            )}
            
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setShowStatisticsModal(false)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProfilePage;