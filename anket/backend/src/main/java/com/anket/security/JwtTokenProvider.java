package com.anket.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Token Sağlayıcı - JWT Token Provider
 * 
 * JWT (JSON Web Token) oluşturma, doğrulama ve çözümleme işlemlerini gerçekleştirir
 * Performs JWT (JSON Web Token) creation, validation and parsing operations
 * 
 * Bu sınıf şu işlevleri sağlar:
 * This class provides the following functions:
 * - JWT token oluşturma (JWT token creation)
 * - JWT token doğrulama (JWT token validation)
 * - Token'dan bilgi çıkarma (Information extraction from token)
 * - Token süre kontrolü (Token expiration check)
 * 
 * JWT Token Yapısı - JWT Token Structure:
 * Header.Payload.Signature
 * 
 * Payload içeriği - Payload content:
 * - sub (subject): Kullanıcı adı/email
 * - iat (issued at): Oluşturulma zamanı
 * - exp (expiration): Son kullanma zamanı
 * - rol: Kullanıcı rolü (custom claim)
 * - uid: Kullanıcı ID'si (custom claim)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - JwtAuthenticationFilter: Token doğrulama için kullanır
 * - AuthService: Giriş sonrası token oluşturmak için kullanır
 * - SecurityConfig: JWT tabanlı güvenlik için gerekli
 * 
 * @Component: Spring'in bu sınıfı bean olarak tanımasını sağlar
 */
@Component
public class JwtTokenProvider {

    /**
     * JWT Gizli Anahtar - JWT Secret Key
     * 
     * application.yml'den okunur, token imzalama için kullanılır
     * Read from application.yml, used for token signing
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT Token Geçerlilik Süresi - JWT Token Expiration Time
     * 
     * application.yml'den okunur, milisaniye cinsinden
     * Read from application.yml, in milliseconds
     */
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Gizli Anahtar Cache - Secret Key Cache
     * 
     * SecretKey nesnesini cache'ler, performans için
     * Caches SecretKey object for performance
     */
    private SecretKey secretKey;

    /**
     * Gizli Anahtar Getirir - Gets Secret Key
     * 
     * String'den SecretKey nesnesini oluşturur ve cache'ler
     * Creates SecretKey object from string and caches it
     * 
     * @return HMAC SHA-256 için gizli anahtar
     */
    private SecretKey getSigningKey() {
        if (secretKey == null) {
            // HMAC SHA-256 için güvenli anahtar oluştur
            // Create secure key for HMAC SHA-256
            secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
        return secretKey;
    }

    // JWT Token Oluşturma Metodları - JWT Token Creation Methods

    /**
     * Kullanıcı Detayları ile Token Oluşturur - Creates Token with User Details
     * 
     * UserDetails nesnesinden JWT token oluşturur
     * Creates JWT token from UserDetails object
     * 
     * @param userDetails Spring Security UserDetails
     * @return JWT token string'i
     * 
     * İlişkili metodlar - Related methods:
     * - AuthService.authenticate(): Giriş sonrası token oluşturmak için
     * - AuthController.login(): Login endpoint'inde kullanır
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Custom claim'ler ekle - Add custom claims
        claims.put("rol", userDetails.getAuthorities().iterator().next().getAuthority());
        
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Kullanıcı Adı ile Token Oluşturur - Creates Token with Username
     * 
     * Sadece kullanıcı adı ile basit token oluşturur
     * Creates simple token with just username
     * 
     * @param username Kullanıcı adı
     * @return JWT token string'i
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Kullanıcı Bilgileri ile Detaylı Token Oluşturur
     * Creates Detailed Token with User Information
     * 
     * @param username Kullanıcı adı
     * @param userId Kullanıcı ID'si
     * @param role Kullanıcı rolü
     * @return JWT token string'i
     */
    public String generateToken(String username, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        
        // Custom claim'ler ekle - Add custom claims
        claims.put("uid", userId); // User ID
        claims.put("rol", role);   // User Role
        claims.put("iss", "anket-app"); // Issuer (token'ı veren uygulama)
        
        return createToken(claims, username);
    }

    /**
     * Token Oluşturma Ana Metodu - Main Token Creation Method
     * 
     * Verilen claim'ler ve subject ile JWT token oluşturur
     * Creates JWT token with given claims and subject
     * 
     * @param claims Token'a eklenecek ek bilgiler
     * @param subject Token'ın subject'i (genellikle kullanıcı adı)
     * @return JWT token string'i
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)              // Custom claim'ler
                .setSubject(subject)            // Subject (kullanıcı adı)
                .setIssuedAt(now)              // Oluşturulma zamanı
                .setExpiration(expiryDate)      // Son kullanma zamanı
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // İmza
                .compact();
    }

    // JWT Token Çözümleme Metodları - JWT Token Parsing Methods

    /**
     * Token'dan Kullanıcı Adını Çıkarır - Extracts Username from Token
     * 
     * @param token JWT token
     * @return Kullanıcı adı (subject)
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Token'dan Son Kullanma Tarihini Çıkarır - Extracts Expiration Date from Token
     * 
     * @param token JWT token
     * @return Son kullanma tarihi
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Token'dan Kullanıcı ID'sini Çıkarır - Extracts User ID from Token
     * 
     * @param token JWT token
     * @return Kullanıcı ID'si
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        Object uid = claims.get("uid");
        
        if (uid instanceof Number) {
            return ((Number) uid).longValue();
        }
        return null;
    }

    /**
     * Token'dan Kullanıcı Rolünü Çıkarır - Extracts User Role from Token
     * 
     * @param token JWT token
     * @return Kullanıcı rolü
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (String) claims.get("rol");
    }

    /**
     * Token'dan Belirli Claim'i Çıkarır - Extracts Specific Claim from Token
     * 
     * @param token JWT token
     * @param claimsResolver Claim çözümleyici fonksiyon
     * @param <T> Claim tipi
     * @return Claim değeri
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Token'dan Tüm Claim'leri Çıkarır - Extracts All Claims from Token
     * 
     * @param token JWT token
     * @return Tüm claim'ler
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.err.println("❌ Error parsing JWT token: " + e.getMessage());
            throw e;
        }
    }

    // JWT Token Doğrulama Metodları - JWT Token Validation Methods

    /**
     * Token'ın Geçerliliğini Kontrol Eder - Validates Token
     * 
     * @param token JWT token
     * @return true: geçerli, false: geçersiz
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
            
        } catch (SecurityException ex) {
            System.err.println("❌ Invalid JWT signature: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.err.println("❌ Invalid JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            System.err.println("❌ Expired JWT token: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.err.println("❌ Unsupported JWT token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("❌ JWT claims string is empty: " + ex.getMessage());
        }
        
        return false;
    }

    /**
     * Token'ı Kullanıcı ile Doğrular - Validates Token with User
     * 
     * Token'daki kullanıcı adının verilen UserDetails ile eşleşip eşleşmediğini kontrol eder
     * Checks if username in token matches given UserDetails
     * 
     * @param token JWT token
     * @param userDetails Kullanıcı detayları
     * @return true: eşleşiyor ve geçerli, false: eşleşmiyor veya geçersiz
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            System.err.println("❌ Error validating token with user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Token'ın Süresinin Dolup Dolmadığını Kontrol Eder
     * Checks if Token is Expired
     * 
     * @param token JWT token
     * @return true: süresi dolmuş, false: hala geçerli
     */
    private boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            // Token parse edilemiyorsa süresi dolmuş kabul et
            // Consider expired if token cannot be parsed
            return true;
        }
    }

    // Token Yenileme ve Yardımcı Metodlar - Token Refresh and Helper Methods

    /**
     * Token'ın Yenilenebilir Olup Olmadığını Kontrol Eder
     * Checks if Token can be Refreshed
     * 
     * @param token JWT token
     * @return true: yenilenebilir, false: yenilenemez
     */
    public boolean canTokenBeRefreshed(String token) {
        try {
            // Token geçerli ama süresi yakında dolacaksa yenilenebilir
            // Token is valid but will expire soon, can be refreshed
            if (!validateToken(token)) {
                return false;
            }
            
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            
            // Son 1 saat içinde yenilenebilir (1 saat = 3600000 ms)
            // Can be refreshed within last 1 hour
            long timeUntilExpiration = expiration.getTime() - now.getTime();
            return timeUntilExpiration < 3600000; // 1 hour in milliseconds
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Token'ı Yeniler - Refreshes Token
     * 
     * Mevcut token'dan yeni bir token oluşturur
     * Creates new token from existing token
     * 
     * @param token Eski JWT token
     * @return Yeni JWT token
     */
    public String refreshToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String username = claims.getSubject();
            
            // Yeni token oluştur (aynı claim'lerle)
            // Create new token (with same claims)
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("uid", claims.get("uid"));
            newClaims.put("rol", claims.get("rol"));
            newClaims.put("iss", claims.get("iss"));
            
            return createToken(newClaims, username);
            
        } catch (Exception e) {
            System.err.println("❌ Error refreshing token: " + e.getMessage());
            throw new RuntimeException("Token yenilenemedi - Cannot refresh token");
        }
    }

    /**
     * Token'dan Özet Bilgi Çıkarır - Extracts Summary from Token
     * 
     * Debug ve loglama amaçlı token özet bilgisi
     * Token summary information for debugging and logging
     * 
     * @param token JWT token
     * @return Token özet bilgisi
     */
    public String getTokenSummary(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            String username = claims.getSubject();
            String role = (String) claims.get("rol");
            Date expiration = claims.getExpiration();
            
            return String.format("User: %s, Role: %s, Expires: %s", 
                               username, role, expiration.toString());
        } catch (Exception e) {
            return "Invalid token";
        }
    }

    /**
     * Token'ın Kalan Süresini Getirir - Gets Remaining Time of Token
     * 
     * @param token JWT token
     * @return Kalan süre (milisaniye)
     */
    public long getRemainingTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            return Math.max(0, expiration.getTime() - now.getTime());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * JWT Token Kullanım Örnekleri - JWT Token Usage Examples
     * 
     * Token Oluşturma - Token Creation:
     * String token = jwtTokenProvider.generateToken("user@example.com", 1L, "USER");
     * 
     * Token Doğrulama - Token Validation:
     * if (jwtTokenProvider.validateToken(token)) {
     *     String username = jwtTokenProvider.getUsernameFromToken(token);
     *     String role = jwtTokenProvider.getRoleFromToken(token);
     * }
     * 
     * Token Yenileme - Token Refresh:
     * if (jwtTokenProvider.canTokenBeRefreshed(oldToken)) {
     *     String newToken = jwtTokenProvider.refreshToken(oldToken);
     * }
     * 
     * Frontend Kullanımı - Frontend Usage:
     * 
     * // Token'ı localStorage'a kaydet
     * localStorage.setItem('token', token);
     * 
     * // API isteklerinde kullan
     * axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
     * 
     * // Token süresini kontrol et
     * const payload = JSON.parse(atob(token.split('.')[1]));
     * const isExpired = payload.exp * 1000 < Date.now();
     * 
     * Güvenlik Notları - Security Notes:
     * - Token'lar HTTPS üzerinden gönderilmeli
     * - LocalStorage yerine httpOnly cookie kullanımı daha güvenli
     * - Token'lara hassas bilgiler konulmamalı
     * - Logout işleminde token'lar blacklist'e alınmalı
     * - Secret key düzenli olarak değiştirilmeli
     */
}
