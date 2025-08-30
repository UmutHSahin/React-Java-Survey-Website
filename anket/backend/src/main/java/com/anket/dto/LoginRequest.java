package com.anket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Giriş İsteği DTO - Login Request DTO
 * 
 * Kullanıcı giriş işlemi için gerekli bilgileri içerir
 * Contains required information for user login operation
 * 
 * Bu sınıf şu amaçlarla kullanılır:
 * This class is used for the following purposes:
 * - Frontend'den gelen giriş verilerini almak (Receive login data from frontend)
 * - Veri doğrulama işlemleri (Data validation)
 * - Controller katmanında parametre olarak kullanılır (Used as parameter in controller layer)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - AuthController.login(): Bu DTO'yu parametre olarak alır
 * - LoginResponse: Giriş başarılı olduğunda döndürülen yanıt
 * 
 * JSON Formatı - JSON Format:
 * {
 *   "email": "demo@example.com",
 *   "password": "password123"
 * }
 */
public class LoginRequest {

    /**
     * E-posta Adresi - Email Address
     * 
     * Kullanıcının giriş için kullandığı e-posta adresi
     * Email address used by user for login
     * 
     * Doğrulama kuralları - Validation rules:
     * - Boş olamaz (@NotBlank)
     * - Geçerli e-posta formatında olmalı (@Email)
     * - Maksimum 100 karakter (@Size)
     */
    @NotBlank(message = "E-posta adresi boş olamaz - Email address cannot be empty")
    @Email(message = "Geçerli bir e-posta adresi giriniz - Please enter a valid email address")
    @Size(max = 100, message = "E-posta adresi maksimum 100 karakter olabilir - Email address can be maximum 100 characters")
    private String email;

    /**
     * Şifre - Password
     * 
     * Kullanıcının giriş şifresi
     * User's login password
     * 
     * Doğrulama kuralları - Validation rules:
     * - Boş olamaz (@NotBlank)
     * - Minimum 6 karakter (@Size)
     * - Maksimum 100 karakter (güvenlik için)
     */
    @NotBlank(message = "Şifre boş olamaz - Password cannot be empty")
    @Size(min = 6, max = 100, message = "Şifre 6-100 karakter arasında olmalıdır - Password must be between 6-100 characters")
    private String password;

    /**
     * Beni Hatırla - Remember Me
     * 
     * Kullanıcının oturumunun uzun süre açık kalmasını isteyip istemediği
     * Whether user wants to keep session open for extended period
     * 
     * Opsiyonel alan - Optional field
     * Varsayılan değer: false
     */
    private Boolean rememberMe = false;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * 
     * Jackson JSON deserializasyonu için gerekli
     * Required for Jackson JSON deserialization
     */
    public LoginRequest() {
    }

    /**
     * Parametreli Constructor - Parameterized Constructor
     * 
     * @param email E-posta adresi
     * @param password Şifre
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.rememberMe = false;
    }

    /**
     * Tam Parametreli Constructor - Full Parameterized Constructor
     * 
     * @param email E-posta adresi
     * @param password Şifre
     * @param rememberMe Beni hatırla seçeneği
     */
    public LoginRequest(String email, String password, Boolean rememberMe) {
        this.email = email;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    /**
     * E-posta adresini getirir - Returns email address
     * @return E-posta adresi
     */
    public String getEmail() {
        return email;
    }

    /**
     * E-posta adresini ayarlar - Sets email address
     * @param email E-posta adresi
     */
    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : null;
    }

    /**
     * Şifreyi getirir - Returns password
     * @return Şifre
     */
    public String getPassword() {
        return password;
    }

    /**
     * Şifreyi ayarlar - Sets password
     * @param password Şifre
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Beni hatırla seçeneğini getirir - Returns remember me option
     * @return Beni hatırla durumu
     */
    public Boolean getRememberMe() {
        return rememberMe;
    }

    /**
     * Beni hatırla seçeneğini ayarlar - Sets remember me option
     * @param rememberMe Beni hatırla durumu
     */
    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe != null ? rememberMe : false;
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * İsteğin geçerli olup olmadığını kontrol eder
     * Checks if request is valid
     * 
     * @return true: geçerli, false: geçersiz
     */
    public boolean isValid() {
        return email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               email.contains("@") && email.contains(".");
    }

    /**
     * E-posta adresini normalize eder
     * Normalizes email address
     * 
     * @return Normalize edilmiş e-posta
     */
    public String getNormalizedEmail() {
        return email != null ? email.trim().toLowerCase() : null;
    }

    /**
     * Güvenli string temsili (şifre hariç)
     * Safe string representation (excluding password)
     * 
     * @return Güvenli string
     */
    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", rememberMe=" + rememberMe +
                '}';
    }

    /**
     * Eşitlik kontrolü - Equality check
     * 
     * @param obj Karşılaştırılacak nesne
     * @return Eşitlik durumu
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        LoginRequest that = (LoginRequest) obj;
        
        if (!email.equals(that.email)) return false;
        if (!password.equals(that.password)) return false;
        return rememberMe.equals(that.rememberMe);
    }

    /**
     * Hash kodu - Hash code
     * 
     * @return Hash kodu
     */
    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + rememberMe.hashCode();
        return result;
    }

    /**
     * LoginRequest Kullanım Örnekleri - LoginRequest Usage Examples
     * 
     * Frontend'de kullanım - Frontend usage:
     * 
     * // JavaScript/React
     * const loginData = {
     *   email: 'demo@example.com',
     *   password: 'password123',
     *   rememberMe: true
     * };
     * 
     * const response = await fetch('/api/auth/login', {
     *   method: 'POST',
     *   headers: {
     *     'Content-Type': 'application/json'
     *   },
     *   body: JSON.stringify(loginData)
     * });
     * 
     * Backend'de kullanım - Backend usage:
     * 
     * @PostMapping("/login")
     * public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
     *   // Validation otomatik olarak çalışır
     *   // Validation works automatically
     *   
     *   String email = request.getNormalizedEmail();
     *   String password = request.getPassword();
     *   Boolean rememberMe = request.getRememberMe();
     *   
     *   // Authentication logic...
     * }
     * 
     * Validation Hataları - Validation Errors:
     * 
     * Geçersiz veri gönderildiğinde Spring Boot otomatik olarak
     * HTTP 400 Bad Request yanıtı döndürür:
     * 
     * {
     *   "timestamp": "2024-01-15T10:30:45",
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Validation failed",
     *   "errors": [
     *     {
     *       "field": "email",
     *       "message": "Geçerli bir e-posta adresi giriniz"
     *     },
     *     {
     *       "field": "password",
     *       "message": "Şifre 6-100 karakter arasında olmalıdır"
     *     }
     *   ]
     * }
     * 
     * Güvenlik Notları - Security Notes:
     * - Şifre hiçbir zaman loglara yazılmamalı
     * - toString() metodunda şifre gizlenir
     * - E-posta adresi normalize edilir (küçük harf, trim)
     * - Validation kuralları güvenlik açıklarını önler
     */
}
