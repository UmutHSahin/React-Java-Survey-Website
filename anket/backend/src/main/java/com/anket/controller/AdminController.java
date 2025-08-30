package com.anket.controller;

import com.anket.entity.Survey;
import com.anket.service.SurveyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Controller Sınıfı - Admin Controller Class
 * 
 * Admin paneli için REST API endpoint'lerini sağlar
 * Provides REST API endpoints for admin panel
 * 
 * Bu controller sadece admin yetkisine sahip kullanıcılar tarafından kullanılmalıdır
 * This controller should only be used by users with admin privileges
 * 
 * İlişkili Sınıflar - Related Classes:
 * - SurveyService: Anket işlemleri için
 * - UserService: Kullanıcı işlemleri için
 * - SecurityConfig: Yetkilendirme kuralları
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    /**
     * Anket Servisi - Survey Service
     * Anket işlemleri için kullanılır
     */
    @Autowired
    private SurveyService surveyService;

    /**
     * Admin paneli durumu kontrolü
     * Admin panel health check
     * 
     * @return Admin paneli durumu
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> adminHealth() {
        logger.info("Admin paneli durumu kontrol ediliyor - Checking admin panel health");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Admin paneli aktif - Admin panel is active");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    // Anket Temizlik Endpoint'leri - Survey Cleanup Endpoints

    /**
     * Yetim anketleri listeler
     * Lists orphaned surveys
     * 
     * @return Yetim anketlerin listesi
     */
    @GetMapping("/surveys/orphaned")
    public ResponseEntity<Map<String, Object>> getOrphanedSurveys() {
        logger.info("Yetim anketler listeleniyor - Listing orphaned surveys");
        
        try {
            List<Survey> orphanedSurveys = surveyService.findOrphanedSurveys();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Yetim anketler başarıyla getirildi - Orphaned surveys retrieved successfully");
            response.put("count", orphanedSurveys.size());
            response.put("surveys", orphanedSurveys);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Yetim anketler getirilirken hata oluştu - Error retrieving orphaned surveys", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Yetim anketler getirilirken hata oluştu - Error retrieving orphaned surveys: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Kullanıcısı silinmiş anketleri listeler
     * Lists surveys with inactive creators
     * 
     * @return Kullanıcısı silinmiş anketlerin listesi
     */
    @GetMapping("/surveys/inactive-creator")
    public ResponseEntity<Map<String, Object>> getSurveysWithInactiveCreator() {
        logger.info("Kullanıcısı silinmiş anketler listeleniyor - Listing surveys with inactive creator");
        
        try {
            List<Survey> surveys = surveyService.findSurveysWithInactiveCreator();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcısı silinmiş anketler başarıyla getirildi - Surveys with inactive creator retrieved successfully");
            response.put("count", surveys.size());
            response.put("surveys", surveys);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Kullanıcısı silinmiş anketler getirilirken hata oluştu - Error retrieving surveys with inactive creator", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kullanıcısı silinmiş anketler getirilirken hata oluştu - Error retrieving surveys with inactive creator: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Sorusu olmayan anketleri listeler
     * Lists surveys without questions
     * 
     * @return Sorusuz anketlerin listesi
     */
    @GetMapping("/surveys/without-questions")
    public ResponseEntity<Map<String, Object>> getSurveysWithoutQuestions() {
        logger.info("Sorusu olmayan anketler listeleniyor - Listing surveys without questions");
        
        try {
            List<Survey> surveys = surveyService.findSurveysWithoutQuestions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sorusu olmayan anketler başarıyla getirildi - Surveys without questions retrieved successfully");
            response.put("count", surveys.size());
            response.put("surveys", surveys);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Sorusu olmayan anketler getirilirken hata oluştu - Error retrieving surveys without questions", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Sorusu olmayan anketler getirilirken hata oluştu - Error retrieving surveys without questions: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Eski ve yanıtsız anketleri listeler
     * Lists old surveys without responses
     * 
     * @param daysOld Kaç gün öncesinden itibaren eski sayılacağı (varsayılan: 30)
     * @return Eski ve yanıtsız anketlerin listesi
     */
    @GetMapping("/surveys/old-without-responses")
    public ResponseEntity<Map<String, Object>> getOldSurveysWithoutResponses(
            @RequestParam(defaultValue = "30") int daysOld) {
        
        logger.info("Eski ve yanıtsız anketler listeleniyor - Listing old surveys without responses (older than {} days)", daysOld);
        
        try {
            List<Survey> surveys = surveyService.findOldSurveysWithoutResponses(daysOld);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Eski ve yanıtsız anketler başarıyla getirildi - Old surveys without responses retrieved successfully");
            response.put("daysOld", daysOld);
            response.put("count", surveys.size());
            response.put("surveys", surveys);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Eski ve yanıtsız anketler getirilirken hata oluştu - Error retrieving old surveys without responses", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Eski ve yanıtsız anketler getirilirken hata oluştu - Error retrieving old surveys without responses: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Yetim anketleri siler (hard delete)
     * Deletes orphaned surveys (hard delete)
     * 
     * @return Silme işlemi sonucu
     */
    @DeleteMapping("/surveys/orphaned")
    public ResponseEntity<Map<String, Object>> deleteOrphanedSurveys() {
        logger.info("Yetim anketler siliniyor - Deleting orphaned surveys");
        
        try {
            int deletedCount = surveyService.deleteOrphanedSurveys();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Yetim anketler başarıyla silindi - Orphaned surveys deleted successfully");
            response.put("deletedCount", deletedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Yetim anketler silinirken hata oluştu - Error deleting orphaned surveys", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Yetim anketler silinirken hata oluştu - Error deleting orphaned surveys: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Kullanıcısı silinmiş anketleri soft delete yapar
     * Soft deletes surveys with inactive creators
     * 
     * @return Soft delete işlemi sonucu
     */
    @PutMapping("/surveys/inactive-creator/soft-delete")
    public ResponseEntity<Map<String, Object>> softDeleteSurveysWithInactiveCreator() {
        logger.info("Kullanıcısı silinmiş anketler soft delete ediliyor - Soft deleting surveys with inactive creator");
        
        try {
            int softDeletedCount = surveyService.softDeleteSurveysWithInactiveCreator();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Kullanıcısı silinmiş anketler başarıyla soft delete edildi - Surveys with inactive creator soft deleted successfully");
            response.put("softDeletedCount", softDeletedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Kullanıcısı silinmiş anketler soft delete edilirken hata oluştu - Error soft deleting surveys with inactive creator", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kullanıcısı silinmiş anketler soft delete edilirken hata oluştu - Error soft deleting surveys with inactive creator: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Sorusu olmayan anketleri temizler
     * Cleans up surveys without questions
     * 
     * @return Temizlik işlemi sonucu
     */
    @PutMapping("/surveys/without-questions/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupSurveysWithoutQuestions() {
        logger.info("Sorusu olmayan anketler temizleniyor - Cleaning up surveys without questions");
        
        try {
            int cleanedCount = surveyService.cleanupSurveysWithoutQuestions();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Sorusu olmayan anketler başarıyla temizlendi - Surveys without questions cleaned successfully");
            response.put("cleanedCount", cleanedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Sorusu olmayan anketler temizlenirken hata oluştu - Error cleaning surveys without questions", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Sorusu olmayan anketler temizlenirken hata oluştu - Error cleaning surveys without questions: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Eski ve yanıtsız anketleri temizler
     * Cleans up old surveys without responses
     * 
     * @param daysOld Kaç gün öncesinden itibaren eski sayılacağı (varsayılan: 30)
     * @return Temizlik işlemi sonucu
     */
    @PutMapping("/surveys/old-without-responses/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldSurveysWithoutResponses(
            @RequestParam(defaultValue = "30") int daysOld) {
        
        logger.info("Eski ve yanıtsız anketler temizleniyor - Cleaning up old surveys without responses (older than {} days)", daysOld);
        
        try {
            int cleanedCount = surveyService.cleanupOldSurveysWithoutResponses(daysOld);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Eski ve yanıtsız anketler başarıyla temizlendi - Old surveys without responses cleaned successfully");
            response.put("daysOld", daysOld);
            response.put("cleanedCount", cleanedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Eski ve yanıtsız anketler temizlenirken hata oluştu - Error cleaning old surveys without responses", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Eski ve yanıtsız anketler temizlenirken hata oluştu - Error cleaning old surveys without responses: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Kapsamlı anket temizliği yapar
     * Performs comprehensive survey cleanup
     * 
     * @param daysOldForCleanup Kaç gün öncesinden itibaren eski sayılacağı (varsayılan: 30)
     * @return Kapsamlı temizlik raporu
     */
    @PostMapping("/surveys/comprehensive-cleanup")
    public ResponseEntity<Map<String, Object>> performComprehensiveCleanup(
            @RequestParam(defaultValue = "30") int daysOldForCleanup) {
        
        logger.info("Kapsamlı anket temizliği başlatılıyor - Starting comprehensive survey cleanup (daysOld: {})", daysOldForCleanup);
        
        try {
            SurveyService.SurveyCleanupReport report = surveyService.performComprehensiveCleanup(daysOldForCleanup);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", report.isSuccess());
            response.put("message", report.getMessage());
            response.put("daysOldForCleanup", daysOldForCleanup);
            response.put("report", report);
            
            if (report.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Kapsamlı anket temizliği sırasında hata oluştu - Error during comprehensive survey cleanup", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Kapsamlı anket temizliği sırasında hata oluştu - Error during comprehensive survey cleanup: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Anket Yönetimi Endpoint'leri - Survey Management Endpoints

    /**
     * Tüm anketleri listeler (admin görünümü)
     * Lists all surveys (admin view)
     * 
     * @return Tüm anketlerin listesi
     */
    @GetMapping("/surveys")
    public ResponseEntity<Map<String, Object>> getAllSurveys() {
        logger.info("Tüm anketler listeleniyor (admin) - Listing all surveys (admin view)");
        
        try {
            List<Survey> surveys = surveyService.getAllActiveSurveys();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tüm anketler başarıyla getirildi - All surveys retrieved successfully");
            response.put("count", surveys.size());
            response.put("surveys", surveys);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Anketler getirilirken hata oluştu - Error retrieving surveys", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Anketler getirilirken hata oluştu - Error retrieving surveys: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Anket siler (admin yetkisiyle)
     * Deletes survey (with admin privileges)
     * 
     * @param surveyId Silinecek anketin ID'si
     * @return Silme işlemi sonucu
     */
    @DeleteMapping("/surveys/{surveyId}")
    public ResponseEntity<Map<String, Object>> deleteSurvey(@PathVariable Long surveyId) {
        logger.info("Anket siliniyor (admin) - Deleting survey (admin): {}", surveyId);
        
        try {
            boolean deleted = surveyService.deleteSurvey(surveyId);
            
            Map<String, Object> response = new HashMap<>();
            
            if (deleted) {
                response.put("success", true);
                response.put("message", "Anket başarıyla silindi - Survey deleted successfully");
                response.put("surveyId", surveyId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Anket bulunamadı - Survey not found");
                response.put("surveyId", surveyId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Anket silinirken hata oluştu - Error deleting survey", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Anket silinirken hata oluştu - Error deleting survey: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Anket durumunu günceller (admin yetkisiyle)
     * Updates survey status (with admin privileges)
     * 
     * @param surveyId Güncellenecek anketin ID'si
     * @param action Yapılacak işlem (activate, close)
     * @return Güncelleme işlemi sonucu
     */
    @PutMapping("/surveys/{surveyId}/status")
    public ResponseEntity<Map<String, Object>> updateSurveyStatus(
            @PathVariable Long surveyId,
            @RequestParam String action) {
        
        logger.info("Anket durumu güncelleniyor (admin) - Updating survey status (admin): {} - {}", surveyId, action);
        
        try {
            boolean updated = false;
            String message = "";
            
            switch (action.toLowerCase()) {
                case "activate":
                    updated = surveyService.activateSurvey(surveyId);
                    message = updated ? "Anket başarıyla aktif hale getirildi - Survey activated successfully" : 
                                      "Anket bulunamadı - Survey not found";
                    break;
                case "close":
                    updated = surveyService.closeSurvey(surveyId);
                    message = updated ? "Anket başarıyla kapatıldı - Survey closed successfully" : 
                                      "Anket bulunamadı - Survey not found";
                    break;
                default:
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "Geçersiz işlem - Invalid action: " + action);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", updated);
            response.put("message", message);
            response.put("surveyId", surveyId);
            response.put("action", action);
            
            if (updated) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("Anket durumu güncellenirken hata oluştu - Error updating survey status", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Anket durumu güncellenirken hata oluştu - Error updating survey status: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Anket istatistiklerini getirir
     * Gets survey statistics
     * 
     * @return Anket istatistikleri
     */
    @GetMapping("/surveys/statistics")
    public ResponseEntity<Map<String, Object>> getSurveyStatistics() {
        logger.info("Anket istatistikleri getiriliyor - Getting survey statistics");
        
        try {
            List<Object[]> statistics = surveyService.getSurveyStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Anket istatistikleri başarıyla getirildi - Survey statistics retrieved successfully");
            response.put("statistics", statistics);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Anket istatistikleri getirilirken hata oluştu - Error retrieving survey statistics", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Anket istatistikleri getirilirken hata oluştu - Error retrieving survey statistics: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
