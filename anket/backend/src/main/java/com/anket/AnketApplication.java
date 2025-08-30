package com.anket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Ana Uygulama Sınıfı - Main Application Class
 * 
 * Bu sınıf Spring Boot uygulamasının başlangıç noktasıdır.
 * This class is the entry point of the Spring Boot application.
 * 
 * @SpringBootApplication: Spring Boot'un otomatik konfigürasyonunu etkinleştirir
 * @EnableJpaAuditing: JPA auditing özelliklerini etkinleştirir (createdDate, lastModifiedDate gibi)
 * 
 * İlişkili sınıflar - Related classes:
 * - Tüm Controller sınıfları (AuthController, SurveyController, etc.)
 * - Tüm Service sınıfları (UserService, SurveyService, etc.)
 * - Tüm Entity sınıfları (User, Survey, Question, etc.)
 */
@SpringBootApplication // Security re-enabled
@EnableJpaAuditing // Otomatik tarih/zaman damgalama için - For automatic timestamp auditing
@ComponentScan(basePackages = "com.anket") // Explicit component scanning - DEBUG
public class AnketApplication {

    /**
     * Uygulamanın başlangıç metodu - Application entry point method
     * 
     * Bu metod Spring Boot uygulamasını başlatır ve tüm konfigürasyonları yükler.
     * This method starts the Spring Boot application and loads all configurations.
     * 
     * @param args Komut satırı argümanları - Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AnketApplication.class, args);
        System.out.println("🚀 Anket Uygulaması Başlatıldı! - Survey Application Started!");
        System.out.println("📊 API Endpoint: http://localhost:8080/api");
        System.out.println("🔒 Admin Panel: http://localhost:8080/api/admin");
    }
}
