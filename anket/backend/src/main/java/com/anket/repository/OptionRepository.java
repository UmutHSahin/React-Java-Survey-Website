package com.anket.repository;

import com.anket.entity.Option;
import com.anket.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Option Repository - Seçenek Veri Erişim Katmanı
 * Provides database operations for Option entity - Option entity'si için veritabanı işlemleri sağlar
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    
    /**
     * Find all options for a question - Bir soruya ait tüm seçenekleri bul
     * @param question Question entity - Soru entity'si
     * @return List of options - Seçenek listesi
     */
    List<Option> findByQuestionOrderByOrderIndex(Question question);
    
    /**
     * Find all options for a question - Bir soruya ait tüm seçenekleri bul
     * @param question Question entity - Soru entity'si
     * @return List of options - Seçenek listesi
     */
    List<Option> findByQuestion(Question question);
    
    /**
     * Find all options for a question by question ID - Soru ID'sine göre tüm seçenekleri bul
     * @param questionId Question ID - Soru ID'si
     * @return List of options - Seçenek listesi
     */
    @Query("SELECT o FROM Option o WHERE o.question.id = :questionId ORDER BY o.orderIndex")
    List<Option> findByQuestionIdOrderByOrderIndex(@Param("questionId") Long questionId);
    
    /**
     * Delete all options for a question - Bir sorunun tüm seçeneklerini sil
     * @param question Question entity - Soru entity'si
     */
    @Modifying
    @Query("DELETE FROM Option o WHERE o.question = :question")
    void deleteByQuestion(@Param("question") Question question);
    
    /**
     * Delete all options for a question by question ID - Soru ID'sine göre tüm seçenekleri sil
     * @param questionId Question ID - Soru ID'si
     */
    @Modifying
    @Query("DELETE FROM Option o WHERE o.question.id = :questionId")
    void deleteByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * Delete all options for questions belonging to a survey - Bir ankete ait soruların tüm seçeneklerini sil
     * @param surveyId Survey ID - Anket ID'si
     */
    @Modifying
    @Query("DELETE FROM Option o WHERE o.question.survey.id = :surveyId")
    void deleteByQuestionSurveyId(@Param("surveyId") Long surveyId);
    
    /**
     * Count options for a question - Bir sorunun seçenek sayısını say
     * @param question Question entity - Soru entity'si
     * @return Option count - Seçenek sayısı
     */
    long countByQuestion(Question question);
    
    /**
     * Count options for a question by question ID - Soru ID'sine göre seçenek sayısını say
     * @param questionId Question ID - Soru ID'si
     * @return Option count - Seçenek sayısı
     */
    @Query("SELECT COUNT(o) FROM Option o WHERE o.question.id = :questionId")
    long countByQuestionId(@Param("questionId") Long questionId);
}
