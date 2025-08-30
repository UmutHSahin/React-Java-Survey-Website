import React, { useState } from 'react';
import './SurveyPage.css';
import Navbar from './Navbar';

const SurveyPage = () => {
  const [surveyStarted, setSurveyStarted] = useState(false);
  const [currentQuestion, setCurrentQuestion] = useState(0);
  const [answers, setAnswers] = useState({});
  const [surveyCompleted, setSurveyCompleted] = useState(false);

  // Sample survey data - this would come from your Java backend
  const surveyData = {
    id: 1,
    title: "Should professional athletes be allowed to use performance enhancing supplements?",
    category: "sports",
    questions: [
      {
        id: 1,
        question: "Do you currently follow professional sports?",
        type: "multiple-choice",
        options: [
          "Yes, regularly",
          "Yes, occasionally", 
          "Rarely",
          "Not at all"
        ]
      },
      {
        id: 2,
        question: "What is your opinion on performance enhancing supplements in sports?",
        type: "multiple-choice",
        options: [
          "Should be completely banned",
          "Should be regulated but allowed",
          "Should be freely allowed",
          "No opinion"
        ]
      },
      {
        id: 3,
        question: "How important is fair play in professional sports to you?",
        type: "multiple-choice",
        options: [
          "Extremely important",
          "Very important",
          "Somewhat important",
          "Not important"
        ]
      },
      {
        id: 4,
        question: "Do you think athletes should have the right to choose what supplements they use?",
        type: "multiple-choice",
        options: [
          "Yes, completely",
          "Yes, with some restrictions",
          "No, it should be regulated",
          "No, completely banned"
        ]
      },
      {
        id: 5,
        question: "What concerns you most about performance enhancing supplements?",
        type: "multiple-choice",
        options: [
          "Health risks to athletes",
          "Unfair competitive advantage",
          "Setting bad example for youth",
          "Nothing concerns me"
        ]
      }
    ]
  };

  const handleStartSurvey = () => {
    setSurveyStarted(true);
  };

  const handleAnswerSelect = (answer) => {
    setAnswers(prev => ({
      ...prev,
      [currentQuestion]: answer
    }));
  };

  const handlePrevious = () => {
    if (currentQuestion > 0) {
      setCurrentQuestion(currentQuestion - 1);
    }
  };

  const handleNext = () => {
    if (currentQuestion < surveyData.questions.length - 1) {
      setCurrentQuestion(currentQuestion + 1);
    }
  };

  const handleComplete = () => {
    setSurveyCompleted(true);
  };

  const handleReturnHome = () => {
    // This would typically use React Router
    window.location.href = '/homepage';
  };

  const isLastQuestion = currentQuestion === surveyData.questions.length - 1;
  const isFirstQuestion = currentQuestion === 0;
  const hasAnsweredCurrent = answers[currentQuestion] !== undefined;

  if (surveyCompleted) {
    return (
      <div className="survey-page">
        <Navbar visibleButtons={['profile','admin','logout']} />
        <div className="survey-container">
          <div className="completion-card">
            <div className="completion-icon">✅</div>
            <h2>Survey Completed!</h2>
            <p>Thank you for participating in our survey. Your responses have been recorded.</p>
            <button className="return-home-btn" onClick={handleReturnHome}>
              Return to Homepage
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (!surveyStarted) {
    return (
      <div className="survey-page">
        <Navbar visibleButtons={['profile','admin','logout']} />
        <div className="survey-container">
          <div className="survey-intro">
            <div className="survey-header">
              <span className={`survey-category ${surveyData.category}`}>
                ⚽ {surveyData.category.charAt(0).toUpperCase() + surveyData.category.slice(1)}
              </span>
            </div>
            
            <h1 className="survey-title">{surveyData.title}</h1>
            
            <div className="survey-info">
              <div className="info-item">
                <span className="info-icon">❓</span>
                <span>{surveyData.questions.length} Questions</span>
              </div>
              <div className="info-item">
                <span className="info-icon">⏱️</span>
                <span>~{Math.ceil(surveyData.questions.length * 0.5)} minutes</span>
              </div>
            </div>
            
            <p className="survey-description">
              Your opinion matters! Help us understand different perspectives on this important topic. 
              All responses are anonymous and will be used for research purposes only.
            </p>
            
            <button className="start-survey-btn" onClick={handleStartSurvey}>
              Start Survey
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="survey-page">
      <Navbar visibleButtons={['profile','admin','logout']} />
      <div className="survey-container">
        <div className="survey-progress">
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{ width: `${((currentQuestion + 1) / surveyData.questions.length) * 100}%` }}
            ></div>
          </div>
          <span className="progress-text">
            Question {currentQuestion + 1} of {surveyData.questions.length}
          </span>
        </div>

        <div className="question-container">
          <div className="question-circle">
            <h2 className="question-text">
              {surveyData.questions[currentQuestion].question}
            </h2>
          </div>

          <div className="options-container">
            {surveyData.questions[currentQuestion].options.map((option, index) => (
              <button
                key={index}
                className={`option-btn ${answers[currentQuestion] === option ? 'selected' : ''}`}
                onClick={() => handleAnswerSelect(option)}
              >
                {option}
              </button>
            ))}
          </div>

          <div className="navigation-buttons">
            <button 
              className="nav-btn prev-btn" 
              onClick={handlePrevious}
              disabled={isFirstQuestion}
            >
              ← Previous
            </button>

            {isLastQuestion ? (
              <button 
                className="nav-btn complete-btn" 
                onClick={handleComplete}
                disabled={!hasAnsweredCurrent}
              >
                Complete Survey
              </button>
            ) : (
              <button 
                className="nav-btn next-btn" 
                onClick={handleNext}
                disabled={!hasAnsweredCurrent}
              >
                Next →
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default SurveyPage;