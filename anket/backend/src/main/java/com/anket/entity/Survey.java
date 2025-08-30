package com.anket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Anket Entity Sınıfı - Survey Entity Class
 * 
 * Sistemdeki anketleri temsil eder
 * Represents surveys in the system
 * 
 * Veritabanı Tablosu: surveys
 * Database Table: surveys
 * 
 * İlişkili Entity'ler - Related Entities:
 * - User (Anket sahibi - Survey owner): Many-to-One
 * - Question (Anket soruları - Survey questions): One-to-Many
 * - Response (Anket yanıtları - Survey responses): One-to-Many
 * 
 * İlişkili Servisler - Related Services:
 * - SurveyService: Anket işlemleri için
 * - QuestionService: Soru işlemleri için
 * - ResponseService: Yanıt işlemleri için
 */
@Entity
@Table(name = "surveys")
public class Survey extends BaseEntity {

    /**
     * Anket Başlığı - Survey Title
     * 
     * Anketin başlığı (zorunlu alan)
     * Survey title (required field)
     * 
     * @NotBlank: Boş olamaz
     * @Size: Minimum 3, maksimum 200 karakter
     */
    @NotBlank(message = "Anket başlığı boş olamaz - Survey title cannot be empty")
    @Size(min = 3, max = 200, message = "Anket başlığı 3-200 karakter arasında olmalıdır - Survey title must be between 3-200 characters")
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * Anket Açıklaması - Survey Description
     * 
     * Anketin detaylı açıklaması (opsiyonel)
     * Detailed description of the survey (optional)
     */
    @Size(max = 1000, message = "Anket açıklaması maksimum 1000 karakter olabilir - Survey description can be maximum 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Anket Durumu - Survey Status
     * 
     * Anketin mevcut durumunu belirtir
     * Indicates current status of the survey
     * 
     * Değerler - Values:
     * - DRAFT: Taslak (henüz yayınlanmamış)
     * - ACTIVE: Aktif (yanıt alınabiliyor)
     * - CLOSED: Kapalı (yanıt alınmıyor)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SurveyStatus status = SurveyStatus.DRAFT;

    /**
     * Anket Oluşturan - Survey Creator
     * 
     * Bu anketi oluşturan kullanıcı
     * User who created this survey
     * 
     * @ManyToOne: Birden fazla anket - bir kullanıcı
     * @JoinColumn: Veritabanında creator_id kolonu ile bağlanır
     * fetch = FetchType.LAZY: İhtiyaç duyulduğunda yüklenir
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /**
     * Anket Soruları - Survey Questions
     * 
     * Bu ankete ait sorular
     * Questions belonging to this survey
     * 
     * @OneToMany: Bir anket - birden fazla soru
     * mappedBy = "survey": Question entity'sindeki survey alanı ile eşleşir
     * cascade = CascadeType.ALL: Anket silindiğinde soruları da silinir
     * orphanRemoval = true: Bağlantısı kesilen sorular otomatik silinir
     */
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("orderIndex ASC") // Sıra numarasına göre sıralar
    private List<Question> questions = new ArrayList<>();

    /**
     * Anket Yanıtları - Survey Responses
     * 
     * Bu ankete verilen yanıtlar
     * Responses given to this survey
     */
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // JSON çıktısında sonsuz döngü önlemek için
    private List<Response> responses = new ArrayList<>();

    /**
     * Başlangıç Tarihi - Start Date
     * 
     * Anketin yanıt almaya başlayacağı tarih (opsiyonel)
     * Date when survey starts accepting responses (optional)
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Bitiş Tarihi - End Date
     * 
     * Anketin yanıt almayı durduracağı tarih (opsiyonel)
     * Date when survey stops accepting responses (optional)
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Anonim Anket - Anonymous Survey
     * 
     * Anketin anonim olup olmadığını belirtir
     * Indicates if the survey is anonymous
     * 
     * true: Anonim (yanıtlayan kullanıcı bilgisi saklanmaz)
     * false: Anonim değil (yanıtlayan kullanıcı bilgisi saklanır)
     */
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = true;

    /**
     * Tekrar Yanıt Verme - Allow Multiple Responses
     * 
     * Aynı kullanıcının birden fazla yanıt verebilip veremeyeceğini belirtir
     * Indicates if same user can give multiple responses
     */
    @Column(name = "allow_multiple_responses", nullable = false)
    private Boolean allowMultipleResponses = false;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * JPA tarafından kullanılır
     */
    public Survey() {
    }

    /**
     * Parametreli Constructor - Parameterized Constructor
     * 
     * @param title Anket başlığı
     * @param description Anket açıklaması
     * @param creator Anket oluşturan kullanıcı
     */
    public Survey(String title, String description, User creator) {
        this.title = title;
        this.description = description;
        this.creator = creator;
        this.status = SurveyStatus.DRAFT;
        this.isAnonymous = true;
        this.allowMultipleResponses = false;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SurveyStatus getStatus() {
        return status;
    }

    public void setStatus(SurveyStatus status) {
        this.status = status;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Boolean getAllowMultipleResponses() {
        return allowMultipleResponses;
    }

    public void setAllowMultipleResponses(Boolean allowMultipleResponses) {
        this.allowMultipleResponses = allowMultipleResponses;
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * Ankete soru ekler - Adds question to survey
     * @param question Eklenecek soru
     */
    public void addQuestion(Question question) {
        questions.add(question);
        question.setSurvey(this);
        question.setOrderIndex(questions.size()); // Sıra numarasını ayarla
    }

    /**
     * Anketten soru çıkarır - Removes question from survey
     * @param question Çıkarılacak soru
     */
    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setSurvey(null);
        // Sıra numaralarını yeniden düzenle
        reorderQuestions();
    }

    /**
     * Soruların sıra numaralarını yeniden düzenler
     * Reorders question indices
     */
    private void reorderQuestions() {
        for (int i = 0; i < questions.size(); i++) {
            questions.get(i).setOrderIndex(i + 1);
        }
    }

    /**
     * Ankete yanıt ekler - Adds response to survey
     * @param response Eklenecek yanıt
     */
    public void addResponse(Response response) {
        responses.add(response);
        response.setSurvey(this);
    }

    /**
     * Toplam yanıt sayısını getirir - Returns total response count
     * @return Yanıt sayısı
     */
    public int getResponseCount() {
        return responses.size();
    }

    /**
     * Toplam soru sayısını getirir - Returns total question count
     * @return Soru sayısı
     */
    public int getQuestionCount() {
        return questions.size();
    }

    /**
     * Anketin aktif olup olmadığını kontrol eder
     * Checks if survey is active
     * @return true: aktif, false: aktif değil
     */
    public boolean isActive() {
        if (status != SurveyStatus.ACTIVE) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Başlangıç tarihi kontrolü
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        
        // Bitiş tarihi kontrolü
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        
        return true;
    }

    /**
     * Anketin taslak durumda olup olmadığını kontrol eder
     * Checks if survey is in draft status
     * @return true: taslak, false: taslak değil
     */
    public boolean isDraft() {
        return status == SurveyStatus.DRAFT;
    }

    /**
     * Anketin kapalı olup olmadığını kontrol eder
     * Checks if survey is closed
     * @return true: kapalı, false: açık
     */
    public boolean isClosed() {
        return status == SurveyStatus.CLOSED;
    }

    /**
     * Anketi aktif duruma getirir - Activates the survey
     */
    public void activate() {
        this.status = SurveyStatus.ACTIVE;
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
    }

    /**
     * Anketi kapatır - Closes the survey
     */
    public void close() {
        this.status = SurveyStatus.CLOSED;
        if (this.endDate == null) {
            this.endDate = LocalDateTime.now();
        }
    }

    /**
     * Kullanıcının bu ankete yanıt verip veremeyeceğini kontrol eder
     * Checks if user can respond to this survey
     * 
     * @param user Kontrol edilecek kullanıcı
     * @return true: yanıt verebilir, false: yanıt veremez
     */
    public boolean canUserRespond(User user) {
        if (!isActive()) {
            return false;
        }
        
        if (allowMultipleResponses) {
            return true;
        }
        
        // Kullanıcının daha önce yanıt verip vermediğini kontrol et
        if (user != null && !isAnonymous) {
            return responses.stream().noneMatch(response -> 
                response.getUser() != null && response.getUser().getId().equals(user.getId()));
        }
        
        return true;
    }

    /**
     * String temsili - String representation
     */
    @Override
    public String toString() {
        return "Survey{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", questionCount=" + getQuestionCount() +
                ", responseCount=" + getResponseCount() +
                '}';
    }
}
