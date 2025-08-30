package com.anket.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Kimlik Doğrulama Giriş Noktası - JWT Authentication Entry Point
 * 
 * Spring Security'de kimlik doğrulama başarısız olduğunda çalışan sınıf
 * Class that runs when authentication fails in Spring Security
 * 
 * Bu sınıf şu durumlarda devreye girer:
 * This class is triggered in the following situations:
 * - Geçersiz JWT token (Invalid JWT token)
 * - Süresi dolmuş JWT token (Expired JWT token)
 * - JWT token eksik (Missing JWT token)
 * - Yetkisiz erişim denemesi (Unauthorized access attempt)
 * 
 * İlişkili Sınıflar - Related Classes:
 * - SecurityConfig: Bu sınıfı exception handler olarak kullanır
 * - JwtAuthenticationFilter: Token doğrulama hatalarında bu sınıfa yönlendirir
 * - JwtTokenProvider: Token geçerlilik kontrolünde hata durumunda çalışır
 * 
 * @Component: Spring'in bu sınıfı bean olarak tanımasını sağlar
 * AuthenticationEntryPoint: Spring Security'nin kimlik doğrulama giriş noktası arayüzü
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * JSON dönüştürücü - JSON converter
     * 
     * Hata yanıtlarını JSON formatına çevirmek için kullanılır
     * Used to convert error responses to JSON format
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Kimlik Doğrulama Başarısız Olduğunda Çalışan Metod
     * Method That Runs When Authentication Fails
     * 
     * Bu metod kimlik doğrulama hatalarını yakalar ve uygun HTTP yanıtı döndürür
     * This method catches authentication errors and returns appropriate HTTP response
     * 
     * @param request HTTP isteği - HTTP request
     * @param response HTTP yanıtı - HTTP response
     * @param authException Kimlik doğrulama hatası - Authentication exception
     * @throws IOException I/O hatası durumunda
     * @throws ServletException Servlet hatası durumunda
     * 
     * İlişkili metodlar - Related methods:
     * - JwtAuthenticationFilter.doFilterInternal(): Token doğrulama hatalarında çağırır
     * - SecurityConfig.filterChain(): Exception handler olarak bu sınıfı kullanır
     */
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        // HTTP yanıt kodunu 401 (Unauthorized) olarak ayarla
        // Set HTTP response code to 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Content-Type'ı JSON olarak ayarla
        // Set Content-Type as JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Hata türünü belirle - Determine error type
        String errorType = determineErrorType(request, authException);
        String errorMessage = getLocalizedErrorMessage(errorType);
        
        // Hata yanıtı oluştur - Create error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("errorType", errorType);
        errorResponse.put("message", errorMessage);
        errorResponse.put("path", request.getRequestURI());
        
        // İsteğe ait ek bilgileri ekle - Add additional request information
        errorResponse.put("method", request.getMethod());
        
        // IP adresini ekle (güvenlik logları için) - Add IP address (for security logs)
        String clientIp = getClientIpAddress(request);
        errorResponse.put("clientIp", clientIp);
        
        // User-Agent bilgisini ekle - Add User-Agent information
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            errorResponse.put("userAgent", userAgent.length() > 100 ? 
                userAgent.substring(0, 100) + "..." : userAgent);
        }
        
        // Güvenlik logunu yaz - Write security log
        logSecurityEvent(request, authException, errorType, clientIp);
        
        // JSON yanıtı gönder - Send JSON response
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Hata Türünü Belirler - Determines Error Type
     * 
     * İstek ve exception bilgilerine göre hata türünü tespit eder
     * Determines error type based on request and exception information
     * 
     * @param request HTTP isteği
     * @param authException Kimlik doğrulama hatası
     * @return Hata türü string'i
     */
    private String determineErrorType(HttpServletRequest request, AuthenticationException authException) {
        // Authorization header kontrolü - Authorization header check
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return "MISSING_TOKEN";
        }
        
        // Exception mesajına göre hata türü belirleme
        // Determine error type based on exception message
        String exceptionMessage = authException.getMessage().toLowerCase();
        
        if (exceptionMessage.contains("expired")) {
            return "EXPIRED_TOKEN";
        } else if (exceptionMessage.contains("invalid")) {
            return "INVALID_TOKEN";
        } else if (exceptionMessage.contains("malformed")) {
            return "MALFORMED_TOKEN";
        } else if (exceptionMessage.contains("signature")) {
            return "INVALID_SIGNATURE";
        } else if (exceptionMessage.contains("unsupported")) {
            return "UNSUPPORTED_TOKEN";
        } else if (exceptionMessage.contains("access denied")) {
            return "ACCESS_DENIED";
        } else {
            return "AUTHENTICATION_FAILED";
        }
    }

    /**
     * Yerelleştirilmiş Hata Mesajı Getirir - Returns Localized Error Message
     * 
     * Hata türüne göre Türkçe ve İngilizce hata mesajı döndürür
     * Returns Turkish and English error message based on error type
     * 
     * @param errorType Hata türü
     * @return Yerelleştirilmiş hata mesajı
     */
    private String getLocalizedErrorMessage(String errorType) {
        switch (errorType) {
            case "MISSING_TOKEN":
                return "Erişim token'ı eksik. Lütfen giriş yapın. - Access token is missing. Please login.";
            
            case "EXPIRED_TOKEN":
                return "Erişim token'ınızın süresi dolmuş. Lütfen tekrar giriş yapın. - Your access token has expired. Please login again.";
            
            case "INVALID_TOKEN":
                return "Geçersiz erişim token'ı. Lütfen tekrar giriş yapın. - Invalid access token. Please login again.";
            
            case "MALFORMED_TOKEN":
                return "Hatalı biçimlendirilmiş token. Lütfen tekrar giriş yapın. - Malformed token. Please login again.";
            
            case "INVALID_SIGNATURE":
                return "Token imzası geçersiz. Güvenlik nedeniyle erişim reddedildi. - Token signature is invalid. Access denied for security reasons.";
            
            case "UNSUPPORTED_TOKEN":
                return "Desteklenmeyen token formatı. - Unsupported token format.";
            
            case "ACCESS_DENIED":
                return "Bu işlem için yetkiniz bulunmuyor. - You don't have permission for this operation.";
            
            case "AUTHENTICATION_FAILED":
            default:
                return "Kimlik doğrulama başarısız. Lütfen giriş bilgilerinizi kontrol edin. - Authentication failed. Please check your credentials.";
        }
    }

    /**
     * İstemci IP Adresini Getirir - Gets Client IP Address
     * 
     * Proxy ve load balancer arkasındaki gerçek IP adresini bulur
     * Finds real IP address behind proxy and load balancer
     * 
     * @param request HTTP isteği
     * @return İstemci IP adresi
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // Proxy header'larını kontrol et - Check proxy headers
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // Birden fazla IP varsa ilkini al - Take first IP if multiple exist
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }
        
        // Hiçbir proxy header'ı bulunamazsa remote address'i kullan
        // Use remote address if no proxy headers found
        return request.getRemoteAddr();
    }

    /**
     * Güvenlik Olayını Loglar - Logs Security Event
     * 
     * Kimlik doğrulama hatalarını güvenlik loguna kaydeder
     * Records authentication errors to security log
     * 
     * @param request HTTP isteği
     * @param authException Kimlik doğrulama hatası
     * @param errorType Hata türü
     * @param clientIp İstemci IP adresi
     */
    private void logSecurityEvent(HttpServletRequest request, 
                                 AuthenticationException authException,
                                 String errorType, 
                                 String clientIp) {
        
        // Güvenlik log formatı - Security log format
        String logMessage = String.format(
            "🔒 SECURITY EVENT - Unauthorized Access Attempt | " +
            "Type: %s | " +
            "IP: %s | " +
            "Path: %s %s | " +
            "User-Agent: %s | " +
            "Error: %s",
            errorType,
            clientIp,
            request.getMethod(),
            request.getRequestURI(),
            request.getHeader("User-Agent"),
            authException.getMessage()
        );
        
        // Konsola yazdır (production'da logger kullanılmalı)
        // Print to console (logger should be used in production)
        System.err.println(logMessage);
        
        // TODO: Production'da aşağıdaki gibi bir logger kullanılmalı
        // TODO: In production, a logger like below should be used
        // logger.warn("Security event: {}", logMessage);
        
        // Kritik güvenlik olayları için ek işlemler yapılabilir
        // Additional actions can be taken for critical security events
        if ("INVALID_SIGNATURE".equals(errorType) || "MALFORMED_TOKEN".equals(errorType)) {
            // Şüpheli aktivite - Suspicious activity
            System.err.println("⚠️ CRITICAL SECURITY ALERT - Potential security breach attempt from IP: " + clientIp);
            
            // TODO: Production'da:
            // TODO: In production:
            // - Email/SMS uyarısı gönder (Send email/SMS alert)
            // - IP'yi geçici olarak engelle (Temporarily block IP)
            // - Güvenlik ekibine bildirim gönder (Notify security team)
        }
    }

    /**
     * Hata Yanıt Formatı - Error Response Format
     * 
     * Bu sınıfın döndürdüğü JSON yanıt formatı:
     * JSON response format returned by this class:
     * 
     * {
     *   "timestamp": "2024-01-15T10:30:45",
     *   "status": 401,
     *   "error": "Unauthorized",
     *   "errorType": "EXPIRED_TOKEN",
     *   "message": "Erişim token'ınızın süresi dolmuş...",
     *   "path": "/api/users/profile",
     *   "method": "GET",
     *   "clientIp": "192.168.1.100",
     *   "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)..."
     * }
     * 
     * Frontend'de Kullanım - Usage in Frontend:
     * 
     * axios.interceptors.response.use(
     *   response => response,
     *   error => {
     *     if (error.response?.status === 401) {
     *       const errorType = error.response.data?.errorType;
     *       
     *       if (errorType === 'EXPIRED_TOKEN') {
     *         // Token yenileme işlemi
     *         refreshToken();
     *       } else if (errorType === 'MISSING_TOKEN' || errorType === 'INVALID_TOKEN') {
     *         // Login sayfasına yönlendir
     *         redirectToLogin();
     *       }
     *     }
     *     return Promise.reject(error);
     *   }
     * );
     */
}
