package com.anket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Soru Entity Sınıfı - Question Entity Class
 * 
 * Anketlerdeki soruları temsil eder
 * Represents questions in surveys
 * 
 * Veritabanı Tablosu: questions
 * Database Table: questions
 * 
 * İlişkili Entity'ler - Related Entities:
 * - Survey (Sorunun ait olduğu anket - Survey that owns the question): Many-to-One
 * - Option (Soru seçenekleri - Question options): One-to-Many
 * - Response (Soru yanıtları - Question responses): One-to-Many
 * 
 * İlişkili Servisler - Related Services:
 * - QuestionService: Soru işlemleri için
 * - OptionService: Seçenek işlemleri için
 * - ResponseService: Yanıt işlemleri için
 */
@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    /**
     * Soru Metni - Question Text
     * 
     * Sorunun içeriği (zorunlu alan)
     * Question content (required field)
     * 
     * @NotBlank: Boş olamaz
     * @Size: Minimum 5, maksimum 500 karakter
     */
    @NotBlank(message = "Soru metni boş olamaz - Question text cannot be empty")
    @Size(min = 5, max = 500, message = "Soru metni 5-500 karakter arasında olmalıdır - Question text must be between 5-500 characters")
    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    /**
     * Soru Tipi - Question Type
     * 
     * Sorunun türünü belirtir (çoktan seçmeli, tekli seçim, vb.)
     * Indicates the type of question (multiple choice, single choice, etc.)
     * 
     * Şu an sadece çoktan seçmeli destekleniyor
     * Currently only multiple choice is supported
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;

    /**
     * Sıra Numarası - Order Index
     * 
     * Sorunun anket içindeki sırası
     * Order of question within the survey
     * 
     * @NotNull: Null olamaz
     * Değer 1'den başlar (1, 2, 3, ...)
     */
    @NotNull(message = "Sıra numarası boş olamaz - Order index cannot be null")
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    /**
     * Zorunlu Soru - Required Question
     * 
     * Sorunun yanıtlanmasının zorunlu olup olmadığını belirtir
     * Indicates whether answering the question is mandatory
     * 
     * true: Zorunlu (yanıt verilmeden anket gönderilemez)
     * false: Opsiyonel (boş bırakılabilir)
     */
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = true;

    /**
     * Ait Olduğu Anket - Parent Survey
     * 
     * Bu sorunun ait olduğu anket
     * Survey that this question belongs to
     * 
     * @ManyToOne: Birden fazla soru - bir anket
     * @JoinColumn: Veritabanında survey_id kolonu ile bağlanır
     * fetch = FetchType.LAZY: İhtiyaç duyulduğunda yüklenir
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @JsonIgnore // JSON çıktısında sonsuz döngü önlemek için
    private Survey survey;

    /**
     * Soru Seçenekleri - Question Options
     * 
     * Bu soruya ait seçenekler (A, B, C, D gibi)
     * Options belonging to this question (like A, B, C, D)
     * 
     * @OneToMany: Bir soru - birden fazla seçenek
     * mappedBy = "question": Option entity'sindeki question alanı ile eşleşir
     * cascade = CascadeType.ALL: Soru silindiğinde seçenekleri de silinir
     * orphanRemoval = true: Bağlantısı kesilen seçenekler otomatik silinir
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("orderIndex ASC") // Sıra numarasına göre sıralar
    private List<Option> options = new ArrayList<>();

    /**
     * Soru Yanıtları - Question Responses
     * 
     * Bu soruya verilen yanıtlar
     * Responses given to this question
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // JSON çıktısında sonsuz döngü önlemek için
    private List<Response> responses = new ArrayList<>();

    /**
     * Açıklama/Yardım Metni - Description/Help Text
     * 
     * Soruya ek açıklama (opsiyonel)
     * Additional description for the question (optional)
     */
    @Size(max = 300, message = "Açıklama maksimum 300 karakter olabilir - Description can be maximum 300 characters")
    @Column(name = "description", length = 300)
    private String description;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * JPA tarafından kullanılır
     */
    public Question() {
    }

    /**
     * Parametreli Constructor - Parameterized Constructor
     * 
     * @param questionText Soru metni
     * @param questionType Soru tipi
     * @param orderIndex Sıra numarası
     * @param survey Ait olduğu anket
     */
    public Question(String questionText, QuestionType questionType, Integer orderIndex, Survey survey) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.orderIndex = orderIndex;
        this.survey = survey;
        this.isRequired = true;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * Soruya seçenek ekler - Adds option to question
     * @param option Eklenecek seçenek
     */
    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
        option.setOrderIndex(options.size()); // Sıra numarasını ayarla
    }

    /**
     * Sorudan seçenek çıkarır - Removes option from question
     * @param option Çıkarılacak seçenek
     */
    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
        // Sıra numaralarını yeniden düzenle
        reorderOptions();
    }

    /**
     * Seçeneklerin sıra numaralarını yeniden düzenler
     * Reorders option indices
     */
    private void reorderOptions() {
        for (int i = 0; i < options.size(); i++) {
            options.get(i).setOrderIndex(i + 1);
        }
    }

    /**
     * Soruya yanıt ekler - Adds response to question
     * @param response Eklenecek yanıt
     */
    public void addResponse(Response response) {
        responses.add(response);
        response.setQuestion(this);
    }

    /**
     * Toplam yanıt sayısını getirir - Returns total response count
     * @return Yanıt sayısı
     */
    public int getResponseCount() {
        return responses.size();
    }

    /**
     * Toplam seçenek sayısını getirir - Returns total option count
     * @return Seçenek sayısı
     */
    public int getOptionCount() {
        return options.size();
    }

    /**
     * Belirli bir seçeneğin kaç kez seçildiğini hesaplar
     * Calculates how many times a specific option was selected
     * 
     * @param optionId Seçenek ID'si
     * @return Seçilme sayısı
     */
    public long getOptionResponseCount(Long optionId) {
        return responses.stream()
                .filter(response -> response.getSelectedOption() != null)
                .filter(response -> response.getSelectedOption().getId().equals(optionId))
                .count();
    }

    /**
     * Belirli bir seçeneğin yüzdelik oranını hesaplar
     * Calculates percentage of a specific option
     * 
     * @param optionId Seçenek ID'si
     * @return Yüzdelik oran (0-100 arası)
     */
    public double getOptionPercentage(Long optionId) {
        int totalResponses = getResponseCount();
        if (totalResponses == 0) {
            return 0.0;
        }
        
        long optionCount = getOptionResponseCount(optionId);
        return (double) optionCount / totalResponses * 100.0;
    }

    /**
     * Sorunun geçerli olup olmadığını kontrol eder
     * Validates the question
     * 
     * @return true: geçerli, false: geçersiz
     */
    public boolean isValid() {
        // Soru metni boş olmamalı
        if (questionText == null || questionText.trim().isEmpty()) {
            return false;
        }
        
        // En az 2 seçenek olmalı (çoktan seçmeli sorular için)
        if (questionType == QuestionType.MULTIPLE_CHOICE && options.size() < 2) {
            return false;
        }
        
        // Sıra numarası pozitif olmalı
        if (orderIndex == null || orderIndex <= 0) {
            return false;
        }
        
        return true;
    }

    /**
     * Sorunun düzenlenebilir olup olmadığını kontrol eder
     * Checks if question is editable
     * 
     * @return true: düzenlenebilir, false: düzenlenemez
     */
    public boolean isEditable() {
        if (survey == null) {
            return true; // Ankete bağlı değilse düzenlenebilir
        }
        
        // Sadece taslak durumdaki anketlerin soruları düzenlenebilir
        return survey.getStatus() == SurveyStatus.DRAFT;
    }

    /**
     * ID'ye göre seçenek bulur - Finds option by ID
     * 
     * @param optionId Seçenek ID'si
     * @return Seçenek (bulunamazsa null)
     */
    public Option findOptionById(Long optionId) {
        return options.stream()
                .filter(option -> option.getId().equals(optionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Sıra numarasına göre seçenek bulur - Finds option by order index
     * 
     * @param orderIndex Sıra numarası
     * @return Seçenek (bulunamazsa null)
     */
    public Option findOptionByOrderIndex(Integer orderIndex) {
        return options.stream()
                .filter(option -> option.getOrderIndex().equals(orderIndex))
                .findFirst()
                .orElse(null);
    }

    /**
     * String temsili - String representation
     */
    @Override
    public String toString() {
        return "Question{" +
                "id=" + getId() +
                ", questionText='" + questionText + '\'' +
                ", questionType=" + questionType +
                ", orderIndex=" + orderIndex +
                ", isRequired=" + isRequired +
                ", optionCount=" + getOptionCount() +
                ", responseCount=" + getResponseCount() +
                '}';
    }
}
