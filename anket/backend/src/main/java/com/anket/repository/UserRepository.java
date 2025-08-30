package com.anket.repository;

import com.anket.entity.User;
import com.anket.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Kullanıcı Repository Arayüzü - User Repository Interface
 * 
 * Kullanıcı entity'si için veritabanı işlemlerini gerçekleştirir
 * Performs database operations for User entity
 * 
 * Spring Data JPA'nın JpaRepository'sini genişletir
 * Extends Spring Data JPA's JpaRepository
 * 
 * İlişkili Sınıflar - Related Classes:
 * - User entity: Veritabanı tablosunu temsil eder
 * - UserService: Bu repository'yi kullanarak iş mantığını gerçekleştirir
 * - AuthService: Kimlik doğrulama işlemlerinde kullanır
 * 
 * @Repository: Spring'in bu sınıfı repository bean'i olarak tanımasını sağlar
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * E-posta adresine göre kullanıcı bulur - Finds user by email address
     * 
     * Giriş işlemlerinde kullanılır çünkü e-posta benzersizdir
     * Used in login operations because email is unique
     * 
     * @param email Aranacak e-posta adresi
     * @return Kullanıcı (bulunamazsa Optional.empty())
     * 
     * İlişkili metodlar - Related methods:
     * - AuthService.authenticate(): Giriş doğrulaması için
     * - UserService.findByEmail(): Kullanıcı arama için
     */
    Optional<User> findByEmail(String email);

    /**
     * E-posta adresinin var olup olmadığını kontrol eder
     * Checks if email address exists
     * 
     * Yeni kullanıcı kaydında e-posta tekrarını önlemek için kullanılır
     * Used to prevent email duplication in new user registration
     * 
     * @param email Kontrol edilecek e-posta adresi
     * @return true: e-posta mevcut, false: e-posta mevcut değil
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.isEmailExists(): E-posta kontrolü için
     * - AuthService.register(): Kayıt işleminde kontrol için
     */
    boolean existsByEmail(String email);

    /**
     * Aktif kullanıcıları getirir - Returns active users
     * 
     * Soft delete yapılan kullanıcıları hariç tutar
     * Excludes soft deleted users
     * 
     * @return Aktif kullanıcıların listesi
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.findAllActiveUsers(): Aktif kullanıcı listesi için
     * - AdminController.getUsers(): Admin panelinde kullanıcı listesi için
     */
    List<User> findByIsActiveTrue();

    /**
     * Belirli bir role sahip kullanıcıları getirir
     * Returns users with specific role
     * 
     * @param role Aranacak kullanıcı rolü
     * @return Bu role sahip kullanıcıların listesi
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.findByRole(): Role göre kullanıcı arama
     * - AdminController.getAdminUsers(): Admin kullanıcıları listeleme
     */
    List<User> findByRole(UserRole role);

    /**
     * Aktif admin kullanıcılarını getirir
     * Returns active admin users
     * 
     * @return Aktif admin kullanıcılarının listesi
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.findActiveAdmins(): Aktif admin listesi
     * - AdminController.getAdminList(): Admin yönetimi için
     */
    List<User> findByRoleAndIsActiveTrue(UserRole role);

    /**
     * Ad ve/veya soyadına göre kullanıcı arar (büyük/küçük harf duyarsız)
     * Searches users by first name and/or last name (case insensitive)
     * 
     * @param firstName Aranacak ad (kısmi eşleşme)
     * @param lastName Aranacak soyad (kısmi eşleşme)
     * @return Eşleşen kullanıcıların listesi
     * 
     * ILIKE: PostgreSQL'de büyük/küçük harf duyarsız arama
     * ILIKE: Case insensitive search in PostgreSQL
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.searchByName(): İsim ile arama
     * - AdminController.searchUsers(): Admin panelinde kullanıcı arama
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
           "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
           "u.isActive = true")
    List<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCaseAndIsActiveTrue(
            @Param("firstName") String firstName, 
            @Param("lastName") String lastName);

    /**
     * E-posta adresine göre kısmi arama yapar (büyük/küçük harf duyarsız)
     * Performs partial search by email address (case insensitive)
     * 
     * @param email Aranacak e-posta (kısmi)
     * @return Eşleşen kullanıcıların listesi
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.searchByEmail(): E-posta ile arama
     * - AdminController.searchUsers(): Admin panelinde arama
     */
    List<User> findByEmailContainingIgnoreCaseAndIsActiveTrue(String email);

    /**
     * Belirli bir tarihten sonra oluşturulan kullanıcıları getirir
     * Returns users created after specific date
     * 
     * @param date Başlangıç tarihi
     * @return Bu tarihten sonra oluşturulan kullanıcılar
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.findRecentUsers(): Son kullanıcılar
     * - StatisticsService.getNewUserCount(): Yeni kullanıcı istatistikleri
     */
    List<User> findByCreatedDateAfterAndIsActiveTrue(LocalDateTime date);

    /**
     * Belirli bir tarih aralığındaki kullanıcıları getirir
     * Returns users created within specific date range
     * 
     * @param startDate Başlangıç tarihi
     * @param endDate Bitiş tarihi
     * @return Bu tarih aralığındaki kullanıcılar
     * 
     * İlişkili metodlar - Related methods:
     * - StatisticsService.getUserCountByDateRange(): Tarih aralığı istatistikleri
     * - AdminController.getUsersByDateRange(): Admin raporları
     */
    List<User> findByCreatedDateBetweenAndIsActiveTrue(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Toplam aktif kullanıcı sayısını getirir
     * Returns total count of active users
     * 
     * @return Aktif kullanıcı sayısı
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.getActiveUserCount(): Kullanıcı sayısı
     * - StatisticsService.getTotalUserCount(): İstatistikler için
     * - DashboardController.getUserStats(): Dashboard için
     */
    long countByIsActiveTrue();

    /**
     * Belirli role sahip aktif kullanıcı sayısını getirir
     * Returns count of active users with specific role
     * 
     * @param role Sayılacak kullanıcı rolü
     * @return Bu role sahip aktif kullanıcı sayısı
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.countByRole(): Role göre sayım
     * - StatisticsService.getAdminCount(): Admin sayısı
     */
    long countByRoleAndIsActiveTrue(UserRole role);

    /**
     * Son 30 günde oluşturulan kullanıcı sayısını getirir
     * Returns count of users created in last 30 days
     * 
     * @param date 30 gün önceki tarih
     * @return Son 30 gündeki yeni kullanıcı sayısı
     * 
     * İlişkili metodlar - Related methods:
     * - StatisticsService.getRecentUserCount(): Son kullanıcı istatistikleri
     * - DashboardController.getRecentStats(): Dashboard istatistikleri
     */
    long countByCreatedDateAfterAndIsActiveTrue(LocalDateTime date);

    /**
     * En az bir anket oluşturmuş kullanıcıları getirir
     * Returns users who have created at least one survey
     * 
     * @return Anket oluşturan kullanıcıların listesi
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.findSurveyCreators(): Anket oluşturan kullanıcılar
     * - StatisticsService.getActiveSurveyCreators(): Aktif anket oluşturucuları
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.createdSurveys s WHERE u.isActive = true AND s.isActive = true")
    List<User> findUsersWithSurveys();

    /**
     * En az bir ankete yanıt vermiş kullanıcıları getirir
     * Returns users who have responded to at least one survey
     * 
     * @return Ankete yanıt veren kullanıcıların listesi
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.findSurveyResponders(): Ankete yanıt veren kullanıcılar
     * - StatisticsService.getActiveResponders(): Aktif yanıtlayıcılar
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.responses r WHERE u.isActive = true AND r.isActive = true")
    List<User> findUsersWithResponses();

    /**
     * Kullanıcının oluşturduğu anket sayısını getirir
     * Returns count of surveys created by user
     * 
     * @param userId Kullanıcı ID'si
     * @return Bu kullanıcının oluşturduğu anket sayısı
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.getSurveyCountByUser(): Kullanıcının anket sayısı
     * - ProfileController.getUserStats(): Profil istatistikleri
     */
    @Query("SELECT COUNT(s) FROM Survey s WHERE s.creator.id = :userId AND s.isActive = true")
    long countSurveysByUserId(@Param("userId") Long userId);

    /**
     * Kullanıcının verdiği yanıt sayısını getirir
     * Returns count of responses given by user
     * 
     * @param userId Kullanıcı ID'si
     * @return Bu kullanıcının verdiği yanıt sayısı
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.getResponseCountByUser(): Kullanıcının yanıt sayısı
     * - ProfileController.getUserStats(): Profil istatistikleri
     */
    @Query("SELECT COUNT(r) FROM Response r WHERE r.user.id = :userId AND r.isActive = true")
    long countResponsesByUserId(@Param("userId") Long userId);

    /**
     * E-posta ve şifreye göre kullanıcı bulur (giriş için)
     * Finds user by email and password (for login)
     * 
     * Güvenlik nedeniyle şifre hashlenerek karşılaştırılmalı
     * Password should be compared after hashing for security
     * 
     * @param email E-posta adresi
     * @param password Hashlenmiş şifre
     * @return Kullanıcı (bulunamazsa Optional.empty())
     * 
     * İlişkili metodlar - Related methods:
     * - AuthService.authenticate(): Giriş doğrulaması
     * - LoginController.login(): Giriş endpoint'i
     */
    Optional<User> findByEmailAndPasswordAndIsActiveTrue(String email, String password);

    /**
     * Kullanıcının aktif olup olmadığını kontrol eder
     * Checks if user is active
     * 
     * @param userId Kullanıcı ID'si
     * @return true: aktif, false: aktif değil veya bulunamadı
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.isUserActive(): Kullanıcı aktiflik kontrolü
     * - SecurityService.validateUser(): Güvenlik kontrolü
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :userId AND u.isActive = true")
    boolean isUserActive(@Param("userId") Long userId);

    /**
     * En aktif kullanıcıları getirir (en çok anket oluşturan)
     * Returns most active users (who created most surveys)
     * 
     * @param limit Getirilecek kullanıcı sayısı
     * @return En aktif kullanıcıların listesi
     * 
     * İlişkili metodlar - Related methods:
     * - StatisticsService.getMostActiveUsers(): En aktif kullanıcılar
     * - DashboardController.getTopUsers(): Dashboard için top kullanıcılar
     */
    @Query("SELECT u FROM User u LEFT JOIN u.createdSurveys s " +
           "WHERE u.isActive = true " +
           "GROUP BY u " +
           "ORDER BY COUNT(s) DESC")
    List<User> findMostActiveUsers(@Param("limit") int limit);

    /**
     * Kullanıcıyı soft delete yapar (isActive = false)
     * Performs soft delete on user (sets isActive = false)
     * 
     * @param userId Silinecek kullanıcının ID'si
     * @return Etkilenen satır sayısı
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.deleteUser(): Kullanıcı silme
     * - AdminController.deleteUser(): Admin panelinde kullanıcı silme
     */
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :userId")
    int softDeleteUser(@Param("userId") Long userId);

    /**
     * Kullanıcıyı tekrar aktif hale getirir
     * Reactivates user (sets isActive = true)
     * 
     * @param userId Aktif hale getirilecek kullanıcının ID'si
     * @return Etkilenen satır sayısı
     * 
     * İlişkili metodlar - Related methods:
     * - UserService.reactivateUser(): Kullanıcıyı tekrar aktif etme
     * - AdminController.reactivateUser(): Admin panelinde kullanıcı aktifleştirme
     */
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :userId")
    int reactivateUser(@Param("userId") Long userId);
}
