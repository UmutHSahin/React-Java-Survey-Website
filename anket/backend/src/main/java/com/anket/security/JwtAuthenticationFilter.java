package com.anket.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Kimlik Doğrulama Filtresi - JWT Authentication Filter
 * 
 * Her HTTP isteğinde JWT token'ını kontrol eden ve kimlik doğrulaması yapan filtre
 * Filter that checks JWT token and performs authentication on every HTTP request
 * 
 * Bu filtre şu işlemleri gerçekleştirir:
 * This filter performs the following operations:
 * 1. HTTP isteğinden JWT token'ını çıkarır (Extracts JWT token from HTTP request)
 * 2. Token'ın geçerliliğini kontrol eder (Validates token)
 * 3. Token'dan kullanıcı bilgilerini alır (Gets user information from token)
 * 4. Spring Security Context'ine kullanıcıyı set eder (Sets user in Spring Security Context)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - JwtTokenProvider: JWT token işlemleri için
 * - UserDetailsService: Kullanıcı bilgilerini yüklemek için
 * - SecurityConfig: Bu filtreyi güvenlik zincirinde kullanır
 * - JwtAuthenticationEntryPoint: Hata durumlarında devreye girer
 * 
 * @Component: Spring'in bu sınıfı bean olarak tanımasını sağlar
 * OncePerRequestFilter: Her istek için sadece bir kez çalışmasını garantiler
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT Token Sağlayıcı - JWT Token Provider
     * 
     * JWT token oluşturma, doğrulama ve çözümleme işlemleri için
     * For JWT token creation, validation and parsing operations
     */
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Kullanıcı Detay Servisi - User Details Service
     * 
     * Kullanıcı bilgilerini veritabanından yüklemek için
     * For loading user information from database
     */
    private final UserDetailsService userDetailsService;

    /**
     * Constructor - Yapıcı Metod
     * 
     * Gerekli bağımlılıkları enjekte eder
     * Injects required dependencies
     * 
     * @param jwtTokenProvider JWT token işlemleri için
     * @param userDetailsService Kullanıcı detay servisi
     */
    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider,
                                  UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filtre İşlemi - Filter Operation
     * 
     * Her HTTP isteğinde çalışan ana filtre metodu
     * Main filter method that runs on every HTTP request
     * 
     * Bu metod şu adımları takip eder:
     * This method follows these steps:
     * 1. Authorization header'ından JWT token'ını çıkarır
     * 2. Token'ın geçerliliğini kontrol eder
     * 3. Token'dan kullanıcı adını alır
     * 4. Kullanıcı bilgilerini yükler
     * 5. Authentication object'i oluşturur
     * 6. Security Context'e set eder
     * 
     * @param request HTTP isteği
     * @param response HTTP yanıtı
     * @param filterChain Filtre zinciri
     * @throws ServletException Servlet hatası durumunda
     * @throws IOException I/O hatası durumunda
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. JWT Token'ını isteğinden çıkar - Extract JWT token from request
            String jwt = getTokenFromRequest(request);
            
            // 2. Token varsa ve geçerliyse işle - Process if token exists and is valid
            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                
                // 3. Token'dan kullanıcı adını al - Get username from token
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                
                // 4. Kullanıcı zaten authenticate edilmemişse devam et
                // Continue if user is not already authenticated
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    
                    // 5. Kullanıcı detaylarını yükle - Load user details
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    // 6. Token kullanıcı ile eşleşiyorsa authentication oluştur
                    // Create authentication if token matches user
                    if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                        
                        // Authentication object'i oluştur - Create authentication object
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credentials (şifre gerekli değil - password not needed)
                                userDetails.getAuthorities() // yetkiler - authorities
                            );
                        
                        // İstek detaylarını ekle - Add request details
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Security Context'e set et - Set in Security Context
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // Debug log (production'da kaldırılmalı)
                        // Debug log (should be removed in production)
                        System.out.println("🔐 User authenticated: " + username + 
                                         " with authorities: " + userDetails.getAuthorities());
                    }
                }
            }
            
        } catch (Exception ex) {
            // Token işleme hatası - Token processing error
            System.err.println("❌ JWT Authentication error: " + ex.getMessage());
            
            // Security Context'i temizle - Clear Security Context
            SecurityContextHolder.clearContext();
            
            // Hata logunu detaylı yaz - Write detailed error log
            logAuthenticationError(request, ex);
        }
        
        // Filtre zincirini devam ettir - Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * İstekten JWT Token'ını Çıkarır - Extracts JWT Token from Request
     * 
     * Authorization header'ından "Bearer " prefix'ini kaldırarak token'ı alır
     * Gets token by removing "Bearer " prefix from Authorization header
     * 
     * @param request HTTP isteği
     * @return JWT token string'i (bulunamazsa null)
     * 
     * Beklenen format - Expected format:
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Authorization header'ını al - Get Authorization header
        String bearerToken = request.getHeader("Authorization");
        
        // Bearer prefix'i kontrol et ve kaldır - Check and remove Bearer prefix
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7); // "Bearer " = 7 karakter
            
            // Token'ın boş olmadığını kontrol et - Check token is not empty
            if (!token.trim().isEmpty()) {
                return token;
            }
        }
        
        // Token bulunamadı - Token not found
        return null;
    }

    /**
     * Kimlik Doğrulama Hatasını Loglar - Logs Authentication Error
     * 
     * JWT işleme hatalarını detaylı şekilde loglar
     * Logs JWT processing errors in detail
     * 
     * @param request HTTP isteği
     * @param ex Hata exception'ı
     */
    private void logAuthenticationError(HttpServletRequest request, Exception ex) {
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String authHeader = request.getHeader("Authorization");
        
        // Detaylı hata logu - Detailed error log
        String errorLog = String.format(
            "🚨 JWT Authentication Filter Error | " +
            "IP: %s | " +
            "Path: %s %s | " +
            "User-Agent: %s | " +
            "Auth Header: %s | " +
            "Error: %s",
            clientIp,
            request.getMethod(),
            request.getRequestURI(),
            userAgent != null ? (userAgent.length() > 50 ? userAgent.substring(0, 50) + "..." : userAgent) : "N/A",
            authHeader != null ? "Bearer ***" : "None",
            ex.getMessage()
        );
        
        System.err.println(errorLog);
        
        // Stack trace'i sadece debug modunda göster
        // Show stack trace only in debug mode
        if (isDebugMode()) {
            ex.printStackTrace();
        }
    }

    /**
     * İstemci IP Adresini Getirir - Gets Client IP Address
     * 
     * @param request HTTP isteği
     * @return İstemci IP adresi
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Debug Modunu Kontrol Eder - Checks Debug Mode
     * 
     * @return Debug modu aktif mi
     */
    private boolean isDebugMode() {
        // Sistem property'si veya environment variable kontrolü yapılabilir
        // System property or environment variable check can be done
        String debugMode = System.getProperty("jwt.debug", "false");
        return "true".equalsIgnoreCase(debugMode);
    }

    /**
     * Belirli URL'ler için filtreyi atla - Skip filter for specific URLs
     * 
     * Bu metod override edilerek belirli endpoint'ler için filtre atlanabilir
     * This method can be overridden to skip filter for specific endpoints
     * 
     * @param request HTTP isteği
     * @return true: filtreyi atla, false: filtreyi çalıştır
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Public endpoint'ler için filtreyi atla - Skip filter for public endpoints
        return path.startsWith("/api/auth/") || 
               path.startsWith("/api/public/") ||
               path.equals("/api/health") ||
               path.startsWith("/swagger-") ||
               path.startsWith("/v3/api-docs") ||
               path.equals("/favicon.ico");
    }

    /**
     * Filtre Çalışma Mantığı - Filter Working Logic
     * 
     * Bu filtre şu sırayla çalışır:
     * This filter works in the following order:
     * 
     * 1. shouldNotFilter() kontrolü - Public endpoint'ler için filtre atlanır
     * 2. doFilterInternal() çalışır - Ana filtre mantığı
     * 3. JWT token çıkarılır ve doğrulanır
     * 4. Kullanıcı bilgileri yüklenir
     * 5. Authentication object'i oluşturulur
     * 6. Security Context'e set edilir
     * 7. Filtre zinciri devam eder
     * 
     * Hata Durumları - Error Cases:
     * - Token yoksa: Filtre sessizce geçer, Security Context boş kalır
     * - Token geçersizse: Hata loglanır, Security Context temizlenir
     * - Kullanıcı bulunamazsa: Exception fırlatılır, hata loglanır
     * 
     * Güvenlik Notları - Security Notes:
     * - Token her istekte doğrulanır (stateless)
     * - Hassas bilgiler loglara yazılmaz
     * - Hata durumlarında Security Context temizlenir
     * - Rate limiting ve brute force koruması eklenebilir
     * 
     * Performans Optimizasyonları - Performance Optimizations:
     * - Token cache'i eklenebilir (Redis)
     * - Kullanıcı bilgileri cache'lenebilir
     * - Async processing kullanılabilir
     */
}
