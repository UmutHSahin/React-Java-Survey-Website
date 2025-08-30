package com.anket.entity;

/**
 * Kullanıcı Rol Enum'ı - User Role Enum
 * 
 * Sistemdeki kullanıcı rollerini tanımlar
 * Defines user roles in the system
 * 
 * Bu enum şu sınıflarda kullanılır - This enum is used in:
 * - User entity: Kullanıcının rolünü belirlemek için
 * - SecurityConfig: Yetkilendirme kontrolü için
 * - Controllers: Erişim kontrolü için
 */
public enum UserRole {
    
    /**
     * Normal Kullanıcı - Regular User
     * 
     * Yetkiler - Permissions:
     * - Anket oluşturabilir (Can create surveys)
     * - Anket yanıtlayabilir (Can respond to surveys)
     * - Kendi anketlerini görüntüleyebilir (Can view own surveys)
     * - Kendi profilini düzenleyebilir (Can edit own profile)
     * 
     * Kısıtlamalar - Restrictions:
     * - Başkalarının anketlerini silemez (Cannot delete others' surveys)
     * - Admin paneline erişemez (Cannot access admin panel)
     * - Sistem ayarlarını değiştiremez (Cannot change system settings)
     */
    USER("Kullanıcı", "Regular user with basic permissions"),
    
    /**
     * Yönetici - Administrator
     * 
     * Yetkiler - Permissions:
     * - Tüm USER yetkilerine sahiptir (Has all USER permissions)
     * - Tüm anketleri görüntüleyebilir (Can view all surveys)
     * - Herhangi bir anketi silebilir (Can delete any survey)
     * - Admin paneline erişebilir (Can access admin panel)
     * - Kullanıcı listesini görüntüleyebilir (Can view user list)
     * - Sistem istatistiklerini görebilir (Can view system statistics)
     * - Diğer kullanıcıları yönetebilir (Can manage other users)
     * 
     * Önemli Not - Important Note:
     * Admin rolü dikkatli kullanılmalıdır, güvenlik riski oluşturabilir
     * Admin role should be used carefully, may create security risks
     */
    ADMIN("Yönetici", "Administrator with full system access");

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
     * @param turkishName Türkçe rol adı
     * @param description İngilizce açıklama
     */
    UserRole(String turkishName, String description) {
        this.turkishName = turkishName;
        this.description = description;
    }

    /**
     * Türkçe rol adını getirir - Returns Turkish role name
     * @return Rol adının Türkçe karşılığı
     */
    public String getTurkishName() {
        return turkishName;
    }

    /**
     * İngilizce açıklamayı getirir - Returns English description
     * @return Rolün İngilizce açıklaması
     */
    public String getDescription() {
        return description;
    }

    /**
     * Rolün admin olup olmadığını kontrol eder
     * Checks if the role is admin
     * @return true: admin rolü, false: user rolü
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Rolün user olup olmadığını kontrol eder
     * Checks if the role is user
     * @return true: user rolü, false: admin rolü
     */
    public boolean isUser() {
        return this == USER;
    }

    /**
     * String'den UserRole'e dönüştürür - Converts String to UserRole
     * 
     * Bu metod API'den gelen string değerleri enum'a çevirmek için kullanılır
     * This method is used to convert string values from API to enum
     * 
     * @param role String olarak rol adı
     * @return UserRole enum değeri
     * @throws IllegalArgumentException Geçersiz rol adı için
     */
    public static UserRole fromString(String role) {
        if (role == null || role.trim().isEmpty()) {
            return USER; // Varsayılan olarak USER rolü döndür
        }
        
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Geçersiz rol: " + role + 
                ". Geçerli roller: USER, ADMIN - Invalid role: " + role + 
                ". Valid roles: USER, ADMIN");
        }
    }

    /**
     * Tüm rollerin listesini getirir - Returns list of all roles
     * Admin panelinde dropdown için kullanılabilir
     * Can be used for dropdown in admin panel
     * 
     * @return UserRole array'i
     */
    public static UserRole[] getAllRoles() {
        return UserRole.values();
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
