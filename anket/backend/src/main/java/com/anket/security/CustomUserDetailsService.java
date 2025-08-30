package com.anket.security;

import com.anket.entity.User;
import com.anket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

/**
 * Özel Kullanıcı Detay Servisi - Custom User Details Service
 * 
 * Spring Security'nin UserDetailsService arayüzünü implement eder
 * Implements Spring Security's UserDetailsService interface
 * 
 * Bu sınıf Spring Security'nin kullanıcı bilgilerini yüklemesi için gereklidir
 * This class is required for Spring Security to load user information
 * 
 * Temel işlevleri - Main functions:
 * - Kullanıcı adına göre kullanıcı bilgilerini veritabanından yükler
 * - User entity'sini Spring Security UserDetails'e dönüştürür
 * - Kullanıcının yetkilerini (authorities) belirler
 * - Hesap durumlarını kontrol eder (aktif/pasif, kilitli/açık)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - UserRepository: Kullanıcı bilgilerini veritabanından çekmek için
 * - JwtAuthenticationFilter: Token doğrulamada kullanıcı bilgilerini yüklemek için
 * - AuthService: Kimlik doğrulama işlemlerinde kullanır
 * - SecurityConfig: Authentication manager konfigürasyonunda kullanır
 * 
 * @Service: Spring'in bu sınıfı servis bean'i olarak tanımasını sağlar
 * @Transactional: Veritabanı işlemlerinin transaction içinde çalışmasını sağlar
 */
@Service
@Transactional(readOnly = true) // Sadece okuma işlemleri için optimize edilmiş
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Kullanıcı Repository - User Repository
     * 
     * Veritabanından kullanıcı bilgilerini çekmek için kullanılır
     * Used to fetch user information from database
     */
    private final UserRepository userRepository;

    /**
     * Constructor - Yapıcı Metod
     * 
     * UserRepository bağımlılığını enjekte eder
     * Injects UserRepository dependency
     * 
     * @param userRepository Kullanıcı repository'si
     */
    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Kullanıcı Adına Göre Kullanıcı Detaylarını Yükler
     * Loads User Details by Username
     * 
     * Bu metod Spring Security tarafından otomatik olarak çağrılır
     * This method is automatically called by Spring Security
     * 
     * Çağrılma durumları - When it's called:
     * - Kullanıcı giriş yaparken (During user login)
     * - JWT token doğrulanırken (During JWT token validation)
     * - @PreAuthorize gibi güvenlik anotasyonlarında (In security annotations like @PreAuthorize)
     * 
     * @param username Kullanıcı adı (genellikle e-posta adresi)
     * @return UserDetails Spring Security kullanıcı detayları
     * @throws UsernameNotFoundException Kullanıcı bulunamazsa
     * 
     * İlişkili metodlar - Related methods:
     * - JwtAuthenticationFilter.doFilterInternal(): Token doğrulamada kullanır
     * - AuthService.authenticate(): Giriş işleminde kullanır
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Debug log - geliştirme aşamasında faydalı
        // Debug log - useful during development
        System.out.println("🔍 Loading user details for: " + username);
        
        // Kullanıcıyı veritabanından bul - Find user from database
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    System.err.println("❌ User not found: " + username);
                    return new UsernameNotFoundException(
                        "Kullanıcı bulunamadı - User not found: " + username
                    );
                });
        
        // Kullanıcının aktif olup olmadığını kontrol et
        // Check if user is active
        if (!user.getIsActive()) {
            System.err.println("🚫 User account is disabled: " + username);
            throw new UsernameNotFoundException(
                "Kullanıcı hesabı devre dışı - User account is disabled: " + username
            );
        }
        
        // User entity'sini Spring Security UserDetails'e dönüştür
        // Convert User entity to Spring Security UserDetails
        UserDetails userDetails = createUserDetails(user);
        
        // Başarılı yükleme logu - Successful loading log
        System.out.println("✅ User details loaded successfully: " + username + 
                         " with authorities: " + userDetails.getAuthorities());
        
        return userDetails;
    }

    /**
     * User Entity'sinden UserDetails Oluşturur
     * Creates UserDetails from User Entity
     * 
     * Bu metod User entity'sini Spring Security'nin anlayabileceği
     * UserDetails nesnesine dönüştürür
     * This method converts User entity to UserDetails object 
     * that Spring Security can understand
     * 
     * @param user Veritabanından gelen User entity'si
     * @return Spring Security UserDetails nesnesi
     */
    private UserDetails createUserDetails(User user) {
        // Kullanıcının yetkilerini belirle - Determine user authorities
        Collection<GrantedAuthority> authorities = getAuthorities(user);
        
        // Spring Security'nin built-in User sınıfını kullan
        // Use Spring Security's built-in User class
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())           // Kullanıcı adı olarak e-posta kullan
                .password(user.getPassword())        // Hashlenmiş şifre
                .authorities(authorities)            // Kullanıcı yetkileri
                .accountExpired(false)              // Hesap süresi dolmamış
                .accountLocked(false)               // Hesap kilitli değil
                .credentialsExpired(false)          // Kimlik bilgileri süresi dolmamış
                .disabled(!user.getIsActive())      // Aktif durumu
                .build();
    }

    /**
     * Kullanıcının Yetkilerini Belirler - Determines User Authorities
     * 
     * User entity'sindeki role bilgisini Spring Security'nin
     * GrantedAuthority formatına dönüştürür
     * Converts role information in User entity to Spring Security's
     * GrantedAuthority format
     * 
     * @param user Kullanıcı entity'si
     * @return Kullanıcının yetkileri
     * 
     * Spring Security Rol Formatı - Spring Security Role Format:
     * - Roller "ROLE_" prefix'i ile başlamalı
     * - Örnek: "ROLE_USER", "ROLE_ADMIN"
     * - @PreAuthorize("hasRole('USER')") şeklinde kullanılır
     */
    private Collection<GrantedAuthority> getAuthorities(User user) {
        // Kullanıcının rolünü Spring Security formatına dönüştür
        // Convert user role to Spring Security format
        String roleName = "ROLE_" + user.getRole().name();
        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        
        // Tek elemanlı koleksiyon döndür - Return single-element collection
        return Collections.singletonList(authority);
    }

    /**
     * Kullanıcı ID'si ile Kullanıcı Detaylarını Yükler (Opsiyonel)
     * Loads User Details by User ID (Optional)
     * 
     * Bu metod JWT token'dan çıkarılan user ID ile kullanıcı yüklemek için
     * kullanılabilir (performans optimizasyonu)
     * This method can be used to load user with user ID extracted from JWT token
     * (performance optimization)
     * 
     * @param userId Kullanıcı ID'si
     * @return UserDetails Spring Security kullanıcı detayları
     * @throws UsernameNotFoundException Kullanıcı bulunamazsa
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        
        System.out.println("🔍 Loading user details by ID: " + userId);
        
        // Kullanıcıyı ID ile bul - Find user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    System.err.println("❌ User not found by ID: " + userId);
                    return new UsernameNotFoundException(
                        "Kullanıcı bulunamadı - User not found with ID: " + userId
                    );
                });
        
        // Aktiflik kontrolü - Active check
        if (!user.getIsActive()) {
            System.err.println("🚫 User account is disabled (ID: " + userId + ")");
            throw new UsernameNotFoundException(
                "Kullanıcı hesabı devre dışı - User account is disabled (ID: " + userId + ")"
            );
        }
        
        UserDetails userDetails = createUserDetails(user);
        
        System.out.println("✅ User details loaded by ID successfully: " + user.getEmail());
        
        return userDetails;
    }

    /**
     * Kullanıcı Durumunu Kontrol Eder - Checks User Status
     * 
     * Kullanıcının hesap durumlarını detaylı kontrol eder
     * Performs detailed check of user account status
     * 
     * @param user Kontrol edilecek kullanıcı
     * @return Durum bilgisi map'i
     */
    public java.util.Map<String, Boolean> checkUserStatus(User user) {
        java.util.Map<String, Boolean> statusMap = new java.util.HashMap<>();
        
        statusMap.put("isActive", user.getIsActive());
        statusMap.put("isAccountNonExpired", true); // Şu an hesap süresi yok
        statusMap.put("isAccountNonLocked", true);  // Şu an hesap kilitleme yok
        statusMap.put("isCredentialsNonExpired", true); // Şu an şifre süresi yok
        statusMap.put("isEnabled", user.getIsActive());
        
        return statusMap;
    }

    /**
     * Kullanıcı Yetkilerini String Olarak Getirir
     * Returns User Authorities as String
     * 
     * Debug ve loglama amaçlı kullanılır
     * Used for debugging and logging purposes
     * 
     * @param user Kullanıcı entity'si
     * @return Yetki string'leri
     */
    public String getUserAuthoritiesAsString(User user) {
        Collection<GrantedAuthority> authorities = getAuthorities(user);
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + ", " + b)
                .orElse("NO_AUTHORITIES");
    }

    /**
     * Kullanıcı Bilgilerini Cache'ler (Gelecekteki Optimizasyon)
     * Caches User Information (Future Optimization)
     * 
     * Performans artışı için kullanıcı bilgileri cache'lenebilir
     * User information can be cached for performance improvement
     * 
     * Cache stratejileri - Cache strategies:
     * - Redis cache kullanımı
     * - Spring Cache abstraction
     * - Manual cache implementation
     * 
     * Cache invalidation durumları - Cache invalidation cases:
     * - Kullanıcı bilgileri güncellendiğinde
     * - Kullanıcı rolü değiştiğinde
     * - Kullanıcı deaktive edildiğinde
     */

    /**
     * UserDetailsService Kullanım Senaryoları - UserDetailsService Usage Scenarios
     * 
     * 1. Login İşlemi - Login Process:
     * - Kullanıcı giriş bilgilerini gönderir
     * - AuthenticationManager bu servisi çağırır
     * - Kullanıcı bilgileri yüklenir ve şifre doğrulanır
     * - Başarılıysa JWT token oluşturulur
     * 
     * 2. JWT Token Doğrulama - JWT Token Validation:
     * - Her API isteğinde JWT token kontrol edilir
     * - Token'dan kullanıcı adı çıkarılır
     * - Bu servis kullanıcı bilgilerini yükler
     * - SecurityContext'e kullanıcı set edilir
     * 
     * 3. Yetkilendirme Kontrolü - Authorization Check:
     * - @PreAuthorize("hasRole('ADMIN')") gibi anotasyonlarda
     * - Method seviyesinde güvenlik kontrollerinde
     * - URL tabanlı erişim kontrollerinde
     * 
     * Güvenlik Notları - Security Notes:
     * - Kullanıcı şifreleri hashlenerek saklanmalı
     * - Deaktive edilmiş kullanıcılar erişim sağlayamamalı
     * - Rol değişiklikleri anında etkili olmalı
     * - Hassas bilgiler loglara yazılmamalı
     * 
     * Performans Optimizasyonları - Performance Optimizations:
     * - Kullanıcı bilgileri cache'lenebilir
     * - Database connection pool kullanılmalı
     * - N+1 query problemi önlenmeli
     * - Lazy loading dikkatli kullanılmalı
     */
}
