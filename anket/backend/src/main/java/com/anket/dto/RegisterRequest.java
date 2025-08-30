package com.anket.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Kayıt İsteği DTO - Registration Request DTO
 * 
 * Yeni kullanıcı kaydı için gerekli bilgileri içerir
 * Contains required information for new user registration
 * 
 * Bu sınıf şu amaçlarla kullanılır:
 * This class is used for the following purposes:
 * - Frontend'den gelen kayıt verilerini almak (Receive registration data from frontend)
 * - Veri doğrulama işlemleri (Data validation)
 * - Controller katmanında parametre olarak kullanılır (Used as parameter in controller layer)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - AuthController.register(): Bu DTO'yu parametre olarak alır
 * - RegisterResponse: Kayıt başarılı olduğunda döndürülen yanıt
 * - User entity: Bu DTO'dan User entity'si oluşturulur
 * 
 * JSON Formatı - JSON Format:
 * {
 *   "firstName": "John",
 *   "lastName": "Doe",
 *   "email": "john.doe@example.com",
 *   "password": "password123",
 *   "confirmPassword": "password123"
 * }
 */
public class RegisterRequest {

    /**
     * Ad - First Name
     * 
     * Kullanıcının adı
     * User's first name
     * 
     * Doğrulama kuralları - Validation rules:
     * - Boş olamaz (@NotBlank)
     * - Minimum 2, maksimum 50 karakter (@Size)
     */
    @NotBlank(message = "Ad alanı boş olamaz - First name cannot be empty")
    @Size(min = 2, max = 50, message = "Ad 2-50 karakter arasında olmalıdır - First name must be between 2-50 characters")
    private String firstName;

    /**
     * Soyad - Last Name
     * 
     * Kullanıcının soyadı
     * User's last name
     * 
     * Doğrulama kuralları - Validation rules:
     * - Boş olamaz (@NotBlank)
     * - Minimum 2, maksimum 50 karakter (@Size)
     */
    @NotBlank(message = "Soyad alanı boş olamaz - Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Soyad 2-50 karakter arasında olmalıdır - Last name must be between 2-50 characters")
    private String lastName;

    /**
     * E-posta Adresi - Email Address
     * 
     * Kullanıcının benzersiz e-posta adresi (giriş için kullanılacak)
     * User's unique email address (will be used for login)
     * 
     * Doğrulama kuralları - Validation rules:
     * - Boş olamaz (@NotBlank)
     * - Geçerli e-posta formatında olmalı (@Email)
     * - Maksimum 100 karakter (@Size)
     * - Benzersiz olmalı (controller'da kontrol edilir)
     */
    @NotBlank(message = "E-posta adresi boş olamaz - Email address cannot be empty")
    @Email(message = "Geçerli bir e-posta adresi giriniz - Please enter a valid email address")
    @Size(max = 100, message = "E-posta adresi maksimum 100 karakter olabilir - Email address can be maximum 100 characters")
    private String email;

    /**
     * Şifre - Password
     * 
     * Kullanıcının hesap şifresi
     * User's account password
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
     * Şifre Onayı - Confirm Password
     * 
     * Şifrenin doğru girildiğinden emin olmak için tekrar girilen şifre
     * Password entered again to ensure correct entry
     * 
     * Doğrulama kuralları - Validation rules:
     * - Boş olamaz (@NotBlank)
     * - Şifre ile eşleşmeli (controller'da kontrol edilir)
     */
    @NotBlank(message = "Şifre onayı boş olamaz - Password confirmation cannot be empty")
    private String confirmPassword;

    /**
     * Kullanım Şartları Kabul - Terms of Service Acceptance
     * 
     * Kullanıcının kullanım şartlarını kabul edip etmediği
     * Whether user accepts terms of service
     * 
     * Opsiyonel alan - Optional field
     * Varsayılan değer: false
     */
    private Boolean acceptTerms = false;

    /**
     * Pazarlama E-postaları - Marketing Emails
     * 
     * Kullanıcının pazarlama e-postaları almayı kabul edip etmediği
     * Whether user accepts to receive marketing emails
     * 
     * Opsiyonel alan - Optional field
     * Varsayılan değer: false
     */
    private Boolean acceptMarketing = false;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * 
     * Jackson JSON deserializasyonu için gerekli
     * Required for Jackson JSON deserialization
     */
    public RegisterRequest() {
    }

    /**
     * Temel Constructor - Basic Constructor
     * 
     * @param firstName Ad
     * @param lastName Soyad
     * @param email E-posta adresi
     * @param password Şifre
     * @param confirmPassword Şifre onayı
     */
    public RegisterRequest(String firstName, String lastName, String email, 
                          String password, String confirmPassword) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.acceptTerms = false;
        this.acceptMarketing = false;
    }

    /**
     * Tam Constructor - Full Constructor
     * 
     * @param firstName Ad
     * @param lastName Soyad
     * @param email E-posta adresi
     * @param password Şifre
     * @param confirmPassword Şifre onayı
     * @param acceptTerms Kullanım şartları kabul
     * @param acceptMarketing Pazarlama e-postaları kabul
     */
    public RegisterRequest(String firstName, String lastName, String email, 
                          String password, String confirmPassword,
                          Boolean acceptTerms, Boolean acceptMarketing) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.acceptTerms = acceptTerms;
        this.acceptMarketing = acceptMarketing;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    /**
     * Adı getirir - Returns first name
     * @return Ad
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Adı ayarlar - Sets first name
     * @param firstName Ad
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName != null ? firstName.trim() : null;
    }

    /**
     * Soyadı getirir - Returns last name
     * @return Soyad
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Soyadı ayarlar - Sets last name
     * @param lastName Soyad
     */
    public void setLastName(String lastName) {
        this.lastName = lastName != null ? lastName.trim() : null;
    }

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
     * Şifre onayını getirir - Returns password confirmation
     * @return Şifre onayı
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Şifre onayını ayarlar - Sets password confirmation
     * @param confirmPassword Şifre onayı
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Kullanım şartları kabulünü getirir - Returns terms acceptance
     * @return Kullanım şartları kabul durumu
     */
    public Boolean getAcceptTerms() {
        return acceptTerms;
    }

    /**
     * Kullanım şartları kabulünü ayarlar - Sets terms acceptance
     * @param acceptTerms Kullanım şartları kabul durumu
     */
    public void setAcceptTerms(Boolean acceptTerms) {
        this.acceptTerms = acceptTerms != null ? acceptTerms : false;
    }

    /**
     * Pazarlama e-postaları kabulünü getirir - Returns marketing emails acceptance
     * @return Pazarlama e-postaları kabul durumu
     */
    public Boolean getAcceptMarketing() {
        return acceptMarketing;
    }

    /**
     * Pazarlama e-postaları kabulünü ayarlar - Sets marketing emails acceptance
     * @param acceptMarketing Pazarlama e-postaları kabul durumu
     */
    public void setAcceptMarketing(Boolean acceptMarketing) {
        this.acceptMarketing = acceptMarketing != null ? acceptMarketing : false;
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * İsteğin geçerli olup olmadığını kontrol eder
     * Checks if request is valid
     * 
     * @return true: geçerli, false: geçersiz
     */
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty() &&
               lastName != null && !lastName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() && email.contains("@") &&
               password != null && password.length() >= 6 &&
               confirmPassword != null && password.equals(confirmPassword);
    }

    /**
     * Şifrelerin eşleşip eşleşmediğini kontrol eder
     * Checks if passwords match
     * 
     * @return true: eşleşiyor, false: eşleşmiyor
     */
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
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
     * Tam adı getirir
     * Returns full name
     * 
     * @return Ad + Soyad
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName.trim() + " " + lastName.trim();
        } else if (firstName != null) {
            return firstName.trim();
        } else if (lastName != null) {
            return lastName.trim();
        } else {
            return "";
        }
    }

    /**
     * Şifre gücünü kontrol eder
     * Checks password strength
     * 
     * @return Şifre güç seviyesi (1-5 arası)
     */
    public int getPasswordStrength() {
        if (password == null) return 0;
        
        int strength = 0;
        
        // Uzunluk kontrolü - Length check
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;
        
        // Karakter türü kontrolleri - Character type checks
        if (password.matches(".*[a-z].*")) strength++; // Küçük harf
        if (password.matches(".*[A-Z].*")) strength++; // Büyük harf
        if (password.matches(".*[0-9].*")) strength++; // Rakam
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength++; // Özel karakter
        
        return Math.min(strength, 5);
    }

    /**
     * Şifre güç açıklamasını getirir
     * Returns password strength description
     * 
     * @return Şifre güç açıklaması
     */
    public String getPasswordStrengthDescription() {
        int strength = getPasswordStrength();
        switch (strength) {
            case 0:
            case 1:
                return "Çok Zayıf - Very Weak";
            case 2:
                return "Zayıf - Weak";
            case 3:
                return "Orta - Medium";
            case 4:
                return "Güçlü - Strong";
            case 5:
                return "Çok Güçlü - Very Strong";
            default:
                return "Bilinmeyen - Unknown";
        }
    }

    /**
     * Validation hatalarını getirir
     * Returns validation errors
     * 
     * @return Hata listesi
     */
    public java.util.List<String> getValidationErrors() {
        java.util.List<String> errors = new java.util.ArrayList<>();
        
        if (firstName == null || firstName.trim().isEmpty()) {
            errors.add("Ad alanı boş olamaz - First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            errors.add("Soyad alanı boş olamaz - Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("E-posta adresi boş olamaz - Email address cannot be empty");
        } else if (!email.contains("@") || !email.contains(".")) {
            errors.add("Geçerli bir e-posta adresi giriniz - Please enter a valid email address");
        }
        if (password == null || password.length() < 6) {
            errors.add("Şifre en az 6 karakter olmalıdır - Password must be at least 6 characters");
        }
        if (!isPasswordMatching()) {
            errors.add("Şifreler eşleşmiyor - Passwords do not match");
        }
        
        return errors;
    }

    /**
     * Güvenli string temsili (şifreler hariç)
     * Safe string representation (excluding passwords)
     * 
     * @return Güvenli string
     */
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                ", confirmPassword='[PROTECTED]'" +
                ", acceptTerms=" + acceptTerms +
                ", acceptMarketing=" + acceptMarketing +
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
        
        RegisterRequest that = (RegisterRequest) obj;
        
        if (!firstName.equals(that.firstName)) return false;
        if (!lastName.equals(that.lastName)) return false;
        if (!email.equals(that.email)) return false;
        if (!password.equals(that.password)) return false;
        if (!confirmPassword.equals(that.confirmPassword)) return false;
        if (!acceptTerms.equals(that.acceptTerms)) return false;
        return acceptMarketing.equals(that.acceptMarketing);
    }

    /**
     * Hash kodu - Hash code
     * 
     * @return Hash kodu
     */
    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + confirmPassword.hashCode();
        result = 31 * result + acceptTerms.hashCode();
        result = 31 * result + acceptMarketing.hashCode();
        return result;
    }

    /**
     * RegisterRequest Kullanım Örnekleri - RegisterRequest Usage Examples
     * 
     * Frontend'de kullanım - Frontend usage:
     * 
     * // React/JavaScript
     * const registerData = {
     *   firstName: 'John',
     *   lastName: 'Doe',
     *   email: 'john.doe@example.com',
     *   password: 'securePassword123',
     *   confirmPassword: 'securePassword123',
     *   acceptTerms: true,
     *   acceptMarketing: false
     * };
     * 
     * const response = await fetch('/api/auth/register', {
     *   method: 'POST',
     *   headers: {
     *     'Content-Type': 'application/json'
     *   },
     *   body: JSON.stringify(registerData)
     * });
     * 
     * Şifre Güç Kontrolü - Password Strength Check:
     * 
     * const checkPasswordStrength = (password) => {
     *   const request = new RegisterRequest();
     *   request.setPassword(password);
     *   
     *   const strength = request.getPasswordStrength();
     *   const description = request.getPasswordStrengthDescription();
     *   
     *   console.log(`Password strength: ${strength}/5 - ${description}`);
     * };
     * 
     * Form Validation - Form Doğrulama:
     * 
     * const validateRegistrationForm = (formData) => {
     *   const request = new RegisterRequest();
     *   // Set form data...
     *   
     *   const errors = request.getValidationErrors();
     *   
     *   if (errors.length > 0) {
     *     console.error('Validation errors:', errors);
     *     return false;
     *   }
     *   
     *   return true;
     * };
     */
}
