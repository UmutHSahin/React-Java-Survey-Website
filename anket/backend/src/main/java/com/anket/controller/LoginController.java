package com.anket.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Login Controller - Giriş Controller'ı
 * 
 * Frontend login işlemleri için basit endpoint'ler
 * Simple endpoints for frontend login operations
 */
@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class LoginController {

    /**
     * Login endpoint
     * Giriş endpoint'i
     */
    @PostMapping
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            
            System.out.println("🔐 Login attempt: " + email);
            
            // Simple validation (replace with real authentication)
            // Basit doğrulama (gerçek kimlik doğrulama ile değiştirin)
            if (email != null && password != null && password.length() >= 6) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("token", "demo-jwt-token-" + System.currentTimeMillis());
                response.put("tokenType", "Bearer");
                response.put("user", Map.of(
                    "id", 1,
                    "firstName", "Demo",
                    "lastName", "User",
                    "email", email,
                    "role", "USER"
                ));
                response.put("timestamp", LocalDateTime.now());
                
                System.out.println("✅ Login successful for: " + email);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid credentials");
                errorResponse.put("message", "Invalid email or password");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                System.out.println("❌ Login failed for: " + email);
                return ResponseEntity.status(401).body(errorResponse);
            }
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Login failed");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Register endpoint
     * Kayıt endpoint'i
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            String firstName = (String) request.get("firstName");
            String lastName = (String) request.get("lastName");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String confirmPassword = (String) request.get("confirmPassword");
            
            System.out.println("📝 Registration attempt: " + email);
            
            // Simple validation
            // Basit doğrulama
            if (firstName == null || lastName == null || email == null || 
                password == null || confirmPassword == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing required fields");
                errorResponse.put("message", "All fields are required");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (!password.equals(confirmPassword)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Password mismatch");
                errorResponse.put("message", "Passwords do not match");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (password.length() < 6) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Password too short");
                errorResponse.put("message", "Password must be at least 6 characters");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Success response (simulate user creation)
            // Başarı yanıtı (kullanıcı oluşturma simülasyonu)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("token", "demo-jwt-token-" + System.currentTimeMillis());
            response.put("tokenType", "Bearer");
            response.put("user", Map.of(
                "id", System.currentTimeMillis() / 1000,
                "firstName", firstName,
                "lastName", lastName,
                "email", email,
                "role", "USER"
            ));
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Registration successful for: " + email);
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Registration failed");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login API is working! 🎉");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}

