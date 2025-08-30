package com.anket.service;

import com.anket.entity.User;
import com.anket.entity.UserRole;
import com.anket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Kullanıcı Servis Sınıfı - User Service Class
 * 
 * Kullanıcı ile ilgili iş mantığını gerçekleştirir
 * Handles business logic related to users
 * 
 * Bu sınıf kullanıcı CRUD işlemlerini, doğrulama işlemlerini ve 
 * kullanıcı yönetimi fonksiyonlarını sağlar
 * This class provides user CRUD operations, validation, and user management functions
 * 
 * İlişkili Sınıflar - Related Classes:
 * - UserRepository: Veritabanı işlemleri için
 * - User entity: Kullanıcı verilerini temsil eder
 * - AuthService: Kimlik doğrulama işlemlerinde kullanır
 * - UserController: REST API endpoint'lerinde kullanır
 * 
 * @Service: Spring'in bu sınıfı servis bean'i olarak tanımasını sağlar
 * @Transactional: Veritabanı işlemlerinin transaction içinde çalışmasını sağlar
 */
@Service
@Transactional
public class UserService {

    /**
     * Kullanıcı Repository - User Repository
     * 
     * Veritabanı işlemleri için kullanılır
     * Used for database operations
     */
    private final UserRepository userRepository;

    /**
     * Şifre Encoder - Password Encoder
     * 
     * Şifreleri hashlemek ve doğrulamak için kullanılır
     * Used for hashing and verifying passwords
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor - Yapıcı Metod
     * 
     * Spring Dependency Injection ile gerekli bağımlılıkları enjekte eder
     * Injects required dependencies via Spring Dependency Injection
     * 
     * @param userRepository Kullanıcı repository
     * @param passwordEncoder Şifre encoder
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CRUD İşlemleri - CRUD Operations

    /**
     * Yeni kullanıcı oluşturur - Creates new user
     * 
     * Bu metod yeni bir kullanıcı kaydı oluşturur, şifreyi hashler ve veritabanına kaydeder
     * This method creates a new user record, hashes password and saves to database
     * 
     * @param user Oluşturulacak kullanıcı bilgileri
     * @return Oluşturulan kullanıcı
     * @throws IllegalArgumentException E-posta zaten varsa
     * 
     * İlişkili metodlar - Related methods:
     * - AuthService.register(): Kullanıcı kaydı için
     * - AdminController.createUser(): Admin panelinde kullanıcı oluşturma
     */
    public User createUser(User user) {
        // E-posta kontrolü - Email validation
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Bu e-posta adresi zaten kullanılıyor - This email address is already in use");
        }

        // Şifreyi hashle - Hash the password
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        // Varsayılan rol ata (eğer atanmamışsa) - Assign default role if not set
        if (user.getRole() == null) {
            user.setRole(UserRole.USER);
        }

        // Kullanıcıyı kaydet - Save user
        User savedUser = userRepository.save(user);
        
        System.out.println("✅ Yeni kullanıcı oluşturuldu - New user created: " + savedUser.getEmail());
        return savedUser;
    }

    /**
     * Kullanıcı bilgilerini günceller - Updates user information
     * 
     * @param userId Güncellenecek kullanıcının ID'si
     * @param updatedUser Güncellenmiş kullanıcı bilgileri
     * @return Güncellenmiş kullanıcı
     * @throws RuntimeException Kullanıcı bulunamazsa
     */
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - User not found: " + userId));

        // Güncellenebilir alanları kontrol et ve güncelle
        // Check and update updatable fields
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        
        // E-posta güncellemesi (benzersizlik kontrolü ile)
        // Email update (with uniqueness check)
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(updatedUser.getEmail())) {
                throw new IllegalArgumentException("Bu e-posta adresi zaten kullanılıyor - This email address is already in use");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        User savedUser = userRepository.save(existingUser);
        System.out.println("🔄 Kullanıcı güncellendi - User updated: " + savedUser.getEmail());
        return savedUser;
    }

    /**
     * Kullanıcı şifresini günceller - Updates user password
     * 
     * @param userId Kullanıcı ID'si
     * @param oldPassword Eski şifre
     * @param newPassword Yeni şifre
     * @return Güncelleme başarılı mı
     */
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - User not found: " + userId));

        // Eski şifreyi kontrol et - Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mevcut şifre yanlış - Current password is incorrect");
        }

        // Yeni şifreyi hashle ve kaydet - Hash new password and save
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedNewPassword);
        userRepository.save(user);

        System.out.println("🔐 Kullanıcı şifresi güncellendi - User password updated: " + user.getEmail());
        return true;
    }

    /**
     * Kullanıcıyı soft delete yapar - Performs soft delete on user
     * 
     * @param userId Silinecek kullanıcının ID'si
     * @return Silme işlemi başarılı mı
     */
    public boolean deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - User not found: " + userId));

        user.softDelete(); // isActive = false yapar
        userRepository.save(user);

        System.out.println("🗑️ Kullanıcı silindi (soft delete) - User deleted (soft delete): " + user.getEmail());
        return true;
    }

    // Arama ve Listeleme İşlemleri - Search and Listing Operations

    /**
     * Tüm aktif kullanıcıları getirir - Returns all active users
     * 
     * @return Aktif kullanıcıların listesi
     */
    @Transactional(readOnly = true)
    public List<User> findAllActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    /**
     * ID'ye göre kullanıcı bulur - Finds user by ID
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcı (bulunamazsa Optional.empty())
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * E-posta adresine göre kullanıcı bulur - Finds user by email
     * 
     * @param email E-posta adresi
     * @return Kullanıcı (bulunamazsa Optional.empty())
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Role göre kullanıcıları getirir - Returns users by role
     * 
     * @param role Kullanıcı rolü
     * @return Bu role sahip kullanıcıların listesi
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRoleAndIsActiveTrue(role);
    }

    /**
     * Ad ve soyada göre kullanıcı arar - Searches users by first and last name
     * 
     * @param firstName Ad (kısmi eşleşme)
     * @param lastName Soyad (kısmi eşleşme)
     * @return Eşleşen kullanıcıların listesi
     */
    @Transactional(readOnly = true)
    public List<User> searchByName(String firstName, String lastName) {
        return userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndIsActiveTrue(
                firstName, lastName);
    }

    /**
     * E-posta adresine göre kısmi arama yapar - Performs partial search by email
     * 
     * @param email E-posta (kısmi)
     * @return Eşleşen kullanıcıların listesi
     */
    @Transactional(readOnly = true)
    public List<User> searchByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCaseAndIsActiveTrue(email);
    }

    // İstatistik ve Sayım İşlemleri - Statistics and Counting Operations

    /**
     * Toplam aktif kullanıcı sayısını getirir - Returns total active user count
     * 
     * @return Aktif kullanıcı sayısı
     */
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByIsActiveTrue();
    }

    /**
     * Belirli role sahip kullanıcı sayısını getirir - Returns user count by role
     * 
     * @param role Kullanıcı rolü
     * @return Bu role sahip kullanıcı sayısı
     */
    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return userRepository.countByRoleAndIsActiveTrue(role);
    }

    /**
     * Son 30 günde oluşturulan kullanıcı sayısını getirir
     * Returns count of users created in last 30 days
     * 
     * @return Son 30 gündeki yeni kullanıcı sayısı
     */
    @Transactional(readOnly = true)
    public long getRecentUserCount() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.countByCreatedDateAfterAndIsActiveTrue(thirtyDaysAgo);
    }

    /**
     * Kullanıcının oluşturduğu anket sayısını getirir
     * Returns count of surveys created by user
     * 
     * @param userId Kullanıcı ID'si
     * @return Bu kullanıcının oluşturduğu anket sayısı
     */
    @Transactional(readOnly = true)
    public long getSurveyCountByUser(Long userId) {
        return userRepository.countSurveysByUserId(userId);
    }

    /**
     * Kullanıcının verdiği yanıt sayısını getirir
     * Returns count of responses given by user
     * 
     * @param userId Kullanıcı ID'si
     * @return Bu kullanıcının verdiği yanıt sayısı
     */
    @Transactional(readOnly = true)
    public long getResponseCountByUser(Long userId) {
        return userRepository.countResponsesByUserId(userId);
    }

    // Doğrulama ve Kontrol İşlemleri - Validation and Check Operations

    /**
     * E-posta adresinin var olup olmadığını kontrol eder
     * Checks if email address exists
     * 
     * @param email Kontrol edilecek e-posta adresi
     * @return true: e-posta mevcut, false: e-posta mevcut değil
     */
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Kullanıcının aktif olup olmadığını kontrol eder
     * Checks if user is active
     * 
     * @param userId Kullanıcı ID'si
     * @return true: aktif, false: aktif değil
     */
    @Transactional(readOnly = true)
    public boolean isUserActive(Long userId) {
        return userRepository.isUserActive(userId);
    }

    /**
     * Şifre doğrulaması yapar - Validates password
     * 
     * @param rawPassword Ham şifre
     * @param encodedPassword Hashlenmiş şifre
     * @return true: şifreler eşleşiyor, false: eşleşmiyor
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Admin İşlemleri - Admin Operations

    /**
     * Kullanıcının rolünü günceller - Updates user role
     * 
     * @param userId Kullanıcı ID'si
     * @param newRole Yeni rol
     * @return Güncellenmiş kullanıcı
     */
    public User updateUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - User not found: " + userId));

        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        User savedUser = userRepository.save(user);

        System.out.println("👑 Kullanıcı rolü güncellendi - User role updated: " + 
                         user.getEmail() + " (" + oldRole + " -> " + newRole + ")");
        return savedUser;
    }

    /**
     * Kullanıcıyı tekrar aktif hale getirir - Reactivates user
     * 
     * @param userId Aktif hale getirilecek kullanıcının ID'si
     * @return Aktivasyon başarılı mı
     */
    public boolean reactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - User not found: " + userId));

        user.activate(); // isActive = true yapar
        userRepository.save(user);

        System.out.println("✅ Kullanıcı tekrar aktif edildi - User reactivated: " + user.getEmail());
        return true;
    }

    /**
     * Tüm admin kullanıcılarını getirir - Returns all admin users
     * 
     * @return Admin kullanıcılarının listesi
     */
    @Transactional(readOnly = true)
    public List<User> findAllAdmins() {
        return userRepository.findByRoleAndIsActiveTrue(UserRole.ADMIN);
    }

    /**
     * En aktif kullanıcıları getirir - Returns most active users
     * 
     * @param limit Getirilecek kullanıcı sayısı
     * @return En aktif kullanıcıların listesi
     */
    @Transactional(readOnly = true)
    public List<User> findMostActiveUsers(int limit) {
        return userRepository.findMostActiveUsers(limit);
    }

    /**
     * Son oluşturulan kullanıcıları getirir - Returns recently created users
     * 
     * @param days Kaç gün öncesine kadar
     * @return Son oluşturulan kullanıcılar
     */
    @Transactional(readOnly = true)
    public List<User> findRecentUsers(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return userRepository.findByCreatedDateAfterAndIsActiveTrue(startDate);
    }

    /**
     * Anket oluşturan kullanıcıları getirir - Returns users who created surveys
     * 
     * @return Anket oluşturan kullanıcıların listesi
     */
    @Transactional(readOnly = true)
    public List<User> findSurveyCreators() {
        return userRepository.findUsersWithSurveys();
    }

    /**
     * Ankete yanıt veren kullanıcıları getirir - Returns users who responded to surveys
     * 
     * @return Ankete yanıt veren kullanıcıların listesi
     */
    @Transactional(readOnly = true)
    public List<User> findSurveyResponders() {
        return userRepository.findUsersWithResponses();
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * Kullanıcı bilgilerini doğrular - Validates user information
     * 
     * @param user Doğrulanacak kullanıcı
     * @throws IllegalArgumentException Geçersiz bilgi varsa
     */
    private void validateUser(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Ad alanı boş olamaz - First name cannot be empty");
        }
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Soyad alanı boş olamaz - Last name cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("E-posta alanı boş olamaz - Email cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Şifre en az 6 karakter olmalıdır - Password must be at least 6 characters");
        }
    }

    /**
     * Kullanıcının tam adını getirir - Returns user's full name
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcının tam adı
     */
    @Transactional(readOnly = true)
    public String getUserFullName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFullName)
                .orElse("Bilinmeyen Kullanıcı - Unknown User");
    }

    /**
     * Kullanıcı istatistiklerini getirir - Returns user statistics
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcı istatistik bilgileri
     */
    @Transactional(readOnly = true)
    public String getUserStatistics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı - User not found: " + userId));

        long surveyCount = getSurveyCountByUser(userId);
        long responseCount = getResponseCountByUser(userId);

        return String.format("👤 %s - 📊 %d Anket, 📝 %d Yanıt", 
                           user.getFullName(), surveyCount, responseCount);
    }

    /**
     * Debug için kullanıcı bilgilerini loglar - Logs user information for debugging
     * 
     * @param userId Kullanıcı ID'si
     */
    public void logUserInfo(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(
            user -> System.out.println("📋 Kullanıcı Bilgisi - User Info: " + user.toString()),
            () -> System.out.println("❌ Kullanıcı bulunamadı - User not found: " + userId)
        );
    }
}
