package com.anket.service;

import com.anket.entity.Survey;
import com.anket.entity.SurveyStatus;
import com.anket.entity.User;
import com.anket.repository.SurveyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Anket Servis Sınıfı - Survey Service Class
 * 
 * Anket ile ilgili tüm iş mantığını gerçekleştirir
 * Handles all business logic related to surveys
 * 
 * İlişkili Sınıflar - Related Classes:
 * - Survey entity: Anket verilerini temsil eder
 * - SurveyRepository: Veritabanı işlemlerini gerçekleştirir
 * - SurveyController: REST API endpoint'lerinde kullanılır
 * - UserService: Kullanıcı işlemleri için
 */
@Service
@Transactional
public class SurveyService {

    private static final Logger logger = LoggerFactory.getLogger(SurveyService.class);

    /**
     * Anket Repository - Survey Repository
     * Veritabanı işlemleri için kullanılır
     */
    @Autowired
    private SurveyRepository surveyRepository;

    // Note: UserService will be used for user-related operations in future enhancements
    // Not: UserService gelecekteki geliştirmelerde kullanıcı işlemleri için kullanılacak

    // CRUD İşlemleri - CRUD Operations

    /**
     * Yeni anket oluşturur - Creates new survey
     * 
     * @param survey Oluşturulacak anket
     * @return Oluşturulan anket
     */
    public Survey createSurvey(Survey survey) {
        logger.info("Yeni anket oluşturuluyor - Creating new survey: {}", survey.getTitle());
        
        // Başlangıç değerlerini ayarla
        if (survey.getStatus() == null) {
            survey.setStatus(SurveyStatus.DRAFT);
        }
        
        if (survey.getIsAnonymous() == null) {
            survey.setIsAnonymous(true);
        }
        
        if (survey.getAllowMultipleResponses() == null) {
            survey.setAllowMultipleResponses(false);
        }
        
        Survey savedSurvey = surveyRepository.save(survey);
        logger.info("Anket başarıyla oluşturuldu - Survey created successfully with ID: {}", savedSurvey.getId());
        
        return savedSurvey;
    }

    /**
     * Anket günceller - Updates survey
     * 
     * @param survey Güncellenecek anket
     * @return Güncellenmiş anket
     */
    public Survey updateSurvey(Survey survey) {
        logger.info("Anket güncelleniyor - Updating survey: {}", survey.getId());
        
        Survey updatedSurvey = surveyRepository.save(survey);
        logger.info("Anket başarıyla güncellendi - Survey updated successfully: {}", updatedSurvey.getId());
        
        return updatedSurvey;
    }

    /**
     * ID'ye göre anket getirir - Gets survey by ID
     * 
     * @param id Anket ID'si
     * @return Anket (varsa)
     */
    public Optional<Survey> getSurveyById(Long id) {
        logger.debug("Anket getiriliyor - Getting survey by ID: {}", id);
        return surveyRepository.findById(id);
    }

    /**
     * Tüm aktif anketleri getirir - Gets all active surveys
     * 
     * @return Aktif anketlerin listesi
     */
    public List<Survey> getAllActiveSurveys() {
        logger.debug("Tüm aktif anketler getiriliyor - Getting all active surveys");
        return surveyRepository.findByIsActiveTrue();
    }

    /**
     * Tüm anketleri getirir (aktif ve pasif dahil) - Gets all surveys (including active and inactive)
     * 
     * @return Tüm anketlerin listesi
     */
    public List<Survey> getAllSurveys() {
        logger.debug("Tüm anketler getiriliyor (aktif ve pasif) - Getting all surveys (active and inactive)");
        return surveyRepository.findAll();
    }

    /**
     * Belirli kullanıcının anketlerini getirir - Gets surveys by specific user
     * 
     * @param creator Anket oluşturan kullanıcı
     * @return Kullanıcının anketleri
     */
    public List<Survey> getSurveysByCreator(User creator) {
        logger.debug("Kullanıcının anketleri getiriliyor - Getting surveys by creator: {}", creator.getEmail());
        return surveyRepository.findByCreatorAndIsActiveTrue(creator);
    }

    /**
     * Belirli durumdaki anketleri getirir - Gets surveys by status
     * 
     * @param status Anket durumu
     * @return Bu durumdaki anketler
     */
    public List<Survey> getSurveysByStatus(SurveyStatus status) {
        logger.debug("Belirli durumdaki anketler getiriliyor - Getting surveys by status: {}", status);
        return surveyRepository.findByStatusAndIsActiveTrue(status);
    }

    /**
     * Anket siler (soft delete) - Deletes survey (soft delete)
     * 
     * @param id Silinecek anketin ID'si
     * @return Silme işlemi başarılı ise true
     */
    public boolean deleteSurvey(Long id) {
        logger.info("Anket siliniyor - Deleting survey: {}", id);
        
        Optional<Survey> surveyOpt = surveyRepository.findById(id);
        if (surveyOpt.isPresent()) {
            Survey survey = surveyOpt.get();
            survey.setIsActive(false);
            surveyRepository.save(survey);
            
            logger.info("Anket başarıyla silindi - Survey deleted successfully: {}", id);
            return true;
        }
        
        logger.warn("Silinecek anket bulunamadı - Survey not found for deletion: {}", id);
        return false;
    }

    // Anket Durumu İşlemleri - Survey Status Operations

    /**
     * Anketi aktif duruma getirir - Activates survey
     * 
     * @param id Anket ID'si
     * @return İşlem başarılı ise true
     */
    public boolean activateSurvey(Long id) {
        logger.info("Anket aktif hale getiriliyor - Activating survey: {}", id);
        
        Optional<Survey> surveyOpt = surveyRepository.findById(id);
        if (surveyOpt.isPresent()) {
            Survey survey = surveyOpt.get();
            survey.activate();
            surveyRepository.save(survey);
            
            logger.info("Anket başarıyla aktif hale getirildi - Survey activated successfully: {}", id);
            return true;
        }
        
        logger.warn("Aktif hale getirilecek anket bulunamadı - Survey not found for activation: {}", id);
        return false;
    }

    /**
     * Anketi kapatır - Closes survey
     * 
     * @param id Anket ID'si
     * @return İşlem başarılı ise true
     */
    public boolean closeSurvey(Long id) {
        logger.info("Anket kapatılıyor - Closing survey: {}", id);
        
        Optional<Survey> surveyOpt = surveyRepository.findById(id);
        if (surveyOpt.isPresent()) {
            Survey survey = surveyOpt.get();
            survey.close();
            surveyRepository.save(survey);
            
            logger.info("Anket başarıyla kapatıldı - Survey closed successfully: {}", id);
            return true;
        }
        
        logger.warn("Kapatılacak anket bulunamadı - Survey not found for closing: {}", id);
        return false;
    }

    // Temizlik İşlemleri - Cleanup Operations

    /**
     * Yetim anketleri (oluşturucu kullanıcısı olmayan) bulur
     * Finds orphaned surveys (surveys without creator user)
     * 
     * @return Yetim anketlerin listesi
     */
    public List<Survey> findOrphanedSurveys() {
        logger.info("Yetim anketler aranıyor - Searching for orphaned surveys");
        
        List<Survey> orphanedSurveys = surveyRepository.findOrphanedSurveys();
        logger.info("Bulunan yetim anket sayısı - Found orphaned surveys count: {}", orphanedSurveys.size());
        
        return orphanedSurveys;
    }

    /**
     * Kullanıcısı silinmiş anketleri bulur
     * Finds surveys whose creator user has been deleted
     * 
     * @return Kullanıcısı silinmiş anketlerin listesi
     */
    public List<Survey> findSurveysWithInactiveCreator() {
        logger.info("Kullanıcısı silinmiş anketler aranıyor - Searching for surveys with inactive creator");
        
        List<Survey> surveysWithInactiveCreator = surveyRepository.findSurveysWithInactiveCreator();
        logger.info("Bulunan anket sayısı - Found surveys with inactive creator count: {}", surveysWithInactiveCreator.size());
        
        return surveysWithInactiveCreator;
    }

    /**
     * Sorusu olmayan anketleri bulur
     * Finds surveys without any questions
     * 
     * @return Sorusuz anketlerin listesi
     */
    public List<Survey> findSurveysWithoutQuestions() {
        logger.info("Sorusu olmayan anketler aranıyor - Searching for surveys without questions");
        
        List<Survey> surveysWithoutQuestions = surveyRepository.findSurveysWithoutQuestions();
        logger.info("Bulunan sorusuz anket sayısı - Found surveys without questions count: {}", surveysWithoutQuestions.size());
        
        return surveysWithoutQuestions;
    }

    /**
     * Eski ve yanıtsız anketleri bulur
     * Finds old surveys without responses
     * 
     * @param daysOld Kaç gün öncesinden itibaren eski sayılacağı
     * @return Eski ve yanıtsız anketlerin listesi
     */
    public List<Survey> findOldSurveysWithoutResponses(int daysOld) {
        logger.info("Eski ve yanıtsız anketler aranıyor - Searching for old surveys without responses (older than {} days)", daysOld);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Survey> oldSurveys = surveyRepository.findOldSurveysWithoutResponses(cutoffDate);
        logger.info("Bulunan eski ve yanıtsız anket sayısı - Found old surveys without responses count: {}", oldSurveys.size());
        
        return oldSurveys;
    }

    /**
     * Yetim anketleri siler (hard delete)
     * Deletes orphaned surveys (hard delete)
     * 
     * @return Silinen anket sayısı
     */
    @Transactional
    public int deleteOrphanedSurveys() {
        logger.info("Yetim anketler siliniyor - Deleting orphaned surveys");
        
        int deletedCount = surveyRepository.deleteOrphanedSurveys();
        logger.info("Silinen yetim anket sayısı - Deleted orphaned surveys count: {}", deletedCount);
        
        return deletedCount;
    }

    /**
     * Kullanıcısı silinmiş anketleri soft delete yapar
     * Soft deletes surveys whose creator has been deleted
     * 
     * @return Soft delete edilen anket sayısı
     */
    @Transactional
    public int softDeleteSurveysWithInactiveCreator() {
        logger.info("Kullanıcısı silinmiş anketler soft delete ediliyor - Soft deleting surveys with inactive creator");
        
        int softDeletedCount = surveyRepository.softDeleteSurveysWithInactiveCreator();
        logger.info("Soft delete edilen anket sayısı - Soft deleted surveys count: {}", softDeletedCount);
        
        return softDeletedCount;
    }

    /**
     * Sorusu olmayan anketleri soft delete yapar
     * Soft deletes surveys without any questions
     * 
     * @return Soft delete edilen anket sayısı
     */
    @Transactional
    public int cleanupSurveysWithoutQuestions() {
        logger.info("Sorusu olmayan anketler temizleniyor - Cleaning up surveys without questions");
        
        int cleanedCount = surveyRepository.cleanupSurveysWithoutQuestions();
        logger.info("Temizlenen sorusuz anket sayısı - Cleaned surveys without questions count: {}", cleanedCount);
        
        return cleanedCount;
    }

    /**
     * Eski ve yanıtsız anketleri soft delete yapar
     * Soft deletes old surveys without responses
     * 
     * @param daysOld Kaç gün öncesinden itibaren eski sayılacağı
     * @return Soft delete edilen anket sayısı
     */
    @Transactional
    public int cleanupOldSurveysWithoutResponses(int daysOld) {
        logger.info("Eski ve yanıtsız anketler temizleniyor - Cleaning up old surveys without responses (older than {} days)", daysOld);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int cleanedCount = surveyRepository.cleanupOldSurveysWithoutResponses(cutoffDate);
        logger.info("Temizlenen eski ve yanıtsız anket sayısı - Cleaned old surveys without responses count: {}", cleanedCount);
        
        return cleanedCount;
    }

    /**
     * Kapsamlı anket temizliği yapar
     * Performs comprehensive survey cleanup
     * 
     * Şu işlemleri gerçekleştirir:
     * 1. Yetim anketleri siler (hard delete)
     * 2. Kullanıcısı silinmiş anketleri soft delete yapar
     * 3. Sorusu olmayan anketleri soft delete yapar
     * 4. Eski ve yanıtsız anketleri soft delete yapar
     * 
     * @param daysOldForCleanup Kaç gün öncesinden itibaren eski sayılacağı
     * @return Temizlik raporu
     */
    @Transactional
    public SurveyCleanupReport performComprehensiveCleanup(int daysOldForCleanup) {
        logger.info("Kapsamlı anket temizliği başlatılıyor - Starting comprehensive survey cleanup");
        
        SurveyCleanupReport report = new SurveyCleanupReport();
        
        try {
            // 1. Yetim anketleri sil
            report.setOrphanedSurveysDeleted(deleteOrphanedSurveys());
            
            // 2. Kullanıcısı silinmiş anketleri soft delete yap
            report.setSurveysWithInactiveCreatorSoftDeleted(softDeleteSurveysWithInactiveCreator());
            
            // 3. Sorusu olmayan anketleri soft delete yap
            report.setSurveysWithoutQuestionsCleaned(cleanupSurveysWithoutQuestions());
            
            // 4. Eski ve yanıtsız anketleri soft delete yap
            report.setOldSurveysWithoutResponsesCleaned(cleanupOldSurveysWithoutResponses(daysOldForCleanup));
            
            // 5. Süresi dolmuş anketleri kapat
            report.setExpiredSurveysClosed(surveyRepository.closeExpiredSurveys());
            
            // 6. Zamanı gelen anketleri aktif hale getir
            report.setScheduledSurveysActivated(surveyRepository.activateScheduledSurveys());
            
            report.setSuccess(true);
            report.setMessage("Kapsamlı anket temizliği başarıyla tamamlandı - Comprehensive survey cleanup completed successfully");
            
            logger.info("Kapsamlı anket temizliği tamamlandı - Comprehensive survey cleanup completed: {}", report);
            
        } catch (Exception e) {
            report.setSuccess(false);
            report.setMessage("Anket temizliği sırasında hata oluştu - Error occurred during survey cleanup: " + e.getMessage());
            
            logger.error("Anket temizliği sırasında hata oluştu - Error during survey cleanup", e);
        }
        
        return report;
    }

    // Arama ve Filtreleme İşlemleri - Search and Filter Operations

    /**
     * Anket arar - Searches surveys
     * 
     * @param searchTerm Arama terimi
     * @return Eşleşen anketler
     */
    public List<Survey> searchSurveys(String searchTerm) {
        logger.debug("Anket arama yapılıyor - Searching surveys with term: {}", searchTerm);
        return surveyRepository.searchSurveys(searchTerm);
    }

    /**
     * Yanıt alabilen aktif anketleri getirir
     * Gets active surveys that are accepting responses
     * 
     * @return Yanıt alabilen anketler
     */
    public List<Survey> getActiveSurveysAcceptingResponses() {
        logger.debug("Yanıt alabilen aktif anketler getiriliyor - Getting active surveys accepting responses");
        return surveyRepository.findActiveSurveysAcceptingResponses();
    }

    /**
     * Süresi dolmuş anketleri getirir
     * Gets expired surveys
     * 
     * @return Süresi dolmuş anketler
     */
    public List<Survey> getExpiredSurveys() {
        logger.debug("Süresi dolmuş anketler getiriliyor - Getting expired surveys");
        return surveyRepository.findExpiredSurveys();
    }

    /**
     * Anket istatistiklerini getirir
     * Gets survey statistics
     * 
     * @return Anket istatistik özeti
     */
    public List<Object[]> getSurveyStatistics() {
        logger.debug("Anket istatistikleri getiriliyor - Getting survey statistics");
        return surveyRepository.getSurveyStatisticsByStatus();
    }

    /**
     * Anket Temizlik Raporu Sınıfı
     * Survey Cleanup Report Class
     */
    public static class SurveyCleanupReport {
        private boolean success = false;
        private String message;
        private int orphanedSurveysDeleted = 0;
        private int surveysWithInactiveCreatorSoftDeleted = 0;
        private int surveysWithoutQuestionsCleaned = 0;
        private int oldSurveysWithoutResponsesCleaned = 0;
        private int expiredSurveysClosed = 0;
        private int scheduledSurveysActivated = 0;

        // Getter ve Setter metodları
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public int getOrphanedSurveysDeleted() { return orphanedSurveysDeleted; }
        public void setOrphanedSurveysDeleted(int orphanedSurveysDeleted) { this.orphanedSurveysDeleted = orphanedSurveysDeleted; }
        
        public int getSurveysWithInactiveCreatorSoftDeleted() { return surveysWithInactiveCreatorSoftDeleted; }
        public void setSurveysWithInactiveCreatorSoftDeleted(int surveysWithInactiveCreatorSoftDeleted) { this.surveysWithInactiveCreatorSoftDeleted = surveysWithInactiveCreatorSoftDeleted; }
        
        public int getSurveysWithoutQuestionsCleaned() { return surveysWithoutQuestionsCleaned; }
        public void setSurveysWithoutQuestionsCleaned(int surveysWithoutQuestionsCleaned) { this.surveysWithoutQuestionsCleaned = surveysWithoutQuestionsCleaned; }
        
        public int getOldSurveysWithoutResponsesCleaned() { return oldSurveysWithoutResponsesCleaned; }
        public void setOldSurveysWithoutResponsesCleaned(int oldSurveysWithoutResponsesCleaned) { this.oldSurveysWithoutResponsesCleaned = oldSurveysWithoutResponsesCleaned; }
        
        public int getExpiredSurveysClosed() { return expiredSurveysClosed; }
        public void setExpiredSurveysClosed(int expiredSurveysClosed) { this.expiredSurveysClosed = expiredSurveysClosed; }
        
        public int getScheduledSurveysActivated() { return scheduledSurveysActivated; }
        public void setScheduledSurveysActivated(int scheduledSurveysActivated) { this.scheduledSurveysActivated = scheduledSurveysActivated; }
        
        public int getTotalProcessed() {
            return orphanedSurveysDeleted + surveysWithInactiveCreatorSoftDeleted + 
                   surveysWithoutQuestionsCleaned + oldSurveysWithoutResponsesCleaned + 
                   expiredSurveysClosed + scheduledSurveysActivated;
        }
        
        @Override
        public String toString() {
            return "SurveyCleanupReport{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", orphanedSurveysDeleted=" + orphanedSurveysDeleted +
                    ", surveysWithInactiveCreatorSoftDeleted=" + surveysWithInactiveCreatorSoftDeleted +
                    ", surveysWithoutQuestionsCleaned=" + surveysWithoutQuestionsCleaned +
                    ", oldSurveysWithoutResponsesCleaned=" + oldSurveysWithoutResponsesCleaned +
                    ", expiredSurveysClosed=" + expiredSurveysClosed +
                    ", scheduledSurveysActivated=" + scheduledSurveysActivated +
                    ", totalProcessed=" + getTotalProcessed() +
                    '}';
        }
    }
}
