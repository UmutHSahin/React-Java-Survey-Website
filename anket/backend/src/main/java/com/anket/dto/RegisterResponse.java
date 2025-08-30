package com.anket.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Kayıt Yanıtı DTO - Registration Response DTO
 * 
 * Başarılı kullanıcı kaydı sonrasında döndürülen bilgileri içerir
 * Contains information returned after successful user registration
 * 
 * Bu sınıf şu bilgileri sağlar:
 * This class provides the following information:
 * - Başarı mesajı (Success message)
 * - JWT access token (otomatik giriş için) (JWT access token for auto-login)
 * - Yeni kullanıcı bilgileri (New user information)
 * - Kayıt zamanı ve ek metadata (Registration time and additional metadata)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - AuthController.register(): Bu DTO'yu yanıt olarak döndürür
 * - RegisterRequest: Kayıt isteği için kullanılan DTO
 * - LoginResponse: Benzer yapıda olan giriş yanıtı
 * 
 * JSON Formatı - JSON Format:
 * {
 *   "message": "Kayıt başarılı",
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "tokenType": "Bearer",
 *   "user": {
 *     "id": 1,
 *     "email": "john.doe@example.com",
 *     "firstName": "John",
 *     "lastName": "Doe",
 *     "role": "USER"
 *   },
 *   "registrationTime": "2024-01-15T10:30:45",
 *   "autoLogin": true
 * }
 */
public class RegisterResponse {

    /**
     * Başarı Mesajı - Success Message
     * 
     * Kayıt işleminin başarılı olduğuna dair mesaj
     * Message indicating successful registration
     */
    private String message;

    /**
     * JWT Access Token - JWT Erişim Token'ı (Opsiyonel)
     * 
     * Otomatik giriş için kullanılan token
     * Token used for automatic login
     * 
     * Eğer autoLogin true ise bu alan doldurulur
     * This field is filled if autoLogin is true
     */
    private String token;

    /**
     * Token Tipi - Token Type
     * 
     * Token'ın tipi (genellikle "Bearer")
     * Type of token (usually "Bearer")
     */
    private String tokenType = "Bearer";

    /**
     * Yeni Kullanıcı Bilgileri - New User Information
     * 
     * Kaydedilen kullanıcının temel bilgileri
     * Basic information of registered user
     * 
     * Güvenlik nedeniyle şifre gibi hassas bilgiler içermez
     * Does not contain sensitive information like password for security
     */
    private Map<String, Object> user;

    /**
     * Kayıt Zamanı - Registration Time
     * 
     * Kullanıcının ne zaman kayıt olduğu
     * When the user registered
     */
    private LocalDateTime registrationTime;

    /**
     * Otomatik Giriş - Auto Login
     * 
     * Kayıt sonrası otomatik giriş yapılıp yapılmadığı
     * Whether automatic login was performed after registration
     */
    private Boolean autoLogin = true;

    /**
     * Doğrulama Gerekli - Verification Required
     * 
     * E-posta doğrulaması gerekip gerekmediği
     * Whether email verification is required
     * 
     * Şu an kullanılmıyor ama gelecekte eklenebilir
     * Currently not used but can be added in future
     */
    private Boolean verificationRequired = false;

    /**
     * Doğrulama E-postası Gönderildi - Verification Email Sent
     * 
     * Doğrulama e-postasının gönderilip gönderilmediği
     * Whether verification email was sent
     */
    private Boolean verificationEmailSent = false;

    /**
     * Ek Metadata - Additional Metadata
     * 
     * Kayıt işlemiyle ilgili ek bilgiler
     * Additional information about registration process
     */
    private Map<String, Object> metadata;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * 
     * Jackson JSON serializasyonu için gerekli
     * Required for Jackson JSON serialization
     */
    public RegisterResponse() {
        this.registrationTime = LocalDateTime.now();
        this.message = "Kayıt başarılı - Registration successful";
        this.autoLogin = true;
        this.tokenType = "Bearer";
    }

    /**
     * Temel Constructor - Basic Constructor
     * 
     * @param message Başarı mesajı
     * @param user Kullanıcı bilgileri
     */
    public RegisterResponse(String message, Map<String, Object> user) {
        this();
        this.message = message;
        this.user = user;
    }

    /**
     * Otomatik Giriş ile Constructor - Constructor with Auto Login
     * 
     * @param message Başarı mesajı
     * @param token JWT token
     * @param user Kullanıcı bilgileri
     */
    public RegisterResponse(String message, String token, Map<String, Object> user) {
        this();
        this.message = message;
        this.token = token;
        this.user = user;
        this.autoLogin = true;
    }

    /**
     * Tam Constructor - Full Constructor
     * 
     * @param message Başarı mesajı
     * @param token JWT token
     * @param tokenType Token tipi
     * @param user Kullanıcı bilgileri
     * @param autoLogin Otomatik giriş durumu
     */
    public RegisterResponse(String message, String token, String tokenType, 
                           Map<String, Object> user, Boolean autoLogin) {
        this();
        this.message = message;
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
        this.autoLogin = autoLogin;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

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
        this.autoLogin = (token != null && !token.trim().isEmpty());
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
     * Kayıt zamanını getirir - Returns registration time
     * @return Kayıt zamanı
     */
    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    /**
     * Kayıt zamanını ayarlar - Sets registration time
     * @param registrationTime Kayıt zamanı
     */
    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    /**
     * Otomatik giriş durumunu getirir - Returns auto login status
     * @return Otomatik giriş durumu
     */
    public Boolean getAutoLogin() {
        return autoLogin;
    }

    /**
     * Otomatik giriş durumunu ayarlar - Sets auto login status
     * @param autoLogin Otomatik giriş durumu
     */
    public void setAutoLogin(Boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    /**
     * Doğrulama gerekli durumunu getirir - Returns verification required status
     * @return Doğrulama gerekli durumu
     */
    public Boolean getVerificationRequired() {
        return verificationRequired;
    }

    /**
     * Doğrulama gerekli durumunu ayarlar - Sets verification required status
     * @param verificationRequired Doğrulama gerekli durumu
     */
    public void setVerificationRequired(Boolean verificationRequired) {
        this.verificationRequired = verificationRequired;
    }

    /**
     * Doğrulama e-postası gönderildi durumunu getirir - Returns verification email sent status
     * @return Doğrulama e-postası gönderildi durumu
     */
    public Boolean getVerificationEmailSent() {
        return verificationEmailSent;
    }

    /**
     * Doğrulama e-postası gönderildi durumunu ayarlar - Sets verification email sent status
     * @param verificationEmailSent Doğrulama e-postası gönderildi durumu
     */
    public void setVerificationEmailSent(Boolean verificationEmailSent) {
        this.verificationEmailSent = verificationEmailSent;
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
     * Kayıt işleminin başarılı olup olmadığını kontrol eder
     * Checks if registration was successful
     * 
     * @return true: başarılı, false: başarısız
     */
    public boolean isSuccessful() {
        return message != null && user != null && !user.isEmpty();
    }

    /**
     * Otomatik girişin aktif olup olmadığını kontrol eder
     * Checks if auto login is active
     * 
     * @return true: aktif, false: pasif
     */
    public boolean hasAutoLogin() {
        return autoLogin != null && autoLogin && token != null && !token.trim().isEmpty();
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
     * Doğrulama sürecinin gerekli olup olmadığını kontrol eder
     * Checks if verification process is required
     * 
     * @return true: gerekli, false: gerekli değil
     */
    public boolean needsVerification() {
        return verificationRequired != null && verificationRequired;
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
     * Kullanıcının e-posta adresini getirir
     * Returns user's email address
     * 
     * @return Kullanıcı e-posta adresi
     */
    public String getUserEmail() {
        return user != null ? (String) user.get("email") : null;
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
     * Authorization header formatında token getirir
     * Returns token in Authorization header format
     * 
     * @return "Bearer " + token
     */
    public String getAuthorizationHeader() {
        return hasAutoLogin() ? tokenType + " " + token : null;
    }

    /**
     * Kayıt sonrası yapılması gereken işlemlerin listesini getirir
     * Returns list of actions to be taken after registration
     * 
     * @return İşlem listesi
     */
    public java.util.List<String> getNextSteps() {
        java.util.List<String> steps = new java.util.ArrayList<>();
        
        if (needsVerification()) {
            steps.add("E-posta adresinizi doğrulayın - Verify your email address");
        }
        
        if (hasAutoLogin()) {
            steps.add("Otomatik giriş yapıldı - Automatically logged in");
        } else {
            steps.add("Giriş sayfasından oturum açın - Login from the login page");
        }
        
        steps.add("Profilinizi tamamlayın - Complete your profile");
        steps.add("İlk anketinizi oluşturun - Create your first survey");
        
        return steps;
    }

    /**
     * Hoş geldin mesajı oluşturur
     * Creates welcome message
     * 
     * @return Hoş geldin mesajı
     */
    public String getWelcomeMessage() {
        String fullName = getUserFullName();
        return String.format("Hoş geldiniz %s! Anket uygulamasına başarıyla kayıt oldunuz. - " +
                           "Welcome %s! You have successfully registered to the survey application.", 
                           fullName, fullName);
    }

    /**
     * String temsili - String representation
     * 
     * @return String temsili (token gizli)
     */
    @Override
    public String toString() {
        return "RegisterResponse{" +
                "message='" + message + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", userEmail='" + getUserEmail() + '\'' +
                ", userRole='" + getUserRole() + '\'' +
                ", registrationTime=" + registrationTime +
                ", autoLogin=" + autoLogin +
                ", verificationRequired=" + verificationRequired +
                ", hasToken=" + hasAutoLogin() +
                '}';
    }

    /**
     * RegisterResponse Kullanım Örnekleri - RegisterResponse Usage Examples
     * 
     * Frontend'de kullanım - Frontend usage:
     * 
     * // React/JavaScript
     * const handleRegister = async (userData) => {
     *   try {
     *     const response = await fetch('/api/auth/register', {
     *       method: 'POST',
     *       headers: { 'Content-Type': 'application/json' },
     *       body: JSON.stringify(userData)
     *     });
     * 
     *     if (response.ok) {
     *       const registerResponse = await response.json();
     *       
     *       // Başarı mesajını göster
     *       alert(registerResponse.message);
     *       
     *       // Otomatik giriş varsa token'ı sakla
     *       if (registerResponse.autoLogin && registerResponse.token) {
     *         localStorage.setItem('token', registerResponse.token);
     *         localStorage.setItem('tokenType', registerResponse.tokenType);
     *         localStorage.setItem('user', JSON.stringify(registerResponse.user));
     *         
     *         // Hoş geldin mesajını göster
     *         console.log(registerResponse.getWelcomeMessage());
     *         
     *         // Ana sayfaya yönlendir
     *         window.location.href = '/dashboard';
     *       } else {
     *         // Login sayfasına yönlendir
     *         window.location.href = '/login';
     *       }
     *       
     *       // Sonraki adımları göster
     *       const nextSteps = registerResponse.getNextSteps();
     *       nextSteps.forEach(step => console.log('TODO:', step));
     *       
     *       // Doğrulama gerekiyorsa uyar
     *       if (registerResponse.verificationRequired) {
     *         alert('Lütfen e-posta adresinizi kontrol edin ve doğrulama bağlantısına tıklayın.');
     *       }
     *     }
     *   } catch (error) {
     *     console.error('Registration failed:', error);
     *   }
     * };
     * 
     * E-posta Doğrulama Kontrolü - Email Verification Check:
     * 
     * const checkEmailVerification = (registerResponse) => {
     *   if (registerResponse.needsVerification()) {
     *     // Doğrulama sayfasını göster
     *     showEmailVerificationPage();
     *     
     *     // Doğrulama e-postası gönderilmişse bilgi ver
     *     if (registerResponse.verificationEmailSent) {
     *       showMessage('Doğrulama e-postası gönderildi. Lütfen gelen kutunuzu kontrol edin.');
     *     }
     *   }
     * };
     * 
     * Kayıt Sonrası İşlemler - Post Registration Actions:
     * 
     * const handlePostRegistration = (registerResponse) => {
     *   // Kullanıcı bilgilerini yerel state'e kaydet
     *   const user = registerResponse.user;
     *   setCurrentUser(user);
     *   
     *   // Analytics event'i gönder
     *   analytics.track('User Registered', {
     *     userId: user.id,
     *     email: user.email,
     *     registrationTime: registerResponse.registrationTime,
     *     autoLogin: registerResponse.autoLogin
     *   });
     *   
     *   // Welcome tour'u başlat
     *   if (registerResponse.autoLogin) {
     *     startWelcomeTour();
     *   }
     * };
     */
}
