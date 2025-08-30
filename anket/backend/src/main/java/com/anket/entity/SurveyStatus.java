package com.anket.entity;

/**
 * Anket Durumu Enum'ı - Survey Status Enum
 * 
 * Anketlerin farklı durumlarını tanımlar
 * Defines different states of surveys
 * 
 * Bu enum şu sınıflarda kullanılır - This enum is used in:
 * - Survey entity: Anketin durumunu belirlemek için
 * - SurveyService: Durum değişiklikleri için
 * - Controllers: Filtreleme ve erişim kontrolü için
 */
public enum SurveyStatus {
    
    /**
     * Taslak - Draft
     * 
     * Anket henüz tamamlanmamış ve yayınlanmamış durumda
     * Survey is not yet completed and not published
     * 
     * Özellikler - Characteristics:
     * - Sadece oluşturan kullanıcı görebilir (Only creator can see)
     * - Düzenlenebilir (Can be edited)
     * - Yanıt alınamaz (Cannot receive responses)
     * - Soru ekleme/çıkarma yapılabilir (Questions can be added/removed)
     * - ACTIVE durumuna geçirilebilir (Can be moved to ACTIVE status)
     * 
     * Kullanım Senaryoları - Use Cases:
     * - Yeni oluşturulan anketler
     * - Henüz tamamlanmamış anketler
     * - Test aşamasındaki anketler
     */
    DRAFT("Taslak", "Survey is being prepared and not yet published"),
    
    /**
     * Aktif - Active
     * 
     * Anket yayınlanmış ve yanıt alıyor durumda
     * Survey is published and accepting responses
     * 
     * Özellikler - Characteristics:
     * - Herkese açık (görünürlük ayarına göre) (Public based on visibility settings)
     * - Yanıt alabilir (Can receive responses)
     * - Sınırlı düzenleme yapılabilir (Limited editing allowed)
     * - İstatistikler görüntülenebilir (Statistics can be viewed)
     * - CLOSED durumuna geçirilebilir (Can be moved to CLOSED status)
     * 
     * Kısıtlamalar - Restrictions:
     * - Soru silinemez (Questions cannot be deleted)
     * - Temel bilgiler değiştirilebilir (Basic info can be modified)
     * - Bitiş tarihi ayarlanabilir (End date can be set)
     * 
     * Kullanım Senaryoları - Use Cases:
     * - Canlı anketler
     * - Veri toplama sürecindeki anketler
     * - Paylaşılan anketler
     */
    ACTIVE("Aktif", "Survey is live and accepting responses"),
    
    /**
     * Kapalı - Closed
     * 
     * Anket kapatılmış ve artık yanıt almıyor
     * Survey is closed and no longer accepting responses
     * 
     * Özellikler - Characteristics:
     * - Görüntülenebilir ama yanıt verilemez (Can be viewed but no responses)
     * - İstatistikler görüntülenebilir (Statistics can be viewed)
     * - Düzenlenemez (Cannot be edited)
     * - Sonuçlar analiz edilebilir (Results can be analyzed)
     * - ACTIVE durumuna geri döndürülebilir (Can be reopened to ACTIVE)
     * 
     * Kullanım Senaryoları - Use Cases:
     * - Süresi dolmuş anketler
     * - Manuel olarak kapatılan anketler
     * - Yeterli yanıt alınan anketler
     * - Arşivlenen anketler
     */
    CLOSED("Kapalı", "Survey is closed and not accepting responses");

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
     * @param turkishName Türkçe durum adı
     * @param description İngilizce açıklama
     */
    SurveyStatus(String turkishName, String description) {
        this.turkishName = turkishName;
        this.description = description;
    }

    /**
     * Türkçe durum adını getirir - Returns Turkish status name
     * @return Durumun Türkçe karşılığı
     */
    public String getTurkishName() {
        return turkishName;
    }

    /**
     * İngilizce açıklamayı getirir - Returns English description
     * @return Durumun İngilizce açıklaması
     */
    public String getDescription() {
        return description;
    }

    /**
     * Durumun taslak olup olmadığını kontrol eder
     * Checks if the status is draft
     * @return true: taslak durumu, false: diğer durumlar
     */
    public boolean isDraft() {
        return this == DRAFT;
    }

    /**
     * Durumun aktif olup olmadığını kontrol eder
     * Checks if the status is active
     * @return true: aktif durumu, false: diğer durumlar
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Durumun kapalı olup olmadığını kontrol eder
     * Checks if the status is closed
     * @return true: kapalı durumu, false: diğer durumlar
     */
    public boolean isClosed() {
        return this == CLOSED;
    }

    /**
     * Durumun düzenlenebilir olup olmadığını kontrol eder
     * Checks if the status allows editing
     * @return true: düzenlenebilir, false: düzenlenemez
     */
    public boolean isEditable() {
        return this == DRAFT; // Sadece taslak durumda tam düzenleme yapılabilir
    }

    /**
     * Durumun yanıt kabul edip etmediğini kontrol eder
     * Checks if the status accepts responses
     * @return true: yanıt kabul eder, false: yanıt kabul etmez
     */
    public boolean acceptsResponses() {
        return this == ACTIVE;
    }

    /**
     * Geçerli durum geçişlerini kontrol eder
     * Validates status transitions
     * 
     * @param targetStatus Hedef durum
     * @return true: geçiş geçerli, false: geçiş geçersiz
     */
    public boolean canTransitionTo(SurveyStatus targetStatus) {
        switch (this) {
            case DRAFT:
                // Taslaktan aktif veya kapalı duruma geçilebilir
                return targetStatus == ACTIVE || targetStatus == CLOSED;
            case ACTIVE:
                // Aktiften kapalı duruma geçilebilir
                return targetStatus == CLOSED;
            case CLOSED:
                // Kapalıdan aktif duruma geri dönülebilir
                return targetStatus == ACTIVE;
            default:
                return false;
        }
    }

    /**
     * String'den SurveyStatus'e dönüştürür - Converts String to SurveyStatus
     * 
     * @param status String olarak durum adı
     * @return SurveyStatus enum değeri
     * @throws IllegalArgumentException Geçersiz durum adı için
     */
    public static SurveyStatus fromString(String status) {
        if (status == null || status.trim().isEmpty()) {
            return DRAFT; // Varsayılan olarak DRAFT durumu döndür
        }
        
        try {
            return SurveyStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Geçersiz anket durumu: " + status + 
                ". Geçerli durumlar: DRAFT, ACTIVE, CLOSED - Invalid survey status: " + status + 
                ". Valid statuses: DRAFT, ACTIVE, CLOSED");
        }
    }

    /**
     * Tüm durumların listesini getirir - Returns list of all statuses
     * Admin panelinde dropdown için kullanılabilir
     * Can be used for dropdown in admin panel
     * 
     * @return SurveyStatus array'i
     */
    public static SurveyStatus[] getAllStatuses() {
        return SurveyStatus.values();
    }

    /**
     * Aktif durumları getirir (yanıt alabilen durumlar)
     * Returns active statuses (statuses that can accept responses)
     * 
     * @return Aktif durumların array'i
     */
    public static SurveyStatus[] getActiveStatuses() {
        return new SurveyStatus[]{ACTIVE};
    }

    /**
     * Görüntülenebilir durumları getirir
     * Returns viewable statuses
     * 
     * @return Görüntülenebilir durumların array'i
     */
    public static SurveyStatus[] getViewableStatuses() {
        return new SurveyStatus[]{ACTIVE, CLOSED};
    }

    /**
     * CSS class adını getirir (frontend için)
     * Returns CSS class name (for frontend)
     * 
     * @return CSS class adı
     */
    public String getCssClass() {
        switch (this) {
            case DRAFT:
                return "status-draft";
            case ACTIVE:
                return "status-active";
            case CLOSED:
                return "status-closed";
            default:
                return "status-unknown";
        }
    }

    /**
     * Bootstrap badge class'ını getirir (frontend için)
     * Returns Bootstrap badge class (for frontend)
     * 
     * @return Bootstrap badge class
     */
    public String getBadgeClass() {
        switch (this) {
            case DRAFT:
                return "badge-secondary";
            case ACTIVE:
                return "badge-success";
            case CLOSED:
                return "badge-danger";
            default:
                return "badge-light";
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
