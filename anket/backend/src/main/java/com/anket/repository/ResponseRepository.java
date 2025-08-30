package com.anket.repository;

import com.anket.entity.Response;
import com.anket.entity.Survey;
import com.anket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Yanıt (Response) Repository - Veritabanı işlemleri
 * Response entity'si için CRUD operasyonları ve özel sorgular sağlar
 * Survey ve User entity'leri ile ilişkili çalışır
 */
@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {
    
    /**
     * Bir anket için tüm yanıtları getirir
     * @param survey Anket entity'si
     * @return Yanıt listesi
     */
    List<Response> findBySurvey(Survey survey);
    
    /**
     * Anket ID'sine göre tüm yanıtları getirir
     * @param surveyId Anket ID'si
     * @return Yanıt listesi
     */
    @Query("SELECT r FROM Response r WHERE r.survey.id = :surveyId")
    List<Response> findBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * Bir kullanıcının tüm yanıtlarını getirir
     * @param user Kullanıcı entity'si
     * @return Yanıt listesi
     */
    List<Response> findByUser(User user);
    
    /**
     * Kullanıcı ID'sine göre tüm yanıtları getirir
     * @param userId Kullanıcı ID'si
     * @return Yanıt listesi
     */
    @Query("SELECT r FROM Response r WHERE r.user.id = :userId")
    List<Response> findByUserId(@Param("userId") Long userId);
    
    /**
     * Kullanıcının belirli bir ankete yanıt verip vermediğini kontrol eder
     * @param userId Kullanıcı ID'si
     * @param surveyId Anket ID'si
     * @return Kullanıcı yanıt verdiyse true, aksi halde false
     */
    @Query("SELECT COUNT(r) > 0 FROM Response r WHERE r.user.id = :userId AND r.survey.id = :surveyId")
    boolean existsByUserIdAndSurveyId(@Param("userId") Long userId, @Param("surveyId") Long surveyId);
    
    /**
     * Bir anket için toplam yanıt sayısını getirir
     * @param survey Anket entity'si
     * @return Yanıt sayısı
     */
    long countBySurvey(Survey survey);
    
    /**
     * Anket ID'sine göre toplam yanıt sayısını getirir
     * @param surveyId Anket ID'si
     * @return Yanıt sayısı
     */
    @Query("SELECT COUNT(r) FROM Response r WHERE r.survey.id = :surveyId")
    long countBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * Bir kullanıcının toplam yanıt sayısını getirir
     * @param user Kullanıcı entity'si
     * @return Yanıt sayısı
     */
    long countByUser(User user);
    
    /**
     * Kullanıcı ID'sine göre toplam yanıt sayısını getirir
     * @param userId Kullanıcı ID'si
     * @return Yanıt sayısı
     */
    @Query("SELECT COUNT(r) FROM Response r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * Belirli bir ankete yanıt veren benzersiz kullanıcı sayısını getirir
     * @param surveyId Anket ID'si
     * @return Benzersiz kullanıcı sayısı
     */
    @Query("SELECT COUNT(DISTINCT r.user.id) FROM Response r WHERE r.survey.id = :surveyId AND r.user.id IS NOT NULL")
    long countDistinctUsersBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * Belirli bir seçenek için toplam yanıt sayısını getirir
     * @param optionId Seçenek ID'si
     * @return Yanıt sayısı
     */
    @Query("SELECT COUNT(r) FROM Response r WHERE r.selectedOption.id = :optionId")
    long countByOptionId(@Param("optionId") Long optionId);
    
    /**
     * Bir anket için tüm yanıtları siler
     * @param surveyId Anket ID'si
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM responses WHERE survey_id = :surveyId", nativeQuery = true)
    void deleteBySurveyId(@Param("surveyId") Long surveyId);
}
