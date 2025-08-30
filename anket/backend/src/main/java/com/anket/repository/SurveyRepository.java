package com.anket.repository;

import com.anket.entity.Survey;
import com.anket.entity.SurveyStatus;
import com.anket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Anket Repository Arayüzü - Survey Repository Interface
 * 
 * Anket entity'si için veritabanı işlemlerini gerçekleştirir
 * Performs database operations for Survey entity
 * 
 * İlişkili Sınıflar - Related Classes:
 * - Survey entity: Veritabanı tablosunu temsil eder
 * - SurveyService: Bu repository'yi kullanarak iş mantığını gerçekleştirir
 * - SurveyController: REST API endpoint'lerinde kullanır
 */
@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    /**
     * Aktif anketleri getirir - Returns active surveys
     * 
     * @return Aktif anketlerin listesi
     */
    List<Survey> findByIsActiveTrue();

    /**
     * Belirli duruma sahip aktif anketleri getirir
     * Returns active surveys with specific status
     * 
     * @param status Anket durumu
     * @return Bu duruma sahip aktif anketler
     */
    List<Survey> findByStatusAndIsActiveTrue(SurveyStatus status);

    /**
     * Belirli kullanıcının oluşturduğu aktif anketleri getirir
     * Returns active surveys created by specific user
     * 
     * @param creator Anket oluşturan kullanıcı
     * @return Bu kullanıcının oluşturduğu aktif anketler
     */
    List<Survey> findByCreatorAndIsActiveTrue(User creator);

    /**
     * Belirli kullanıcının belirli durumdaki anketlerini getirir
     * Returns surveys by specific user with specific status
     * 
     * @param creator Anket oluşturan kullanıcı
     * @param status Anket durumu
     * @return Eşleşen anketler
     */
    List<Survey> findByCreatorAndStatusAndIsActiveTrue(User creator, SurveyStatus status);

    /**
     * Başlığa göre anket arar (büyük/küçük harf duyarsız)
     * Searches surveys by title (case insensitive)
     * 
     * @param title Aranacak başlık (kısmi)
     * @return Eşleşen aktif anketler
     */
    List<Survey> findByTitleContainingIgnoreCaseAndIsActiveTrue(String title);

    /**
     * Belirli tarihten sonra oluşturulan anketleri getirir
     * Returns surveys created after specific date
     * 
     * @param date Başlangıç tarihi
     * @return Bu tarihten sonra oluşturulan aktif anketler
     */
    List<Survey> findByCreatedDateAfterAndIsActiveTrue(LocalDateTime date);

    /**
     * Belirli tarih aralığındaki anketleri getirir
     * Returns surveys created within specific date range
     * 
     * @param startDate Başlangıç tarihi
     * @param endDate Bitiş tarihi
     * @return Bu tarih aralığındaki aktif anketler
     */
    List<Survey> findByCreatedDateBetweenAndIsActiveTrue(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Toplam aktif anket sayısını getirir
     * Returns total count of active surveys
     * 
     * @return Aktif anket sayısı
     */
    long countByIsActiveTrue();

    /**
     * Belirli duruma sahip aktif anket sayısını getirir
     * Returns count of active surveys with specific status
     * 
     * @param status Anket durumu
     * @return Bu duruma sahip aktif anket sayısı
     */
    long countByStatusAndIsActiveTrue(SurveyStatus status);

    /**
     * Belirli kullanıcının oluşturduğu aktif anket sayısını getirir
     * Returns count of active surveys created by specific user
     * 
     * @param creator Anket oluşturan kullanıcı
     * @return Bu kullanıcının oluşturduğu aktif anket sayısı
     */
    long countByCreatorAndIsActiveTrue(User creator);

    /**
     * En çok yanıt alan anketleri getirir
     * Returns surveys with most responses
     * 
     * @param limit Getirilecek anket sayısı
     * @return En popüler anketler
     */
    @Query("SELECT s FROM Survey s LEFT JOIN s.responses r " +
           "WHERE s.isActive = true " +
           "GROUP BY s " +
           "ORDER BY COUNT(r) DESC")
    List<Survey> findMostPopularSurveys(@Param("limit") int limit);

    /**
     * Son oluşturulan anketleri getirir
     * Returns recently created surveys
     * 
     * @param limit Getirilecek anket sayısı
     * @return Son oluşturulan anketler
     */
    @Query("SELECT s FROM Survey s " +
           "WHERE s.isActive = true " +
           "ORDER BY s.createdDate DESC")
    List<Survey> findRecentSurveys(@Param("limit") int limit);

    /**
     * Anketin yanıt sayısını getirir
     * Returns response count for survey
     * 
     * @param surveyId Anket ID'si
     * @return Yanıt sayısı
     */
    @Query("SELECT COUNT(r) FROM Response r WHERE r.survey.id = :surveyId AND r.isActive = true")
    long countResponsesBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * Anketin soru sayısını getirir
     * Returns question count for survey
     * 
     * @param surveyId Anket ID'si
     * @return Soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId AND q.isActive = true")
    long countQuestionsBySurveyId(@Param("surveyId") Long surveyId);

    /**
     * Aktif (yanıt alabilen) anketleri getirir
     * Returns surveys that are currently accepting responses
     * 
     * @return Yanıt alabilen anketler
     */
    @Query("SELECT s FROM Survey s WHERE s.status = 'ACTIVE' AND s.isActive = true " +
           "AND (s.startDate IS NULL OR s.startDate <= CURRENT_TIMESTAMP) " +
           "AND (s.endDate IS NULL OR s.endDate >= CURRENT_TIMESTAMP)")
    List<Survey> findActiveSurveysAcceptingResponses();

    /**
     * Süresi dolmuş anketleri getirir
     * Returns expired surveys
     * 
     * @return Süresi dolmuş anketler
     */
    @Query("SELECT s FROM Survey s WHERE s.status = 'ACTIVE' AND s.isActive = true " +
           "AND s.endDate IS NOT NULL AND s.endDate < CURRENT_TIMESTAMP")
    List<Survey> findExpiredSurveys();

    /**
     * Henüz başlamamış anketleri getirir
     * Returns surveys that haven't started yet
     * 
     * @return Henüz başlamamış anketler
     */
    @Query("SELECT s FROM Survey s WHERE s.status = 'ACTIVE' AND s.isActive = true " +
           "AND s.startDate IS NOT NULL AND s.startDate > CURRENT_TIMESTAMP")
    List<Survey> findPendingSurveys();

    /**
     * Kullanıcının daha önce yanıtladığı anketleri getirir
     * Returns surveys that user has already responded to
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcının yanıtladığı anketler
     */
    @Query("SELECT DISTINCT s FROM Survey s JOIN s.responses r " +
           "WHERE r.user.id = :userId AND s.isActive = true AND r.isActive = true")
    List<Survey> findSurveysRespondedByUser(@Param("userId") Long userId);

    /**
     * Kullanıcının henüz yanıtlamadığı anketleri getirir
     * Returns surveys that user hasn't responded to yet
     * 
     * @param userId Kullanıcı ID'si
     * @return Kullanıcının yanıtlamadığı anketler
     */
    @Query("SELECT s FROM Survey s WHERE s.status = 'ACTIVE' AND s.isActive = true " +
           "AND s.id NOT IN (SELECT DISTINCT r.survey.id FROM Response r WHERE r.user.id = :userId AND r.isActive = true) " +
           "AND (s.startDate IS NULL OR s.startDate <= CURRENT_TIMESTAMP) " +
           "AND (s.endDate IS NULL OR s.endDate >= CURRENT_TIMESTAMP)")
    List<Survey> findAvailableSurveysForUser(@Param("userId") Long userId);

    /**
     * Anonim anketleri getirir
     * Returns anonymous surveys
     * 
     * @return Anonim anketler
     */
    List<Survey> findByIsAnonymousTrueAndStatusAndIsActiveTrue(SurveyStatus status);

    /**
     * Kayıtlı kullanıcı anketlerini getirir
     * Returns non-anonymous surveys
     * 
     * @return Kayıtlı kullanıcı anketleri
     */
    List<Survey> findByIsAnonymousFalseAndStatusAndIsActiveTrue(SurveyStatus status);

    /**
     * Çoklu yanıta izin veren anketleri getirir
     * Returns surveys that allow multiple responses
     * 
     * @return Çoklu yanıt anketleri
     */
    List<Survey> findByAllowMultipleResponsesTrueAndStatusAndIsActiveTrue(SurveyStatus status);

    /**
     * İstatistik için anket özetini getirir
     * Returns survey summary for statistics
     * 
     * @return Anket istatistik özeti
     */
    @Query("SELECT s.status, COUNT(s) FROM Survey s WHERE s.isActive = true GROUP BY s.status")
    List<Object[]> getSurveyStatisticsByStatus();

    /**
     * Aylık anket oluşturma istatistiklerini getirir
     * Returns monthly survey creation statistics
     * 
     * @param startDate Başlangıç tarihi
     * @param endDate Bitiş tarihi
     * @return Aylık anket sayıları
     */
    @Query("SELECT DATE_TRUNC('month', s.createdDate) as month, COUNT(s) " +
           "FROM Survey s " +
           "WHERE s.createdDate BETWEEN :startDate AND :endDate AND s.isActive = true " +
           "GROUP BY DATE_TRUNC('month', s.createdDate) " +
           "ORDER BY month")
    List<Object[]> getMonthlySurveyCreationStats(@Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);

    /**
     * Anket arama (başlık ve açıklamada)
     * Search surveys (in title and description)
     * 
     * @param searchTerm Arama terimi
     * @return Eşleşen anketler
     */
    @Query("SELECT s FROM Survey s WHERE s.isActive = true " +
           "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Survey> searchSurveys(@Param("searchTerm") String searchTerm);

    /**
     * Belirli kullanıcının anket arama
     * Search surveys by specific user
     * 
     * @param creatorId Oluşturucu kullanıcı ID'si
     * @param searchTerm Arama terimi
     * @return Eşleşen anketler
     */
    @Query("SELECT s FROM Survey s WHERE s.creator.id = :creatorId AND s.isActive = true " +
           "AND (LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Survey> searchSurveysByCreator(@Param("creatorId") Long creatorId, 
                                       @Param("searchTerm") String searchTerm);

    /**
     * Anket durumunu günceller
     * Updates survey status
     * 
     * @param surveyId Anket ID'si
     * @param status Yeni durum
     * @return Etkilenen satır sayısı
     */
    @Query("UPDATE Survey s SET s.status = :status WHERE s.id = :surveyId AND s.isActive = true")
    int updateSurveyStatus(@Param("surveyId") Long surveyId, @Param("status") SurveyStatus status);

    /**
     * Anketi soft delete yapar
     * Performs soft delete on survey
     * 
     * @param surveyId Silinecek anketin ID'si
     * @return Etkilenen satır sayısı
     */
    @Query("UPDATE Survey s SET s.isActive = false WHERE s.id = :surveyId")
    int softDeleteSurvey(@Param("surveyId") Long surveyId);

    /**
     * Anketi tekrar aktif hale getirir
     * Reactivates survey
     * 
     * @param surveyId Aktif hale getirilecek anketin ID'si
     * @return Etkilenen satır sayısı
     */
    @Query("UPDATE Survey s SET s.isActive = true WHERE s.id = :surveyId")
    int reactivateSurvey(@Param("surveyId") Long surveyId);

    /**
     * Süresi dolmuş anketleri otomatik kapatır
     * Automatically closes expired surveys
     * 
     * @return Kapatılan anket sayısı
     */
    @Query("UPDATE Survey s SET s.status = 'CLOSED' " +
           "WHERE s.status = 'ACTIVE' AND s.isActive = true " +
           "AND s.endDate IS NOT NULL AND s.endDate < CURRENT_TIMESTAMP")
    int closeExpiredSurveys();

    /**
     * Zamanı gelen anketleri aktif hale getirir
     * Activates surveys whose start time has come
     * 
     * @return Aktif hale getirilen anket sayısı
     */
    @Query("UPDATE Survey s SET s.status = 'ACTIVE' " +
           "WHERE s.status = 'DRAFT' AND s.isActive = true " +
           "AND s.startDate IS NOT NULL AND s.startDate <= CURRENT_TIMESTAMP")
    int activateScheduledSurveys();

    /**
     * Oluşturucu kullanıcısı olmayan anketleri bulur (yetim anketler)
     * Finds surveys without a creator user (orphaned surveys)
     * 
     * Bu anketler genellikle test sırasında veya veri tutarsızlığı nedeniyle oluşur
     * These surveys are usually created during testing or due to data inconsistency
     * 
     * @return Yetim anketlerin listesi
     */
    @Query("SELECT s FROM Survey s WHERE s.creator IS NULL AND s.isActive = true")
    List<Survey> findOrphanedSurveys();

    /**
     * Kullanıcısı silinmiş anketleri bulur
     * Finds surveys whose creator user has been deleted
     * 
     * @return Kullanıcısı silinmiş anketlerin listesi
     */
    @Query("SELECT s FROM Survey s WHERE s.creator.isActive = false AND s.isActive = true")
    List<Survey> findSurveysWithInactiveCreator();

    /**
     * Yetim anketleri (oluşturucu kullanıcısı olmayan) siler
     * Deletes orphaned surveys (surveys without creator user)
     * 
     * @return Silinen anket sayısı
     */
    @Query("DELETE FROM Survey s WHERE s.creator IS NULL")
    int deleteOrphanedSurveys();

    /**
     * Kullanıcısı silinmiş anketleri soft delete yapar
     * Soft deletes surveys whose creator has been deleted
     * 
     * @return Soft delete edilen anket sayısı
     */
    @Query("UPDATE Survey s SET s.isActive = false " +
           "WHERE s.creator.isActive = false AND s.isActive = true")
    int softDeleteSurveysWithInactiveCreator();

    /**
     * Belirli bir tarihten önce oluşturulan ve hiç yanıt almamış anketleri bulur
     * Finds surveys created before a specific date with no responses
     * 
     * @param date Tarih sınırı
     * @return Yanıtsız eski anketlerin listesi
     */
    @Query("SELECT s FROM Survey s WHERE s.createdDate < :date AND s.isActive = true " +
           "AND NOT EXISTS (SELECT r FROM Response r WHERE r.survey = s AND r.isActive = true)")
    List<Survey> findOldSurveysWithoutResponses(@Param("date") LocalDateTime date);

    /**
     * Belirli bir tarihten önce oluşturulan ve hiç yanıt almamış anketleri siler
     * Deletes surveys created before a specific date with no responses
     * 
     * @param date Tarih sınırı
     * @return Silinen anket sayısı
     */
    @Query("UPDATE Survey s SET s.isActive = false " +
           "WHERE s.createdDate < :date AND s.isActive = true " +
           "AND NOT EXISTS (SELECT r FROM Response r WHERE r.survey = s AND r.isActive = true)")
    int cleanupOldSurveysWithoutResponses(@Param("date") LocalDateTime date);

    /**
     * Sorusu olmayan anketleri bulur
     * Finds surveys without any questions
     * 
     * @return Sorusuz anketlerin listesi
     */
    @Query("SELECT s FROM Survey s WHERE s.isActive = true " +
           "AND NOT EXISTS (SELECT q FROM Question q WHERE q.survey = s AND q.isActive = true)")
    List<Survey> findSurveysWithoutQuestions();

    /**
     * Sorusu olmayan anketleri soft delete yapar
     * Soft deletes surveys without any questions
     * 
     * @return Soft delete edilen anket sayısı
     */
    @Query("UPDATE Survey s SET s.isActive = false " +
           "WHERE s.isActive = true " +
           "AND NOT EXISTS (SELECT q FROM Question q WHERE q.survey = s AND q.isActive = true)")
    int cleanupSurveysWithoutQuestions();
}
