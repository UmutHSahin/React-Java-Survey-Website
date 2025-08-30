package com.anket.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Giriş Yanıtı DTO - Login Response DTO
 * 
 * Başarılı kullanıcı girişi sonrasında döndürülen bilgileri içerir
 * Contains information returned after successful user login
 * 
 * Bu sınıf şu bilgileri sağlar:
 * This class provides the following information:
 * - JWT access token (Erişim token'ı)
 * - Token tipi ve geçerlilik süresi (Token type and expiration)
 * - Kullanıcı bilgileri (User information)
 * - Giriş zamanı ve ek metadata (Login time and additional metadata)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - AuthController.login(): Bu DTO'yu yanıt olarak döndürür
 * - LoginRequest: Giriş isteği için kullanılan DTO
 * - JwtTokenProvider: Token bilgilerini sağlar
 * 
 * JSON Formatı - JSON Format:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "tokenType": "Bearer",
 *   "expiresIn": 86400,
 *   "user": {
 *     "id": 1,
 *     "email": "demo@example.com",
 *     "firstName": "Demo",
 *     "lastName": "User",
 *     "role": "USER"
 *   },
 *   "loginTime": "2024-01-15T10:30:45",
 *   "message": "Giriş başarılı"
 * }
 */
public class LoginResponse {

    /**
     * JWT Access Token - JWT Erişim Token'ı
     * 
     * Kullanıcının kimlik doğrulaması için kullanacağı token
     * Token that user will use for authentication
     * 
     * Bu token her API isteğinde Authorization header'ında gönderilir
     * This token is sent in Authorization header with every API request
     * 
     * Format: "Bearer " + token
     */
    private String token;

    /**
     * Token Tipi - Token Type
     * 
     * Token'ın tipi (genellikle "Bearer")
     * Type of token (usually "Bearer")
     * 
     * OAuth 2.0 standardına uygun
     * Compliant with OAuth 2.0 standard
     */
    private String tokenType = "Bearer";

    /**
     * Geçerlilik Süresi - Expiration Time
     * 
     * Token'ın kaç saniye sonra geçerliliğini kaybedeceği
     * How many seconds until token expires
     * 
     * Frontend tarafında token yenileme zamanını hesaplamak için kullanılır
     * Used by frontend to calculate token refresh timing
     */
    private Long expiresIn;

    /**
     * Kullanıcı Bilgileri - User Information
     * 
     * Giriş yapan kullanıcının temel bilgileri
     * Basic information of logged-in user
     * 
     * Güvenlik nedeniyle şifre gibi hassas bilgiler içermez
     * Does not contain sensitive information like password for security
     */
    private Map<String, Object> user;

    /**
     * Giriş Zamanı - Login Time
     * 
     * Kullanıcının ne zaman giriş yaptığı
     * When the user logged in
     */
    private LocalDateTime loginTime;

    /**
     * Başarı Mesajı - Success Message
     * 
     * Giriş işleminin başarılı olduğuna dair mesaj
     * Message indicating successful login
     */
    private String message;

    /**
     * Refresh Token - Yenileme Token'ı (Opsiyonel)
     * 
     * Access token'ı yenilemek için kullanılan token
     * Token used to refresh access token
     * 
     * Şu an kullanılmıyor ama gelecekte eklenebilir
     * Currently not used but can be added in future
     */
    private String refreshToken;

    /**
     * Ek Metadata - Additional Metadata
     * 
     * Giriş işlemiyle ilgili ek bilgiler
     * Additional information about login process
     */
    private Map<String, Object> metadata;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * 
     * Jackson JSON serializasyonu için gerekli
     * Required for Jackson JSON serialization
     */
    public LoginResponse() {
        this.loginTime = LocalDateTime.now();
        this.tokenType = "Bearer";
        this.message = "Giriş başarılı - Login successful";
    }

    /**
     * Temel Constructor - Basic Constructor
     * 
     * @param token JWT token
     * @param expiresIn Geçerlilik süresi
     * @param user Kullanıcı bilgileri
     */
    public LoginResponse(String token, Long expiresIn, Map<String, Object> user) {
        this();
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    /**
     * Tam Constructor - Full Constructor
     * 
     * @param token JWT token
     * @param tokenType Token tipi
     * @param expiresIn Geçerlilik süresi
     * @param user Kullanıcı bilgileri
     * @param message Başarı mesajı
     */
    public LoginResponse(String token, String tokenType, Long expiresIn, 
                        Map<String, Object> user, String message) {
        this();
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
        this.message = message;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    /**
     * JWT token'ını getirir - Returns JWT token
     * @return JWT token
     */
    public String getToken() {
        return token;
    }

    /**
     * JWT token'ını ayarlar - Sets JWT token
     * @param token JWT token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Token tipini getirir - Returns token type
     * @return Token tipi
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * Token tipini ayarlar - Sets token type
     * @param tokenType Token tipi
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * Geçerlilik süresini getirir - Returns expiration time
     * @return Geçerlilik süresi (saniye)
     */
    public Long getExpiresIn() {
        return expiresIn;
    }

    /**
     * Geçerlilik süresini ayarlar - Sets expiration time
     * @param expiresIn Geçerlilik süresi (saniye)
     */
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * Kullanıcı bilgilerini getirir - Returns user information
     * @return Kullanıcı bilgileri
     */
    public Map<String, Object> getUser() {
        return user;
    }

    /**
     * Kullanıcı bilgilerini ayarlar - Sets user information
     * @param user Kullanıcı bilgileri
     */
    public void setUser(Map<String, Object> user) {
        this.user = user;
    }

    /**
     * Giriş zamanını getirir - Returns login time
     * @return Giriş zamanı
     */
    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    /**
     * Giriş zamanını ayarlar - Sets login time
     * @param loginTime Giriş zamanı
     */
    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * Başarı mesajını getirir - Returns success message
     * @return Başarı mesajı
     */
    public String getMessage() {
        return message;
    }

    /**
     * Başarı mesajını ayarlar - Sets success message
     * @param message Başarı mesajı
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Refresh token'ı getirir - Returns refresh token
     * @return Refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Refresh token'ı ayarlar - Sets refresh token
     * @param refreshToken Refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Metadata'yı getirir - Returns metadata
     * @return Metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Metadata'yı ayarlar - Sets metadata
     * @param metadata Metadata
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * Token'ın geçerli olup olmadığını kontrol eder
     * Checks if token is valid
     * 
     * @return true: geçerli, false: geçersiz
     */
    public boolean hasValidToken() {
        return token != null && !token.trim().isEmpty();
    }

    /**
     * Kullanıcı bilgilerinin var olup olmadığını kontrol eder
     * Checks if user information exists
     * 
     * @return true: var, false: yok
     */
    public boolean hasUserInfo() {
        return user != null && !user.isEmpty();
    }

    /**
     * Token'ın süresinin dolmasına kalan süreyi dakika cinsinden getirir
     * Returns remaining time until token expiration in minutes
     * 
     * @return Kalan süre (dakika)
     */
    public Long getRemainingTimeInMinutes() {
        return expiresIn != null ? expiresIn / 60 : 0;
    }

    /**
     * Kullanıcının tam adını getirir
     * Returns user's full name
     * 
     * @return Kullanıcının tam adı
     */
    public String getUserFullName() {
        if (user != null) {
            String firstName = (String) user.get("firstName");
            String lastName = (String) user.get("lastName");
            
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (lastName != null) {
                return lastName;
            }
        }
        return "Unknown User";
    }

    /**
     * Kullanıcının rolünü getirir
     * Returns user's role
     * 
     * @return Kullanıcı rolü
     */
    public String getUserRole() {
        return user != null ? (String) user.get("role") : "UNKNOWN";
    }

    /**
     * Kullanıcının admin olup olmadığını kontrol eder
     * Checks if user is admin
     * 
     * @return true: admin, false: normal kullanıcı
     */
    public boolean isUserAdmin() {
        return user != null && Boolean.TRUE.equals(user.get("isAdmin"));
    }

    /**
     * Authorization header formatında token getirir
     * Returns token in Authorization header format
     * 
     * @return "Bearer " + token
     */
    public String getAuthorizationHeader() {
        return hasValidToken() ? tokenType + " " + token : null;
    }

    /**
     * Yanıtın başarılı olup olmadığını kontrol eder
     * Checks if response is successful
     * 
     * @return true: başarılı, false: başarısız
     */
    public boolean isSuccessful() {
        return hasValidToken() && hasUserInfo();
    }

    /**
     * String temsili - String representation
     * 
     * @return String temsili (token gizli)
     */
    @Override
    public String toString() {
        return "LoginResponse{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", userEmail='" + (user != null ? user.get("email") : "N/A") + '\'' +
                ", userRole='" + getUserRole() + '\'' +
                ", loginTime=" + loginTime +
                ", message='" + message + '\'' +
                ", hasToken=" + hasValidToken() +
                '}';
    }

    /**
     * LoginResponse Kullanım Örnekleri - LoginResponse Usage Examples
     * 
     * Frontend'de kullanım - Frontend usage:
     * 
     * // React/JavaScript
     * const handleLogin = async (credentials) => {
     *   try {
     *     const response = await fetch('/api/auth/login', {
     *       method: 'POST',
     *       headers: { 'Content-Type': 'application/json' },
     *       body: JSON.stringify(credentials)
     *     });
     * 
     *     if (response.ok) {
     *       const loginResponse = await response.json();
     *       
     *       // Token'ı sakla
     *       localStorage.setItem('token', loginResponse.token);
     *       localStorage.setItem('tokenType', loginResponse.tokenType);
     *       localStorage.setItem('user', JSON.stringify(loginResponse.user));
     *       
     *       // Token yenileme zamanını hesapla
     *       const expirationTime = Date.now() + (loginResponse.expiresIn * 1000);
     *       localStorage.setItem('tokenExpiration', expirationTime);
     *       
     *       // Kullanıcı bilgilerini kullan
     *       console.log('Welcome', loginResponse.user.fullName);
     *       console.log('Role:', loginResponse.user.role);
     *       
     *       // Authorization header'ını ayarla
     *       axios.defaults.headers.common['Authorization'] = 
     *         `${loginResponse.tokenType} ${loginResponse.token}`;
     *     }
     *   } catch (error) {
     *     console.error('Login failed:', error);
     *   }
     * };
     * 
     * Token Yenileme Kontrolü - Token Refresh Check:
     * 
     * const checkTokenExpiration = () => {
     *   const expirationTime = localStorage.getItem('tokenExpiration');
     *   const currentTime = Date.now();
     *   
     *   // Token'ın süresi 5 dakika içinde dolacaksa yenile
     *   if (expirationTime && (expirationTime - currentTime) < 300000) {
     *     refreshToken();
     *   }
     * };
     * 
     * setInterval(checkTokenExpiration, 60000); // Her dakika kontrol et
     * 
     * Axios Interceptor Kullanımı - Axios Interceptor Usage:
     * 
     * axios.interceptors.response.use(
     *   response => response,
     *   error => {
     *     if (error.response?.status === 401) {
     *       // Token geçersiz, login sayfasına yönlendir
     *       localStorage.removeItem('token');
     *       localStorage.removeItem('user');
     *       window.location.href = '/login';
     *     }
     *     return Promise.reject(error);
     *   }
     * );
     */
}
