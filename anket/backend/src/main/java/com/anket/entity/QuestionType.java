package com.anket.entity;

/**
 * Soru Tipi Enum'ı - Question Type Enum
 * 
 * Anketlerdeki soru tiplerini tanımlar
 * Defines question types in surveys
 * 
 * Bu enum şu sınıflarda kullanılır - This enum is used in:
 * - Question entity: Sorunun tipini belirlemek için
 * - QuestionService: Soru işlemleri için
 * - Frontend: Farklı soru tiplerini render etmek için
 */
public enum QuestionType {
    
    /**
     * Çoktan Seçmeli - Multiple Choice
     * 
     * Kullanıcının önceden tanımlanmış seçeneklerden birini seçtiği soru tipi
     * Question type where user selects one option from predefined choices
     * 
     * Özellikler - Characteristics:
     * - Birden fazla seçenek (A, B, C, D gibi) (Multiple options like A, B, C, D)
     * - Sadece bir seçenek seçilebilir (Only one option can be selected)
     * - En az 2 seçenek gereklidir (Minimum 2 options required)
     * - Maksimum 10 seçenek önerilir (Maximum 10 options recommended)
     * 
     * Kullanım Alanları - Use Cases:
     * - Evet/Hayır soruları
     * - Demografik sorular (yaş grupları, eğitim durumu)
     * - Memnuniyet anketleri
     * - Test soruları
     * 
     * Örnek - Example:
     * Soru: "Bu hizmetten memnun musunuz?"
     * Seçenekler: A) Çok memnunum B) Memnunum C) Kararsızım D) Memnun değilim
     */
    MULTIPLE_CHOICE("Çoktan Seçmeli", "User selects one option from multiple predefined choices"),
    
    /**
     * Çoklu Seçim - Multiple Select (Gelecekte eklenebilir - Can be added in future)
     * 
     * Kullanıcının birden fazla seçeneği işaretleyebildiği soru tipi
     * Question type where user can select multiple options
     * 
     * Şu an desteklenmiyor ama gelecekte eklenebilir
     * Currently not supported but can be added in future
     */
    MULTIPLE_SELECT("Çoklu Seçim", "User can select multiple options from predefined choices"),
    
    /**
     * Açık Uçlu - Text Input (Gelecekte eklenebilir - Can be added in future)
     * 
     * Kullanıcının serbest metin girdiği soru tipi
     * Question type where user enters free text
     * 
     * Şu an desteklenmiyor ama gelecekte eklenebilir
     * Currently not supported but can be added in future
     */
    TEXT_INPUT("Açık Uçlu", "User enters free text response"),
    
    /**
     * Sayısal - Numeric Input (Gelecekte eklenebilir - Can be added in future)
     * 
     * Kullanıcının sayısal değer girdiği soru tipi
     * Question type where user enters numeric value
     * 
     * Şu an desteklenmiyor ama gelecekte eklenebilir
     * Currently not supported but can be added in future
     */
    NUMERIC_INPUT("Sayısal", "User enters numeric value"),
    
    /**
     * Derecelendirme - Rating Scale (Gelecekte eklenebilir - Can be added in future)
     * 
     * Kullanıcının 1-5 veya 1-10 arası puan verdiği soru tipi
     * Question type where user gives rating from 1-5 or 1-10
     * 
     * Şu an desteklenmiyor ama gelecekte eklenebilir
     * Currently not supported but can be added in future
     */
    RATING_SCALE("Derecelendirme", "User gives rating on a scale");

    /**
     * Türkçe Açıklama - Turkish Description
     * Kullanıcı arayüzünde gösterilmek için
     */
    private final String turkishName;
    
    /**
     * İngilizce Açıklama - English Description
     * API dokümantasyonu ve log mesajları için
     */
    private final String description;

    /**
     * Enum Constructor - Enum Yapıcısı
     * 
     * @param turkishName Türkçe soru tipi adı
     * @param description İngilizce açıklama
     */
    QuestionType(String turkishName, String description) {
        this.turkishName = turkishName;
        this.description = description;
    }

    /**
     * Türkçe soru tipi adını getirir - Returns Turkish question type name
     * @return Soru tipinin Türkçe karşılığı
     */
    public String getTurkishName() {
        return turkishName;
    }

    /**
     * İngilizce açıklamayı getirir - Returns English description
     * @return Soru tipinin İngilizce açıklaması
     */
    public String getDescription() {
        return description;
    }

    /**
     * Soru tipinin çoktan seçmeli olup olmadığını kontrol eder
     * Checks if the question type is multiple choice
     * @return true: çoktan seçmeli, false: diğer tipler
     */
    public boolean isMultipleChoice() {
        return this == MULTIPLE_CHOICE;
    }

    /**
     * Soru tipinin çoklu seçim olup olmadığını kontrol eder
     * Checks if the question type is multiple select
     * @return true: çoklu seçim, false: diğer tipler
     */
    public boolean isMultipleSelect() {
        return this == MULTIPLE_SELECT;
    }

    /**
     * Soru tipinin metin girişi olup olmadığını kontrol eder
     * Checks if the question type is text input
     * @return true: metin girişi, false: diğer tipler
     */
    public boolean isTextInput() {
        return this == TEXT_INPUT;
    }

    /**
     * Soru tipinin sayısal giriş olup olmadığını kontrol eder
     * Checks if the question type is numeric input
     * @return true: sayısal giriş, false: diğer tipler
     */
    public boolean isNumericInput() {
        return this == NUMERIC_INPUT;
    }

    /**
     * Soru tipinin derecelendirme olup olmadığını kontrol eder
     * Checks if the question type is rating scale
     * @return true: derecelendirme, false: diğer tipler
     */
    public boolean isRatingScale() {
        return this == RATING_SCALE;
    }

    /**
     * Soru tipinin seçenekler gerektirip gerektirmediğini kontrol eder
     * Checks if the question type requires options
     * @return true: seçenek gerekli, false: seçenek gerekmez
     */
    public boolean requiresOptions() {
        return this == MULTIPLE_CHOICE || this == MULTIPLE_SELECT;
    }

    /**
     * Soru tipinin şu an desteklenip desteklenmediğini kontrol eder
     * Checks if the question type is currently supported
     * @return true: destekleniyor, false: desteklenmiyor
     */
    public boolean isSupported() {
        return this == MULTIPLE_CHOICE; // Şu an sadece çoktan seçmeli destekleniyor
    }

    /**
     * Minimum seçenek sayısını getirir - Returns minimum option count
     * @return Minimum seçenek sayısı
     */
    public int getMinimumOptionCount() {
        switch (this) {
            case MULTIPLE_CHOICE:
            case MULTIPLE_SELECT:
                return 2; // En az 2 seçenek
            default:
                return 0; // Diğer tipler seçenek gerektirmez
        }
    }

    /**
     * Maksimum seçenek sayısını getirir - Returns maximum option count
     * @return Maksimum seçenek sayısı
     */
    public int getMaximumOptionCount() {
        switch (this) {
            case MULTIPLE_CHOICE:
            case MULTIPLE_SELECT:
                return 10; // Maksimum 10 seçenek
            default:
                return 0; // Diğer tipler seçenek gerektirmez
        }
    }

    /**
     * String'den QuestionType'a dönüştürür - Converts String to QuestionType
     * 
     * @param type String olarak soru tipi adı
     * @return QuestionType enum değeri
     * @throws IllegalArgumentException Geçersiz soru tipi için
     */
    public static QuestionType fromString(String type) {
        if (type == null || type.trim().isEmpty()) {
            return MULTIPLE_CHOICE; // Varsayılan olarak çoktan seçmeli döndür
        }
        
        try {
            return QuestionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Geçersiz soru tipi: " + type + 
                ". Geçerli tipler: MULTIPLE_CHOICE, MULTIPLE_SELECT, TEXT_INPUT, NUMERIC_INPUT, RATING_SCALE - " +
                "Invalid question type: " + type + 
                ". Valid types: MULTIPLE_CHOICE, MULTIPLE_SELECT, TEXT_INPUT, NUMERIC_INPUT, RATING_SCALE");
        }
    }

    /**
     * Tüm soru tiplerinin listesini getirir - Returns list of all question types
     * @return QuestionType array'i
     */
    public static QuestionType[] getAllTypes() {
        return QuestionType.values();
    }

    /**
     * Desteklenen soru tiplerinin listesini getirir - Returns list of supported question types
     * @return Desteklenen QuestionType array'i
     */
    public static QuestionType[] getSupportedTypes() {
        return new QuestionType[]{MULTIPLE_CHOICE}; // Şu an sadece çoktan seçmeli
    }

    /**
     * Seçenek gerektiren soru tiplerinin listesini getirir
     * Returns list of question types that require options
     * @return Seçenek gerektiren QuestionType array'i
     */
    public static QuestionType[] getTypesRequiringOptions() {
        return new QuestionType[]{MULTIPLE_CHOICE, MULTIPLE_SELECT};
    }

    /**
     * HTML input tipini getirir (frontend için)
     * Returns HTML input type (for frontend)
     * @return HTML input type
     */
    public String getHtmlInputType() {
        switch (this) {
            case MULTIPLE_CHOICE:
                return "radio";
            case MULTIPLE_SELECT:
                return "checkbox";
            case TEXT_INPUT:
                return "text";
            case NUMERIC_INPUT:
                return "number";
            case RATING_SCALE:
                return "range";
            default:
                return "text";
        }
    }

    /**
     * CSS class adını getirir (frontend için)
     * Returns CSS class name (for frontend)
     * @return CSS class adı
     */
    public String getCssClass() {
        switch (this) {
            case MULTIPLE_CHOICE:
                return "question-multiple-choice";
            case MULTIPLE_SELECT:
                return "question-multiple-select";
            case TEXT_INPUT:
                return "question-text-input";
            case NUMERIC_INPUT:
                return "question-numeric-input";
            case RATING_SCALE:
                return "question-rating-scale";
            default:
                return "question-unknown";
        }
    }

    /**
     * String temsili - String representation
     * @return Enum adı ve Türkçe karşılığı
     */
    @Override
    public String toString() {
        return name() + " (" + turkishName + ")";
    }
}
