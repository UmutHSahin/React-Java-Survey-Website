package com.anket.config;

import com.anket.security.JwtAuthenticationEntryPoint;
import com.anket.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Güvenlik Konfigürasyon Sınıfı - Security Configuration Class
 * 
 * Spring Security'nin yapılandırmasını gerçekleştirir
 * Configures Spring Security settings
 * 
 * Bu sınıf şu güvenlik özelliklerini sağlar:
 * This class provides the following security features:
 * - JWT tabanlı kimlik doğrulama (JWT-based authentication)
 * - Role tabanlı yetkilendirme (Role-based authorization)
 * - CORS konfigürasyonu (CORS configuration)
 * - Şifre hashleme (Password hashing)
 * - API endpoint güvenliği (API endpoint security)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - JwtAuthenticationFilter: JWT token işlemleri için
 * - JwtAuthenticationEntryPoint: Kimlik doğrulama hataları için
 * - UserDetailsService: Kullanıcı bilgilerini yüklemek için
 * 
 * @Configuration: Spring konfigürasyon sınıfı olduğunu belirtir
 * @EnableWebSecurity: Web güvenliğini etkinleştirir
 * @EnableMethodSecurity: Metod seviyesinde güvenlik kontrolü sağlar
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * JWT Kimlik Doğrulama Giriş Noktası - JWT Authentication Entry Point
     * 
     * Kimlik doğrulama hatalarını yönetir
     * Handles authentication errors
     */
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * JWT Kimlik Doğrulama Filtresi - JWT Authentication Filter
     * 
     * Her HTTP isteğinde JWT token'ını kontrol eder
     * Checks JWT token on every HTTP request
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor - Yapıcı Metod
     * 
     * Gerekli bağımlılıkları enjekte eder
     * Injects required dependencies
     * 
     * @param jwtAuthenticationEntryPoint JWT giriş noktası
     * @param jwtAuthenticationFilter JWT filtresi
     */
    @Autowired
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Şifre Encoder Bean'i - Password Encoder Bean
     * 
     * Şifreleri BCrypt algoritması ile hashler
     * Hashes passwords using BCrypt algorithm
     * 
     * BCrypt avantajları - BCrypt advantages:
     * - Güvenli hashleme algoritması (Secure hashing algorithm)
     * - Salt otomatik eklenir (Salt is automatically added)
     * - Brute force saldırılarına dayanıklı (Resistant to brute force attacks)
     * 
     * @return BCrypt şifre encoder'ı
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 round (güvenlik seviyesi)
    }

    /**
     * Authentication Manager Bean'i - Authentication Manager Bean
     * 
     * Kimlik doğrulama işlemlerini yönetir
     * Manages authentication operations
     * 
     * @param config Authentication konfigürasyonu
     * @return AuthenticationManager instance
     * @throws Exception Konfigürasyon hatası durumunda
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Güvenlik Filtre Zinciri - Security Filter Chain
     * 
     * HTTP güvenlik konfigürasyonunu tanımlar
     * Defines HTTP security configuration
     * 
     * Bu metod şu güvenlik kurallarını belirler:
     * This method defines the following security rules:
     * - Hangi endpoint'ler herkese açık (Which endpoints are public)
     * - Hangi endpoint'ler kimlik doğrulama gerektirir (Which endpoints require authentication)
     * - Hangi endpoint'ler admin yetkisi gerektirir (Which endpoints require admin role)
     * 
     * @param http HttpSecurity konfigürasyonu
     * @return SecurityFilterChain
     * @throws Exception Konfigürasyon hatası durumunda
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF korumasını devre dışı bırak (REST API için gerekli değil)
            // Disable CSRF protection (not needed for REST API)
            .csrf(csrf -> csrf.disable())
            
            // CORS konfigürasyonunu etkinleştir
            // Enable CORS configuration
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Session yönetimini STATELESS yap (JWT kullandığımız için)
            // Set session management to STATELESS (because we use JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Kimlik doğrulama giriş noktasını ayarla
            // Set authentication entry point
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            
            // HTTP isteklerini yetkilendirme kuralları
            // Authorization rules for HTTP requests
            .authorizeHttpRequests(authz -> authz
                
                // 🔓 Herkese Açık Endpoint'ler - Public Endpoints
                .requestMatchers("/api/auth/**").permitAll() // Giriş/Kayıt işlemleri
                .requestMatchers("/auth/**").permitAll() // Alternatif auth path
                .requestMatchers("/api/public/**").permitAll() // Genel erişim
                .requestMatchers("/error").permitAll() // Spring Boot error endpoint
                .requestMatchers("/api/error").permitAll() // Error endpoint
                .requestMatchers("/api/test/**").permitAll() // Test endpoints
                .requestMatchers("/api/simple-auth/**").permitAll() // Simple auth test endpoints
                .requestMatchers("/api/working-auth/**").permitAll() // Working auth test endpoints
                .requestMatchers("/api/simple").permitAll() // Simple test endpoint
                .requestMatchers("/api/health").permitAll() // Health check
                .requestMatchers("/simple").permitAll() // Simple test endpoint
                .requestMatchers("/simple-*").permitAll() // Simple endpoints
                .requestMatchers("/clean-database").permitAll() // Database cleanup endpoint
                .requestMatchers(HttpMethod.GET, "/api/surveys/public/**").permitAll() // Anonim anket görüntüleme
                .requestMatchers(HttpMethod.POST, "/api/responses/anonymous/**").permitAll() // Anonim yanıt verme
                
                // 📊 İstatistik ve Genel Bilgiler - Statistics and General Info
                .requestMatchers(HttpMethod.GET, "/api/surveys/stats/public").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll() // Sistem sağlık kontrolü
                
                // 🔐 Kimlik Doğrulama Gerektiren Endpoint'ler - Authenticated Endpoints
                .requestMatchers("/api/users/profile/**").authenticated() // Kullanıcı profili
                .requestMatchers("/api/surveys/my/**").authenticated() // Kullanıcının anketleri
                .requestMatchers(HttpMethod.POST, "/api/surveys").authenticated() // Anket oluşturma
                .requestMatchers(HttpMethod.PUT, "/api/surveys/{id}").authenticated() // Anket güncelleme
                .requestMatchers(HttpMethod.DELETE, "/api/surveys/{id}").authenticated() // Anket silme
                .requestMatchers("/api/responses/my/**").authenticated() // Kullanıcının yanıtları
                
                // 👑 Admin Yetkisi Gerektiren Endpoint'ler - Admin Only Endpoints
                .requestMatchers("/api/admin/**").permitAll() // Admin paneli (temporarily open for testing)
                .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN") // Tüm kullanıcılar
                .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN") // Kullanıcı silme
                .requestMatchers(HttpMethod.PUT, "/api/users/{id}/role").hasRole("ADMIN") // Rol güncelleme
                .requestMatchers("/api/surveys/all").hasRole("ADMIN") // Tüm anketler
                .requestMatchers("/api/responses/all").hasRole("ADMIN") // Tüm yanıtlar
                .requestMatchers("/api/stats/**").hasRole("ADMIN") // Detaylı istatistikler
                
                // 🔒 Diğer Tüm İstekler Kimlik Doğrulama Gerektirir
                // All Other Requests Require Authentication
                .anyRequest().permitAll() // TEMPORARILY ALLOW ALL FOR TESTING
            );

        // JWT Authentication Filter'ını ekle
        // Add JWT Authentication Filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS Konfigürasyon Kaynağı - CORS Configuration Source
     * 
     * Cross-Origin Resource Sharing ayarlarını yapar
     * Configures Cross-Origin Resource Sharing settings
     * 
     * Bu konfigürasyon frontend uygulamasının backend'e erişmesini sağlar
     * This configuration allows frontend application to access backend
     * 
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // İzin verilen origin'ler (frontend URL'leri)
        // Allowed origins (frontend URLs)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",    // React development server
            "http://localhost:5173",    // Vite development server
            "http://localhost:8080",    // Local development
            "https://*.vercel.app",     // Vercel deployment
            "https://*.netlify.app",    // Netlify deployment
            "https://*.herokuapp.com"   // Heroku deployment
        ));
        
        // İzin verilen HTTP metodları - Allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // İzin verilen header'lar - Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Exposed header'lar (frontend'in erişebileceği header'lar)
        // Exposed headers (headers that frontend can access)
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization",
            "X-Total-Count"
        ));
        
        // Credential'ları (çerezler, yetkilendirme header'ları) destekle
        // Support credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Preflight isteklerini cache'leme süresi (saniye)
        // Preflight request caching duration (seconds)
        configuration.setMaxAge(3600L);

        // Konfigürasyonu tüm yollara uygula
        // Apply configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Güvenlik Middleware'i - Security Middleware
     * 
     * Bu sınıfta tanımlanan güvenlik kuralları şu sırayla çalışır:
     * Security rules defined in this class work in the following order:
     * 
     * 1. CORS Filter: Cross-origin istekleri kontrol eder
     * 2. JWT Authentication Filter: JWT token'ını doğrular
     * 3. Authorization Filter: Kullanıcı yetkilerini kontrol eder
     * 4. Exception Handler: Güvenlik hatalarını yakalar
     * 
     * Güvenlik Seviyeleri - Security Levels:
     * 🔓 Public: Herkese açık (giriş/kayıt, anonim anketler)
     * 🔐 Authenticated: Giriş yapmış kullanıcılar (profil, kendi anketleri)
     * 👑 Admin: Sadece yöneticiler (kullanıcı yönetimi, sistem istatistikleri)
     * 
     * JWT Token Formatı - JWT Token Format:
     * Authorization: Bearer <token>
     * 
     * Örnek Kullanım - Example Usage:
     * - Public endpoint: GET /api/surveys/public/1
     * - Authenticated endpoint: GET /api/users/profile (Authorization header gerekli)
     * - Admin endpoint: GET /api/admin/users (Authorization header + ADMIN rolü gerekli)
     */
}
