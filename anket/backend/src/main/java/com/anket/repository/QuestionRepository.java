package com.anket.repository;

import com.anket.entity.Question;
import com.anket.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Question Repository - Soru Veri Erişim Katmanı
 * Provides database operations for Question entity - Question entity'si için veritabanı işlemleri sağlar
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * Find all questions for a survey - Bir ankete ait tüm soruları bul
     * @param survey Survey entity - Anket entity'si
     * @return List of questions - Soru listesi
     */
    List<Question> findBySurveyOrderByOrderIndex(Survey survey);
    
    /**
     * Find all questions for a survey by survey ID - Anket ID'sine göre tüm soruları bul
     * @param surveyId Survey ID - Anket ID'si
     * @return List of questions - Soru listesi
     */
    @Query("SELECT q FROM Question q WHERE q.survey.id = :surveyId ORDER BY q.orderIndex")
    List<Question> findBySurveyIdOrderByOrderIndex(@Param("surveyId") Long surveyId);
    
    /**
     * Delete all questions for a survey - Bir anketin tüm sorularını sil
     * @param survey Survey entity - Anket entity'si
     */
    @Modifying
    @Query("DELETE FROM Question q WHERE q.survey = :survey")
    void deleteBySurvey(@Param("survey") Survey survey);
    
    /**
     * Delete all questions for a survey by survey ID - Anket ID'sine göre tüm soruları sil
     * @param surveyId Survey ID - Anket ID'si
     */
    @Modifying
    @Query("DELETE FROM Question q WHERE q.survey.id = :surveyId")
    void deleteBySurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * Count questions for a survey - Bir anketin soru sayısını say
     * @param survey Survey entity - Anket entity'si
     * @return Question count - Soru sayısı
     */
    long countBySurvey(Survey survey);
    
    /**
     * Count questions for a survey by survey ID - Anket ID'sine göre soru sayısını say
     * @param surveyId Survey ID - Anket ID'si
     * @return Question count - Soru sayısı
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId")
    long countBySurveyId(@Param("surveyId") Long surveyId);
}
