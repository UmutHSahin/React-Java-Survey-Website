package com.anket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Yanıt Entity Sınıfı - Response Entity Class
 * 
 * Kullanıcıların anket sorularına verdiği yanıtları temsil eder
 * Represents user responses to survey questions
 * 
 * Veritabanı Tablosu: responses
 * Database Table: responses
 * 
 * İlişkili Entity'ler - Related Entities:
 * - User (Yanıt veren kullanıcı - User who gave the response): Many-to-One
 * - Survey (Yanıtlanan anket - Survey that was responded): Many-to-One
 * - Question (Yanıtlanan soru - Question that was answered): Many-to-One
 * - Option (Seçilen seçenek - Selected option): Many-to-One
 * 
 * İlişkili Servisler - Related Services:
 * - ResponseService: Yanıt işlemleri için
 * - SurveyService: Anket-yanıt ilişkileri için
 * - StatisticsService: İstatistik hesaplamaları için
 */
@Entity
@Table(name = "responses")
public class Response extends BaseEntity {

    /**
     * Yanıt Veren Kullanıcı - User Who Responded
     * 
     * Bu yanıtı veren kullanıcı (anonim anketlerde null olabilir)
     * User who gave this response (can be null for anonymous surveys)
     * 
     * @ManyToOne: Birden fazla yanıt - bir kullanıcı
     * @JoinColumn: Veritabanında user_id kolonu ile bağlanır
     * fetch = FetchType.LAZY: İhtiyaç duyulduğunda yüklenir
     * 
     * Anonim anketlerde bu alan null'dır
     * This field is null for anonymous surveys
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Yanıtlanan Anket - Survey That Was Responded
     * 
     * Bu yanıtın ait olduğu anket
     * Survey that this response belongs to
     * 
     * @ManyToOne: Birden fazla yanıt - bir anket
     * @JoinColumn: Veritabanında survey_id kolonu ile bağlanır
     * @NotNull: Her yanıt mutlaka bir ankete ait olmalı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @NotNull(message = "Anket alanı boş olamaz - Survey cannot be null")
    @JsonIgnore // JSON çıktısında sonsuz döngü önlemek için
    private Survey survey;

    /**
     * Yanıtlanan Soru - Question That Was Answered
     * 
     * Bu yanıtın ait olduğu soru
     * Question that this response belongs to
     * 
     * @ManyToOne: Birden fazla yanıt - bir soru
     * @JoinColumn: Veritabanında question_id kolonu ile bağlanır
     * @NotNull: Her yanıt mutlaka bir soruya ait olmalı
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "Soru alanı boş olamaz - Question cannot be null")
    private Question question;

    /**
     * Seçilen Seçenek - Selected Option
     * 
     * Çoktan seçmeli sorularda seçilen seçenek
     * Selected option for multiple choice questions
     * 
     * @ManyToOne: Birden fazla yanıt - bir seçenek
     * @JoinColumn: Veritabanında selected_option_id kolonu ile bağlanır
     * 
     * Çoktan seçmeli sorular için zorunlu
     * Required for multiple choice questions
     * Diğer soru tipleri için null olabilir (gelecekte)
     * Can be null for other question types (in future)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private Option selectedOption;

    /**
     * Metin Yanıtı - Text Response
     * 
     * Açık uçlu sorular için metin yanıtı (gelecekte kullanılabilir)
     * Text response for open-ended questions (can be used in future)
     * 
     * Şu an kullanılmıyor ama gelecekte eklenebilir
     * Currently not used but can be added in future
     */
    @Column(name = "text_response", length = 1000)
    private String textResponse;

    /**
     * Sayısal Yanıt - Numeric Response
     * 
     * Sayısal sorular için sayısal yanıt (gelecekte kullanılabilir)
     * Numeric response for numeric questions (can be used in future)
     * 
     * Şu an kullanılmıyor ama gelecekte eklenebilir
     * Currently not used but can be added in future
     */
    @Column(name = "numeric_response")
    private Double numericResponse;

    /**
     * Yanıt Tarihi - Response Date
     * 
     * Yanıtın verildiği tarih ve saat
     * Date and time when response was given
     * 
     * Otomatik olarak oluşturulma anında ayarlanır
     * Automatically set when response is created
     */
    @Column(name = "response_date", nullable = false)
    private LocalDateTime responseDate;

    /**
     * IP Adresi - IP Address
     * 
     * Yanıtı veren kullanıcının IP adresi (güvenlik ve analiz için)
     * IP address of the user who gave response (for security and analysis)
     * 
     * Opsiyonel alan - Optional field
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * User Agent - User Agent
     * 
     * Yanıtı veren kullanıcının tarayıcı bilgisi (analiz için)
     * Browser information of the user who gave response (for analysis)
     * 
     * Opsiyonel alan - Optional field
     */
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    /**
     * Oturum ID'si - Session ID
     * 
     * Anonim kullanıcıları takip etmek için oturum kimliği
     * Session identifier to track anonymous users
     * 
     * Anonim anketlerde aynı kullanıcının birden fazla yanıt vermesini önlemek için
     * Used to prevent same anonymous user from responding multiple times
     */
    @Column(name = "session_id", length = 100)
    private String sessionId;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * JPA tarafından kullanılır
     */
    public Response() {
        this.responseDate = LocalDateTime.now();
    }

    /**
     * Kayıtlı Kullanıcı İçin Constructor - Constructor for Registered User
     * 
     * @param user Yanıt veren kullanıcı
     * @param survey Yanıtlanan anket
     * @param question Yanıtlanan soru
     * @param selectedOption Seçilen seçenek
     */
    public Response(User user, Survey survey, Question question, Option selectedOption) {
        this();
        this.user = user;
        this.survey = survey;
        this.question = question;
        this.selectedOption = selectedOption;
    }

    /**
     * Anonim Kullanıcı İçin Constructor - Constructor for Anonymous User
     * 
     * @param survey Yanıtlanan anket
     * @param question Yanıtlanan soru
     * @param selectedOption Seçilen seçenek
     * @param sessionId Oturum ID'si
     */
    public Response(Survey survey, Question question, Option selectedOption, String sessionId) {
        this();
        this.survey = survey;
        this.question = question;
        this.selectedOption = selectedOption;
        this.sessionId = sessionId;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Option getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(Option selectedOption) {
        this.selectedOption = selectedOption;
    }

    public String getTextResponse() {
        return textResponse;
    }

    public void setTextResponse(String textResponse) {
        this.textResponse = textResponse;
    }

    public Double getNumericResponse() {
        return numericResponse;
    }

    public void setNumericResponse(Double numericResponse) {
        this.numericResponse = numericResponse;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * Yanıtın anonim olup olmadığını kontrol eder
     * Checks if response is anonymous
     * 
     * @return true: anonim yanıt, false: kayıtlı kullanıcı yanıtı
     */
    public boolean isAnonymous() {
        return user == null;
    }

    /**
     * Yanıtın geçerli olup olmadığını kontrol eder
     * Validates the response
     * 
     * @return true: geçerli, false: geçersiz
     */
    public boolean isValid() {
        // Anket ve soru zorunlu
        if (survey == null || question == null) {
            return false;
        }
        
        // Çoktan seçmeli sorular için seçenek zorunlu
        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            if (selectedOption == null) {
                return false;
            }
            
            // Seçilen seçenek bu soruya ait olmalı
            if (!selectedOption.getQuestion().getId().equals(question.getId())) {
                return false;
            }
        }
        
        // Anonim anketlerde oturum ID'si olmalı
        if (survey.getIsAnonymous() && user == null) {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Yanıtın düzenlenebilir olup olmadığını kontrol eder
     * Checks if response is editable
     * 
     * @return true: düzenlenebilir, false: düzenlenemez
     */
    public boolean isEditable() {
        // Anket kapalıysa yanıt düzenlenemez
        if (survey == null || survey.getStatus() == SurveyStatus.CLOSED) {
            return false;
        }
        
        // Yanıt verildiğinden itibaren belirli bir süre geçtiyse düzenlenemez
        // (şu an 24 saat olarak ayarlandı)
        if (responseDate != null) {
            LocalDateTime editDeadline = responseDate.plusHours(24);
            if (LocalDateTime.now().isAfter(editDeadline)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Yanıt değerini string olarak getirir - Returns response value as string
     * 
     * @return Yanıt değeri
     */
    public String getResponseValue() {
        if (selectedOption != null) {
            return selectedOption.getOptionText();
        } else if (textResponse != null) {
            return textResponse;
        } else if (numericResponse != null) {
            return numericResponse.toString();
        } else {
            return "Yanıt verilmemiş - No response given";
        }
    }

    /**
     * Yanıt etiketini getirir - Returns response label
     * 
     * @return Yanıt etiketi (A, B, C, D gibi)
     */
    public String getResponseLabel() {
        if (selectedOption != null) {
            return selectedOption.getShortDisplayText();
        } else {
            return "-";
        }
    }

    /**
     * Yanıt veren kullanıcının adını getirir - Returns name of responding user
     * 
     * @return Kullanıcı adı veya "Anonim"
     */
    public String getResponderName() {
        if (user != null) {
            return user.getFullName();
        } else {
            return "Anonim Kullanıcı - Anonymous User";
        }
    }

    /**
     * Yanıt veren kullanıcının e-posta adresini getirir
     * Returns email of responding user
     * 
     * @return E-posta adresi veya "Anonim"
     */
    public String getResponderEmail() {
        if (user != null) {
            return user.getEmail();
        } else {
            return "Anonim - Anonymous";
        }
    }

    /**
     * Yanıtın özet bilgisini getirir - Returns summary of response
     * 
     * @return Özet bilgi
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Soru: ").append(question.getQuestionText()).append(" | ");
        summary.append("Yanıt: ").append(getResponseValue()).append(" | ");
        summary.append("Tarih: ").append(responseDate.toString());
        
        if (!isAnonymous()) {
            summary.append(" | Kullanıcı: ").append(user.getFullName());
        } else {
            summary.append(" | Anonim");
        }
        
        return summary.toString();
    }

    /**
     * Yanıtın ait olduğu anketin başlığını getirir
     * Returns title of survey this response belongs to
     * 
     * @return Anket başlığı
     */
    public String getSurveyTitle() {
        return survey != null ? survey.getTitle() : "Bilinmeyen Anket - Unknown Survey";
    }

    /**
     * Yanıtın ait olduğu sorunun metnini getirir
     * Returns text of question this response belongs to
     * 
     * @return Soru metni
     */
    public String getQuestionText() {
        return question != null ? question.getQuestionText() : "Bilinmeyen Soru - Unknown Question";
    }

    /**
     * Yanıtın verildiği tarihten bu yana geçen süreyi hesaplar
     * Calculates time elapsed since response was given
     * 
     * @return Geçen süre açıklaması
     */
    public String getTimeElapsed() {
        if (responseDate == null) {
            return "Bilinmeyen - Unknown";
        }
        
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(responseDate, now).toMinutes();
        
        if (minutes < 1) {
            return "Az önce - Just now";
        } else if (minutes < 60) {
            return minutes + " dakika önce - minutes ago";
        } else if (minutes < 1440) { // 24 saat
            long hours = minutes / 60;
            return hours + " saat önce - hours ago";
        } else {
            long days = minutes / 1440;
            return days + " gün önce - days ago";
        }
    }

    /**
     * String temsili - String representation
     */
    @Override
    public String toString() {
        return "Response{" +
                "id=" + getId() +
                ", user=" + (user != null ? user.getEmail() : "Anonymous") +
                ", survey=" + (survey != null ? survey.getTitle() : "Unknown") +
                ", question=" + (question != null ? question.getId() : "Unknown") +
                ", selectedOption=" + (selectedOption != null ? selectedOption.getOptionText() : "None") +
                ", responseDate=" + responseDate +
                '}';
    }

    /**
     * Eşitlik kontrolü - Equality check
     * 
     * İki yanıt aynı kullanıcı, anket ve soru kombinasyonuna sahipse eşittir
     * Two responses are equal if they have same user, survey and question combination
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Response response = (Response) obj;
        
        if (getId() != null && response.getId() != null) {
            return getId().equals(response.getId());
        }
        
        // Aynı kullanıcı, anket ve soru kombinasyonu
        boolean sameUser = (user == null && response.user == null) || 
                          (user != null && user.equals(response.user));
        boolean sameSurvey = survey != null && survey.equals(response.survey);
        boolean sameQuestion = question != null && question.equals(response.question);
        
        return sameUser && sameSurvey && sameQuestion;
    }

    /**
     * Hash kodu - Hash code
     */
    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        
        int result = user != null ? user.hashCode() : 0;
        result = 31 * result + (survey != null ? survey.hashCode() : 0);
        result = 31 * result + (question != null ? question.hashCode() : 0);
        return result;
    }
}
