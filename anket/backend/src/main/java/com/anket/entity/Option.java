package com.anket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Seçenek Entity Sınıfı - Option Entity Class
 * 
 * Sorulardaki seçenekleri temsil eder (A, B, C, D gibi)
 * Represents options in questions (like A, B, C, D)
 * 
 * Veritabanı Tablosu: options
 * Database Table: options
 * 
 * İlişkili Entity'ler - Related Entities:
 * - Question (Seçeneğin ait olduğu soru - Question that owns the option): Many-to-One
 * - Response (Bu seçeneği seçen yanıtlar - Responses that selected this option): One-to-Many
 * 
 * İlişkili Servisler - Related Services:
 * - OptionService: Seçenek işlemleri için
 * - QuestionService: Soru-seçenek ilişkileri için
 * - ResponseService: Yanıt-seçenek ilişkileri için
 */
@Entity
@Table(name = "options")
public class Option extends BaseEntity {

    /**
     * Seçenek Metni - Option Text
     * 
     * Seçeneğin içeriği (zorunlu alan)
     * Option content (required field)
     * 
     * @NotBlank: Boş olamaz
     * @Size: Minimum 1, maksimum 200 karakter
     * 
     * Örnek değerler - Example values:
     * - "Kesinlikle katılıyorum"
     * - "Evet"
     * - "18-25 yaş arası"
     */
    @NotBlank(message = "Seçenek metni boş olamaz - Option text cannot be empty")
    @Size(min = 1, max = 200, message = "Seçenek metni 1-200 karakter arasında olmalıdır - Option text must be between 1-200 characters")
    @Column(name = "option_text", nullable = false, length = 200)
    private String optionText;

    /**
     * Sıra Numarası - Order Index
     * 
     * Seçeneğin soru içindeki sırası
     * Order of option within the question
     * 
     * @NotNull: Null olamaz
     * Değer 1'den başlar (1=A, 2=B, 3=C, 4=D, ...)
     */
    @NotNull(message = "Sıra numarası boş olamaz - Order index cannot be null")
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    /**
     * Seçenek Etiketi - Option Label
     * 
     * Seçeneğin harfi (A, B, C, D gibi) - opsiyonel
     * Option letter (like A, B, C, D) - optional
     * 
     * Eğer boşsa, otomatik olarak sıra numarasına göre atanır
     * If empty, automatically assigned based on order index
     */
    @Size(max = 5, message = "Seçenek etiketi maksimum 5 karakter olabilir - Option label can be maximum 5 characters")
    @Column(name = "option_label", length = 5)
    private String optionLabel;

    /**
     * Doğru Cevap - Correct Answer
     * 
     * Bu seçeneğin doğru cevap olup olmadığını belirtir
     * Indicates whether this option is the correct answer
     * 
     * Quiz/test uygulamaları için kullanılabilir
     * Can be used for quiz/test applications
     * 
     * Şu an kullanılmıyor ama gelecekte eklenebilir
     * Currently not used but can be added in future
     */
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    /**
     * Ait Olduğu Soru - Parent Question
     * 
     * Bu seçeneğin ait olduğu soru
     * Question that this option belongs to
     * 
     * @ManyToOne: Birden fazla seçenek - bir soru
     * @JoinColumn: Veritabanında question_id kolonu ile bağlanır
     * fetch = FetchType.LAZY: İhtiyaç duyulduğunda yüklenir
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore // JSON çıktısında sonsuz döngü önlemek için
    private Question question;

    /**
     * Bu Seçeneği Seçen Yanıtlar - Responses That Selected This Option
     * 
     * Bu seçeneği işaretleyen tüm yanıtlar
     * All responses that marked this option
     * 
     * @OneToMany: Bir seçenek - birden fazla yanıt
     * mappedBy = "selectedOption": Response entity'sindeki selectedOption alanı ile eşleşir
     * cascade = CascadeType.ALL: Seçenek silindiğinde yanıtları da güncellenir
     */
    @OneToMany(mappedBy = "selectedOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // JSON çıktısında sonsuz döngü önlemek için
    private List<Response> responses = new ArrayList<>();

    /**
     * Açıklama - Description
     * 
     * Seçeneğe ek açıklama (opsiyonel)
     * Additional description for the option (optional)
     */
    @Size(max = 300, message = "Açıklama maksimum 300 karakter olabilir - Description can be maximum 300 characters")
    @Column(name = "description", length = 300)
    private String description;

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * JPA tarafından kullanılır
     */
    public Option() {
    }

    /**
     * Parametreli Constructor - Parameterized Constructor
     * 
     * @param optionText Seçenek metni
     * @param orderIndex Sıra numarası
     * @param question Ait olduğu soru
     */
    public Option(String optionText, Integer orderIndex, Question question) {
        this.optionText = optionText;
        this.orderIndex = orderIndex;
        this.question = question;
        this.isCorrect = false;
        this.optionLabel = generateLabel(orderIndex);
    }

    /**
     * Tam Parametreli Constructor - Full Parameterized Constructor
     * 
     * @param optionText Seçenek metni
     * @param orderIndex Sıra numarası
     * @param optionLabel Seçenek etiketi
     * @param question Ait olduğu soru
     */
    public Option(String optionText, Integer orderIndex, String optionLabel, Question question) {
        this.optionText = optionText;
        this.orderIndex = orderIndex;
        this.optionLabel = optionLabel;
        this.question = question;
        this.isCorrect = false;
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
        // Sıra numarası değiştiğinde etiketi güncelle
        if (this.optionLabel == null || this.optionLabel.isEmpty()) {
            this.optionLabel = generateLabel(orderIndex);
        }
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
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
     * Sıra numarasından etiket oluşturur - Generates label from order index
     * 
     * @param orderIndex Sıra numarası
     * @return Seçenek etiketi (A, B, C, D, ...)
     */
    private String generateLabel(Integer orderIndex) {
        if (orderIndex == null || orderIndex <= 0) {
            return "A";
        }
        
        // 1=A, 2=B, 3=C, ... şeklinde dönüştür
        if (orderIndex <= 26) {
            return String.valueOf((char) ('A' + orderIndex - 1));
        } else {
            // 26'dan sonra AA, AB, AC şeklinde devam eder
            int firstChar = (orderIndex - 1) / 26;
            int secondChar = (orderIndex - 1) % 26;
            return String.valueOf((char) ('A' + firstChar - 1)) + String.valueOf((char) ('A' + secondChar));
        }
    }

    /**
     * Seçeneğe yanıt ekler - Adds response to option
     * @param response Eklenecek yanıt
     */
    public void addResponse(Response response) {
        responses.add(response);
        response.setSelectedOption(this);
    }

    /**
     * Seçenekten yanıt çıkarır - Removes response from option
     * @param response Çıkarılacak yanıt
     */
    public void removeResponse(Response response) {
        responses.remove(response);
        response.setSelectedOption(null);
    }

    /**
     * Bu seçeneği kaç kişinin seçtiğini getirir - Returns how many people selected this option
     * @return Seçilme sayısı
     */
    public int getSelectionCount() {
        return responses.size();
    }

    /**
     * Bu seçeneğin yüzdelik oranını hesaplar - Calculates percentage of this option
     * 
     * @return Yüzdelik oran (0-100 arası)
     */
    public double getSelectionPercentage() {
        if (question == null) {
            return 0.0;
        }
        
        int totalResponses = question.getResponseCount();
        if (totalResponses == 0) {
            return 0.0;
        }
        
        return (double) getSelectionCount() / totalResponses * 100.0;
    }

    /**
     * Seçeneğin geçerli olup olmadığını kontrol eder
     * Validates the option
     * 
     * @return true: geçerli, false: geçersiz
     */
    public boolean isValid() {
        // Seçenek metni boş olmamalı
        if (optionText == null || optionText.trim().isEmpty()) {
            return false;
        }
        
        // Sıra numarası pozitif olmalı
        if (orderIndex == null || orderIndex <= 0) {
            return false;
        }
        
        // Soruya bağlı olmalı
        if (question == null) {
            return false;
        }
        
        return true;
    }

    /**
     * Seçeneğin düzenlenebilir olup olmadığını kontrol eder
     * Checks if option is editable
     * 
     * @return true: düzenlenebilir, false: düzenlenemez
     */
    public boolean isEditable() {
        if (question == null) {
            return true; // Soruya bağlı değilse düzenlenebilir
        }
        
        return question.isEditable(); // Sorunun düzenlenebilirlik durumuna bağlı
    }

    /**
     * Seçeneğin tam görüntüleme metnini getirir - Returns full display text of option
     * 
     * @return Etiket + Metin (örn: "A) Kesinlikle katılıyorum")
     */
    public String getFullDisplayText() {
        String label = optionLabel != null ? optionLabel : generateLabel(orderIndex);
        return label + ") " + optionText;
    }

    /**
     * Seçeneğin kısa görüntüleme metnini getirir - Returns short display text of option
     * 
     * @return Sadece etiket (örn: "A")
     */
    public String getShortDisplayText() {
        return optionLabel != null ? optionLabel : generateLabel(orderIndex);
    }

    /**
     * Seçeneğin doğru cevap olup olmadığını kontrol eder
     * Checks if option is the correct answer
     * 
     * @return true: doğru cevap, false: yanlış cevap
     */
    public boolean isCorrectAnswer() {
        return isCorrect != null && isCorrect;
    }

    /**
     * İstatistik bilgilerini getirir - Returns statistics information
     * 
     * @return İstatistik metni
     */
    public String getStatisticsText() {
        int count = getSelectionCount();
        double percentage = getSelectionPercentage();
        return String.format("%s: %d kişi (%.1f%%)", getShortDisplayText(), count, percentage);
    }

    /**
     * Karşılaştırma için kullanılır (sıra numarasına göre)
     * Used for comparison (by order index)
     * 
     * @param other Karşılaştırılacak seçenek
     * @return Karşılaştırma sonucu
     */
    public int compareTo(Option other) {
        if (other == null) {
            return 1;
        }
        return Integer.compare(this.orderIndex, other.orderIndex);
    }

    /**
     * String temsili - String representation
     */
    @Override
    public String toString() {
        return "Option{" +
                "id=" + getId() +
                ", optionText='" + optionText + '\'' +
                ", orderIndex=" + orderIndex +
                ", optionLabel='" + optionLabel + '\'' +
                ", isCorrect=" + isCorrect +
                ", selectionCount=" + getSelectionCount() +
                '}';
    }

    /**
     * Eşitlik kontrolü - Equality check
     * 
     * İki seçenek aynı soru içindeki aynı sıra numarasına sahipse eşittir
     * Two options are equal if they have same order index in same question
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Option option = (Option) obj;
        
        if (getId() != null && option.getId() != null) {
            return getId().equals(option.getId());
        }
        
        return orderIndex.equals(option.orderIndex) && 
               question != null && question.equals(option.question);
    }

    /**
     * Hash kodu - Hash code
     */
    @Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        
        int result = orderIndex.hashCode();
        result = 31 * result + (question != null ? question.hashCode() : 0);
        return result;
    }
}
