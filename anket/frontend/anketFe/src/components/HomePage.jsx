import React, { useState, useEffect } from 'react';
import './Homepage.css';
import './LoadingStates.css';
import Navbar from './Navbar';
import { getSurveys, getSurveyDetails, submitSurveyResponse } from '../services/api';

const Homepage = () => {
  const [isFilterOpen, setIsFilterOpen] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [filters, setFilters] = useState({
    category: 'all',
    questionCount: 'all',
    dateRange: 'all',
    sortBy: 'recent'
  });

  // Real poll data from backend
  const [polls, setPolls] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [showSurveyModal, setShowSurveyModal] = useState(false);
  const [selectedSurvey, setSelectedSurvey] = useState(null);
  const [surveyQuestions, setSurveyQuestions] = useState([]);
  const [isLoadingSurvey, setIsLoadingSurvey] = useState(false);
  const [currentStep, setCurrentStep] = useState('info'); // 'info', 'taking', 'completed'
  const [responses, setResponses] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Fetch surveys from backend on component mount
  useEffect(() => {
    const fetchPolls = async () => {
      try {
        setIsLoading(true);
        setError('');
        
        console.log('🔄 Fetching polls from backend...');
        
        // Fetch surveys from backend
        const surveysData = await getSurveys();
        
        console.log('📊 Backend response:', surveysData);
        
        // Transform backend data to frontend format
        const transformedPolls = surveysData.surveys.map(survey => ({
          id: survey.id,
          title: survey.title,
          category: survey.description ? (
            survey.description.toLowerCase().includes('sport') ? 'sports' : 
            survey.description.toLowerCase().includes('health') ? 'health' :
            survey.description.toLowerCase().includes('work') ? 'work' :
            survey.description.toLowerCase().includes('environment') ? 'environment' :
            survey.description.toLowerCase().includes('politic') ? 'politics' : 'general'
          ) : 'general',
          questionCount: survey.questionCount || 0,
          responses: survey.responseCount || 0,
          createdDate: survey.createdDate ? new Date(survey.createdDate).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
          author: survey.creatorEmail || 'Anonymous'
        }));
        
        setPolls(transformedPolls);
        
        console.log(`✅ Loaded ${transformedPolls.length} polls from backend`);
        
      } catch (error) {
        console.error('❌ Failed to fetch polls:', error);
        setError('Failed to load surveys from backend. Showing empty list.');
        setPolls([]); // Show empty list instead of mock data
      } finally {
        setIsLoading(false);
      }
    };

    fetchPolls();
  }, []);

  // Keep the old mock data structure for reference, but it won't be used
  const mockPolls = [
    {
      id: 1,
      title: "Should professional athletes be allowed to use performance enhancing supplements?",
      category: "sports",
      questionCount: 5,
      responses: 1247,
      createdDate: "2024-07-20",
      author: "SportsFan2024"
    },
    {
      id: 2,
      title: "What do you think about the new healthcare policy changes?",
      category: "health",
      questionCount: 8,
      responses: 892,
      createdDate: "2024-07-18",
      author: "HealthExpert"
    },
    {
      id: 3,
      title: "How should the government handle climate change initiatives?",
      category: "politics",
      questionCount: 12,
      responses: 2156,
      createdDate: "2024-07-22",
      author: "PolicyWatcher"
    },
    {
      id: 4,
      title: "Best training methods for marathon preparation",
      category: "sports",
      questionCount: 6,
      responses: 643,
      createdDate: "2024-07-15",
      author: "RunnerPro"
    },
    {
      id: 5,
      title: "Mental health support in educational institutions",
      category: "health",
      questionCount: 10,
      responses: 1089,
      createdDate: "2024-07-19",
      author: "EduHealth"
    }
  ];

  const categories = [
    { id: 'sports', name: 'Sports', icon: '⚽' },
    { id: 'politics', name: 'Politics', icon: '🏛️' },
    { id: 'health', name: 'Health', icon: '🏥' }
  ];

  const handleCategoryClick = (categoryId) => {
    setSelectedCategory(categoryId);
    setFilters(prev => ({ ...prev, category: categoryId }));
  };

  const toggleFilter = () => {
    setIsFilterOpen(!isFilterOpen);
  };

  const handleFilterChange = (filterType, value) => {
    setFilters(prev => ({ ...prev, [filterType]: value }));
  };

  const getFilteredPolls = () => {
    let filtered = [...polls];

    // Filter by category
    if (filters.category !== 'all') {
      filtered = filtered.filter(poll => poll.category === filters.category);
    }

    // Filter by question count
    if (filters.questionCount !== 'all') {
      switch (filters.questionCount) {
        case 'short':
          filtered = filtered.filter(poll => poll.questionCount <= 5);
          break;
        case 'medium':
          filtered = filtered.filter(poll => poll.questionCount > 5 && poll.questionCount <= 10);
          break;
        case 'long':
          filtered = filtered.filter(poll => poll.questionCount > 10);
          break;
      }
    }

    // Filter by date range
    if (filters.dateRange !== 'all') {
      const now = new Date();
      const cutoffDate = new Date();
      
      switch (filters.dateRange) {
        case '1month':
          cutoffDate.setMonth(now.getMonth() - 1);
          break;
        case '3months':
          cutoffDate.setMonth(now.getMonth() - 3);
          break;
      }
      
      if (filters.dateRange !== 'all') {
        filtered = filtered.filter(poll => new Date(poll.createdDate) >= cutoffDate);
      }
    }

    // Sort polls
    switch (filters.sortBy) {
      case 'recent':
        filtered.sort((a, b) => new Date(b.createdDate) - new Date(a.createdDate));
        break;
      case 'popular':
        filtered.sort((a, b) => b.responses - a.responses);
        break;
      case 'questions':
        filtered.sort((a, b) => b.questionCount - a.questionCount);
        break;
    }

    return filtered;
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      year: 'numeric', 
      month: 'short', 
      day: 'numeric' 
    });
  };

  // Handle take survey button click
  const handleTakeSurvey = async (poll) => {
    console.log('📝 Taking survey:', poll.title);
    setSelectedSurvey(poll);
    setShowSurveyModal(true);
    setCurrentStep('info');
    setResponses({});
    
    // Load survey details with questions
    setIsLoadingSurvey(true);
    try {
      const surveyDetails = await getSurveyDetails(poll.id);
      if (surveyDetails.success) {
        setSurveyQuestions(surveyDetails.survey.questions);
        console.log('✅ Loaded survey questions:', surveyDetails.survey.questions.length);
      }
    } catch (error) {
      console.error('❌ Failed to load survey details:', error);
    } finally {
      setIsLoadingSurvey(false);
    }
  };

  // Close survey modal
  const closeSurveyModal = () => {
    setShowSurveyModal(false);
    setSelectedSurvey(null);
    setSurveyQuestions([]);
    setCurrentStep('info');
    setResponses({});
  };

  // Start taking the survey
  const startSurvey = () => {
    setCurrentStep('taking');
  };

  // Handle answer selection
  const handleAnswerChange = (questionId, answer) => {
    setResponses(prev => ({
      ...prev,
      [questionId]: answer
    }));
  };

  // Submit survey responses
  const handleSubmitSurvey = async () => {
    setIsSubmitting(true);
    try {
      const result = await submitSurveyResponse(selectedSurvey.id, responses, 'Anonymous');
      if (result.success) {
        setCurrentStep('completed');
        console.log('✅ Survey submitted successfully');
      }
    } catch (error) {
      console.error('❌ Failed to submit survey:', error);
      alert('Failed to submit survey. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  // Check if all required questions are answered
  const isAllQuestionsAnswered = () => {
    const requiredQuestions = surveyQuestions.filter(q => q.required);
    return requiredQuestions.every(q => responses[q.id] && responses[q.id].trim() !== '');
  };

  return (
    <div className="homepage">
      <Navbar visibleButtons={['profile','admin','logout']} />
      
      {/* Overlay for filter modal */}
      {isFilterOpen && <div className="filter-overlay" onClick={toggleFilter}></div>}
      
      <div className="main-content">
        {/* Category Buttons */}
        <div className="category-section">
          <div className="category-buttons">
            <button 
              className={`category-btn ${selectedCategory === 'all' ? 'active' : ''}`}
              onClick={() => handleCategoryClick('all')}
            >
              <span className="category-icon">📊</span>
              <span>All Surveys</span>
            </button>
            {categories.map(category => (
              <button 
                key={category.id}
                className={`category-btn ${selectedCategory === category.id ? 'active' : ''}`}
                onClick={() => handleCategoryClick(category.id)}
              >
                <span className="category-icon">{category.icon}</span>
                <span>{category.name}</span>
              </button>
            ))}
            <button className="filter-toggle-btn" onClick={toggleFilter}>
              <span className="filter-icon">🔍</span>
              <span>Filter</span>
            </button>
          </div>
        </div>

        {/* Filter Panel */}
        <div className={`filter-panel ${isFilterOpen ? 'open' : ''}`}>
          <div className="filter-content">
            <h3>Advanced Filters</h3>
            
            <div className="filter-group">
              <label>Category</label>
              <select 
                value={filters.category} 
                onChange={(e) => handleFilterChange('category', e.target.value)}
              >
                <option value="all">All Categories</option>
                <option value="sports">Sports</option>
                <option value="politics">Politics</option>
                <option value="health">Health</option>
              </select>
            </div>

            <div className="filter-group">
              <label>Number of Questions</label>
              <select 
                value={filters.questionCount} 
                onChange={(e) => handleFilterChange('questionCount', e.target.value)}
              >
                <option value="all">Any Length</option>
                <option value="short">Short (1-5 questions)</option>
                <option value="medium">Medium (6-10 questions)</option>
                <option value="long">Long (10+ questions)</option>
              </select>
            </div>

            <div className="filter-group">
              <label>Date Range</label>
              <select 
                value={filters.dateRange} 
                onChange={(e) => handleFilterChange('dateRange', e.target.value)}
              >
                <option value="all">All Time</option>
                <option value="1month">Last Month</option>
                <option value="3months">Last 3 Months</option>
              </select>
            </div>

            <div className="filter-group">
              <label>Sort By</label>
              <select 
                value={filters.sortBy} 
                onChange={(e) => handleFilterChange('sortBy', e.target.value)}
              >
                <option value="recent">Most Recent</option>
                <option value="popular">Most Popular</option>
                <option value="questions">Most Questions</option>
              </select>
            </div>

            <div className="filter-actions">
              <button className="apply-filters-btn" onClick={toggleFilter}>
                Apply Filters
              </button>
              <button 
                className="reset-filters-btn" 
                onClick={() => {
                  setFilters({
                    category: 'all',
                    questionCount: 'all',
                    dateRange: 'all',
                    sortBy: 'recent'
                  });
                  setSelectedCategory('all');
                }}
              >
                Reset
              </button>
            </div>
          </div>
        </div>

        {/* Polls Section */}
        <div className="polls-section">
          <div className="polls-header">
            <h2>Featured Surveys</h2>
            <span className="poll-count">
              {isLoading ? 'Loading...' : `${getFilteredPolls().length} surveys found`}
            </span>
          </div>
          
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
          ) : getFilteredPolls().length === 0 ? (
            <div className="no-polls">
              <p>No surveys found. All surveys have been removed or no surveys exist yet.</p>
              <button 
                className="reset-filters-btn"
                onClick={() => {
                  setFilters({
                    category: 'all',
                    questionCount: 'all',
                    dateRange: 'all',
                    sortBy: 'recent'
                  });
                  setSelectedCategory('all');
                }}
              >
                Reset Filters
              </button>
            </div>
          ) : (
            <div className="polls-grid">
              {getFilteredPolls().map(poll => (
                <div key={poll.id} className={`poll-card ${poll.isCompleted ? 'completed' : ''}`}>
                  <div className="poll-header">
                    <span className={`poll-category ${poll.category}`}>
                      {categories.find(cat => cat.id === poll.category)?.icon || '📊'} 
                      {poll.category.charAt(0).toUpperCase() + poll.category.slice(1)}
                    </span>
                    {poll.isCompleted && (
                      <span className="completion-badge" title="Survey Completed">
                        ✅ Completed
                      </span>
                    )}
                    <span className="poll-date">{formatDate(poll.createdDate)}</span>
                  </div>
                  
                  <h3 className="poll-title">{poll.title}</h3>
                  
                  <div className="poll-meta">
                    <span className="poll-questions">{poll.questionCount} questions</span>
                    <span className="poll-responses">{poll.responses.toLocaleString()} people completed</span>
                    {poll.isCompleted && (
                      <span className="completion-indicator" title="You completed this survey">
                        🎯 You completed this
                      </span>
                    )}
                  </div>
                  
                  <div className="poll-footer">
                    <span className="poll-author">by {poll.author}</span>
                    {poll.isCompleted ? (
                      <button 
                        className="take-survey-btn completed"
                        disabled
                        title="You have already completed this survey"
                      >
                        ✅ Completed
                      </button>
                    ) : (
                      <button 
                        className="take-survey-btn"
                        onClick={() => handleTakeSurvey(poll)}
                      >
                        Take Survey
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Survey Taking Modal */}
      {showSurveyModal && selectedSurvey && (
        <div className="modal-overlay">
          <div className="survey-modal">
            <div className="survey-modal-header">
              <h2>📋 {selectedSurvey.title}</h2>
              <button className="close-btn" onClick={closeSurveyModal}>×</button>
            </div>
            
            <div className="survey-modal-content">
              {currentStep === 'info' && (
                <>
                  <div className="survey-info">
                    <p><strong>Description:</strong> {selectedSurvey.description || 'No description available'}</p>
                    <p><strong>Category:</strong> {selectedSurvey.category}</p>
                    <p><strong>Questions:</strong> {surveyQuestions.length} questions</p>
                    <p><strong>Responses:</strong> {selectedSurvey.responses.toLocaleString()} people have completed this survey</p>
                    <p><strong>Created by:</strong> {selectedSurvey.author}</p>
                  </div>

                  {isLoadingSurvey ? (
                    <div className="loading-state">
                      <div className="loading-spinner"></div>
                      <p>Loading survey questions...</p>
                    </div>
                  ) : (
                    <div className="survey-ready">
                      <div className="ready-icon">🎯</div>
                      <div className="ready-content">
                        <h3>Ready to Start!</h3>
                        <p>This survey contains {surveyQuestions.length} questions and should take about {Math.ceil(surveyQuestions.length * 0.5)} minutes to complete.</p>
                        <p><strong>Your responses are anonymous and will help improve our understanding of this topic.</strong></p>
                      </div>
                    </div>
                  )}
                </>
              )}

              {currentStep === 'taking' && (
                <div className="survey-questions">
                  <div className="survey-progress">
                    <div className="progress-bar">
                      <div 
                        className="progress-fill" 
                        style={{ width: `${(Object.keys(responses).length / surveyQuestions.length) * 100}%` }}
                      ></div>
                    </div>
                    <p>{Object.keys(responses).length} of {surveyQuestions.length} questions answered</p>
                  </div>

                  <div className="questions-container">
                    {surveyQuestions.map((question, index) => (
                      <div key={question.id} className="question-item">
                        <div className="question-header">
                          <span className="question-number">{index + 1}.</span>
                          <h4 className="question-text">{question.question}</h4>
                          {question.required && <span className="required-indicator">*</span>}
                        </div>

                        <div className="question-input">
                          {question.type === 'multiple_choice' ? (
                            <div className="options-list">
                              {question.options.map((option, optionIndex) => (
                                <label key={optionIndex} className="option-label">
                                  <input
                                    type="radio"
                                    name={`question-${question.id}`}
                                    value={option}
                                    checked={responses[question.id] === option}
                                    onChange={(e) => handleAnswerChange(question.id, e.target.value)}
                                  />
                                  <span className="option-text">{option}</span>
                                </label>
                              ))}
                            </div>
                          ) : (
                            <textarea
                              className="text-input"
                              placeholder="Enter your response..."
                              value={responses[question.id] || ''}
                              onChange={(e) => handleAnswerChange(question.id, e.target.value)}
                              rows={3}
                            />
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {currentStep === 'completed' && (
                <div className="survey-completed">
                  <div className="completed-icon">🎉</div>
                  <div className="completed-content">
                    <h3>Thank You!</h3>
                    <p>Your responses have been submitted successfully.</p>
                    <p>Thank you for participating in this survey. Your feedback is valuable and will help improve our understanding of this topic.</p>
                    
                    <div className="completion-stats">
                      <p><strong>Survey:</strong> {selectedSurvey.title}</p>
                      <p><strong>Questions Answered:</strong> {Object.keys(responses).length}</p>
                      <p><strong>Submitted:</strong> {new Date().toLocaleString()}</p>
                    </div>
                  </div>
                </div>
              )}
            </div>

            <div className="survey-modal-footer">
              {currentStep === 'info' && (
                <>
                  <button className="btn btn-secondary" onClick={closeSurveyModal}>
                    Cancel
                  </button>
                  <button 
                    className="btn btn-primary" 
                    onClick={startSurvey}
                    disabled={isLoadingSurvey || surveyQuestions.length === 0}
                  >
                    {isLoadingSurvey ? 'Loading...' : 'Start Survey'}
                  </button>
                </>
              )}

              {currentStep === 'taking' && (
                <>
                  <button className="btn btn-secondary" onClick={closeSurveyModal}>
                    Cancel
                  </button>
                  <button 
                    className="btn btn-primary" 
                    onClick={handleSubmitSurvey}
                    disabled={!isAllQuestionsAnswered() || isSubmitting}
                  >
                    {isSubmitting ? 'Submitting...' : 'Submit Survey'}
                  </button>
                </>
              )}

              {currentStep === 'completed' && (
                <button className="btn btn-primary" onClick={closeSurveyModal}>
                  Close
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Homepage;