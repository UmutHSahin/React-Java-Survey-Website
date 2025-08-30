// API Service for Backend Integration
// Backend API'si ile entegrasyon için servis dosyası

const API_BASE_URL = 'http://localhost:8080/api';

// API Helper Functions
// API Yardımcı Fonksiyonları

/**
 * Generic API request function
 * Genel API istek fonksiyonu
 */
const apiRequest = async (endpoint, options = {}) => {
  const url = `${API_BASE_URL}${endpoint}`;
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // Add authorization token if available
  // Mevcut ise authorization token'ı ekle
  const token = localStorage.getItem('authToken');
  if (token) {
    defaultOptions.headers.Authorization = `Bearer ${token}`;
  }

  const finalOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers,
    },
  };

  try {
    const response = await fetch(url, finalOptions);
    
    // Handle different response types
    // Farklı yanıt türlerini işle
    const contentType = response.headers.get('content-type');
    let data;
    
    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
    } else {
      data = await response.text();
    }

    if (!response.ok) {
      throw new Error(data.message || `HTTP error! status: ${response.status}`);
    }

    return { data, status: response.status };
  } catch (error) {
    console.error(`API Request Error (${endpoint}):`, error);
    throw error;
  }
};

// Authentication API
// Kimlik Doğrulama API'si

/**
 * User login
 * Kullanıcı girişi
 */
export const loginUser = async (email, password) => {
  try {
    const response = await apiRequest('/simple-login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });

    // Store token in localStorage
    // Token'ı localStorage'da sakla
    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
    }

    return response.data;
  } catch (error) {
    throw new Error(error.message || 'Login failed');
  }
};

/**
 * User registration
 * Kullanıcı kaydı
 */
export const registerUser = async (userData) => {
  try {
    const response = await apiRequest('/simple-register', {
      method: 'POST',
      body: JSON.stringify(userData),
    });

    // Store token in localStorage
    // Token'ı localStorage'da sakla
    if (response.data.token) {
      localStorage.setItem('authToken', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));
    }

    return response.data;
  } catch (error) {
    throw new Error(error.message || 'Registration failed');
  }
};

/**
 * User logout
 * Kullanıcı çıkışı
 */
export const logoutUser = async () => {
  try {
    await apiRequest('/auth/logout', {
      method: 'POST',
    });
  } catch (error) {
    console.warn('Logout API call failed:', error);
  } finally {
    // Always clear local storage
    // Her zaman local storage'ı temizle
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  }
};

/**
 * Get current user info
 * Mevcut kullanıcı bilgilerini al
 */
export const getCurrentUser = async () => {
  try {
    const response = await apiRequest('/auth/me');
    return response.data;
  } catch (error) {
    // If token is invalid, clear local storage
    // Token geçersizse local storage'ı temizle
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    throw error;
  }
};

// Survey API
// Anket API'si

/**
 * Get all surveys from backend
 * Backend'den tüm anketleri al
 */
export const getSurveys = async () => {
  try {
    // Add cache-busting parameter to prevent browser caching
    const timestamp = new Date().getTime();
    const response = await apiRequest(`/list-all-surveys?t=${timestamp}`);
    return response.data;
  } catch (error) {
    console.warn('Failed to fetch surveys from backend, returning empty array:', error);
    return { surveys: [], totalSurveysCount: 0 };
  }
};

/**
 * Get surveys created by current user
 * Mevcut kullanıcının oluşturduğu anketleri al
 */
export const getMySurveys = async () => {
  try {
    console.log('🔄 Fetching my surveys from backend...');
    
    // Get current user from localStorage
    const currentUser = getStoredUser();
    if (!currentUser || !currentUser.email) {
      console.warn('⚠️ No user logged in, cannot fetch surveys');
      throw new Error('User not authenticated');
    }
    
    const timestamp = new Date().getTime();
    const response = await apiRequest(`/my-surveys?userEmail=${encodeURIComponent(currentUser.email)}&t=${timestamp}`);
    
    console.log('✅ My surveys fetched successfully for user:', currentUser.email, response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to fetch my surveys:', error);
    throw new Error(error.message || 'Failed to fetch my surveys');
  }
};

/**
 * Get all surveys including inactive ones
 * Tüm anketleri al (pasif olanlar dahil)
 */
export const getAllSurveysIncludingInactive = async () => {
  try {
    const response = await apiRequest('/list-all-surveys-including-inactive');
    return response.data;
  } catch (error) {
    console.warn('Failed to fetch all surveys from backend:', error);
    return { allSurveys: [], totalSurveysCount: 0 };
  }
};

/**
 * Inspect database for survey status
 * Anket durumu için veritabanını incele
 */
export const inspectDatabase = async () => {
  try {
    const response = await apiRequest('/inspect-database');
    return response.data;
  } catch (error) {
    console.warn('Failed to inspect database:', error);
    return { userCount: 0, totalSurveysInDB: 0, activeSurveysInDB: 0, inactiveSurveysInDB: 0 };
  }
};

/**
 * Get survey by ID
 * ID'ye göre anket al
 */
export const getSurveyById = async (id) => {
  try {
    const response = await apiRequest(`/surveys/${id}`);
    return response.data;
  } catch (error) {
    throw new Error(error.message || 'Failed to fetch survey');
  }
};

/**
 * Create new survey
 * Yeni anket oluştur
 */
export const createSurvey = async (surveyData) => {
  try {
    console.log('🔄 Creating survey with data:', surveyData);
    
    const response = await apiRequest('/create-survey', {
      method: 'POST',
      body: JSON.stringify(surveyData),
    });
    
    console.log('✅ Survey created successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to create survey:', error);
    throw new Error(error.message || 'Failed to create survey');
  }
};

/**
 * Update survey
 * Anket güncelle
 */
export const updateSurvey = async (id, surveyData) => {
  try {
    console.log('🔄 Updating survey with ID:', id, 'Data:', surveyData);
    
    const response = await apiRequest(`/update-survey/${id}`, {
      method: 'PUT',
      body: JSON.stringify(surveyData),
    });
    
    console.log('✅ Survey updated successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to update survey:', error);
    throw new Error(error.message || 'Failed to update survey');
  }
};

/**
 * Delete a survey from backend
 * Backend'den anket sil
 */
export const deleteSurvey = async (surveyId) => {
  try {
    console.log('🗑️ Deleting survey with ID:', surveyId);
    
    const response = await apiRequest(`/delete-survey/${surveyId}`, {
      method: 'DELETE',
    });
    
    console.log('✅ Survey deleted successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to delete survey:', error);
    throw new Error(error.message || 'Failed to delete survey');
  }
};



// Response API
// Yanıt API'si

/**
 * Get survey results
 * Anket sonuçlarını al
 */
export const getSurveyResults = async (surveyId) => {
  try {
    const response = await apiRequest(`/surveys/${surveyId}/results`);
    return response.data;
  } catch (error) {
    throw new Error(error.message || 'Failed to fetch survey results');
  }
};

/**
 * Get survey details with questions
 * Sorular ile birlikte anket detaylarını al
 */
export const getSurveyDetails = async (surveyId) => {
  try {
    console.log('🔄 Fetching survey details for ID:', surveyId);
    
    const response = await apiRequest(`/survey-details/${surveyId}`);
    
    console.log('✅ Survey details fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to fetch survey details:', error);
    throw new Error(error.message || 'Failed to fetch survey details');
  }
};

/**
 * Get survey statistics by ID
 * ID'ye göre anket istatistiklerini al
 */
export const getSurveyStatistics = async (surveyId) => {
  try {
    console.log('📊 Fetching survey statistics for ID:', surveyId);
    
    const response = await apiRequest(`/survey-statistics/${surveyId}`);
    
    console.log('✅ Survey statistics fetched successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to fetch survey statistics:', error);
    throw new Error(error.message || 'Failed to fetch survey statistics');
  }
};

/**
 * Create admin user
 * Admin kullanıcısı oluştur
 */
export const createAdminUser = async () => {
  try {
    console.log('👑 Creating admin user...');
    
    const response = await apiRequest('/create-admin-user', {
      method: 'POST',
    });
    
    console.log('✅ Admin user created successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to create admin user:', error);
    throw new Error(error.message || 'Failed to create admin user');
  }
};

/**
 * Get total users count for admin
 * Admin için toplam kullanıcı sayısını al
 */
export const getTotalUsersCount = async () => {
  try {
    console.log('👥 Fetching total users count for admin...');
    
    const response = await apiRequest('/admin/total-users');
    
    console.log('✅ Total users count fetched for admin:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to fetch total users count for admin:', error);
    throw new Error(error.message || 'Failed to fetch total users count for admin');
  }
};

/**
 * Get all surveys for admin
 * Admin için tüm anketleri al
 */
export const getAllSurveysForAdmin = async () => {
  try {
    console.log('👑 Fetching all surveys for admin...');
    
    const response = await apiRequest('/admin/all-surveys');
    
    console.log('✅ All surveys fetched for admin:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to fetch all surveys for admin:', error);
    throw new Error(error.message || 'Failed to fetch all surveys for admin');
  }
};

/**
 * Submit survey response
 * Anket yanıtını gönder
 */
export const submitSurveyResponse = async (surveyId, responses, respondentName = null) => {
  try {
    console.log('🔄 Submitting survey response for survey:', surveyId);
    
    const requestData = {
      surveyId: surveyId,
      responses: responses,
      respondentName: respondentName
    };
    
    const response = await apiRequest('/submit-survey-response', {
      method: 'POST',
      body: JSON.stringify(requestData),
    });
    
    console.log('✅ Survey response submitted successfully:', response.data);
    return response.data;
  } catch (error) {
    console.error('❌ Failed to submit survey response:', error);
    throw new Error(error.message || 'Failed to submit survey response');
  }
};

// Utility Functions
// Yardımcı Fonksiyonlar

/**
 * Check if user is authenticated
 * Kullanıcının kimlik doğrulaması yapılmış mı kontrol et
 */
export const isAuthenticated = () => {
  const token = localStorage.getItem('authToken');
  const user = localStorage.getItem('user');
  return !!(token && user);
};

/**
 * Get stored user data
 * Saklanan kullanıcı verilerini al
 */
export const getStoredUser = () => {
  const userStr = localStorage.getItem('user');
  return userStr ? JSON.parse(userStr) : null;
};

/**
 * Health check
 * Sistem durumu kontrolü
 */
export const healthCheck = async () => {
  try {
    const response = await apiRequest('/health');
    return response.data;
  } catch (error) {
    throw new Error('Backend is not available');
  }
};

// Export default API object
// Varsayılan API nesnesini export et
export default {
  // Auth
  loginUser,
  registerUser,
  logoutUser,
  getCurrentUser,
  
  // Surveys
  getSurveys,
  getSurveyById,
  createSurvey,
  updateSurvey,
  deleteSurvey,
  
  // Responses
  submitSurveyResponse,
  getSurveyResults,
  
  // Utilities
  isAuthenticated,
  getStoredUser,
  healthCheck,
};
