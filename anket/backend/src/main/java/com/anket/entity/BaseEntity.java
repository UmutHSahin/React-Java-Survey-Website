package com.anket.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Temel Entity Sınıfı - Base Entity Class
 * 
 * Tüm entity sınıflarının ortak özelliklerini içerir.
 * Contains common properties for all entity classes.
 * 
 * Bu sınıftan kalıtım alan sınıflar - Classes that inherit from this:
 * - User (Kullanıcı)
 * - Survey (Anket)
 * - Question (Soru)
 * - Option (Seçenek)
 * - Response (Yanıt)
 * 
 * @MappedSuperclass: Bu sınıfın veritabanında tablo oluşturmayacağını belirtir
 * @EntityListeners: Otomatik tarih damgalama için dinleyici ekler
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * Birincil Anahtar - Primary Key
     * 
     * Her entity için benzersiz kimlik numarası
     * Unique identifier for each entity
     * 
     * @GeneratedValue: Otomatik artan değer - Auto-incrementing value
     * @GenerationType.IDENTITY: Veritabanının otomatik artan özelliğini kullanır
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Oluşturulma Tarihi - Creation Date
     * 
     * Entity'nin veritabanına ilk kaydedildiği tarih
     * Date when the entity was first saved to database
     * 
     * @CreatedDate: Spring Data JPA tarafından otomatik olarak doldurulur
     * @Column(updatable = false): Bu alan güncellenmez
     */
    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    /**
     * Son Güncelleme Tarihi - Last Modified Date
     * 
     * Entity'nin son güncellendiği tarih
     * Date when the entity was last updated
     * 
     * @LastModifiedDate: Her güncelleme işleminde otomatik olarak güncellenir
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    /**
     * Aktif Durumu - Active Status
     * 
     * Entity'nin aktif olup olmadığını belirtir (soft delete için kullanılır)
     * Indicates whether the entity is active (used for soft delete)
     * 
     * true: Aktif (görünür)
     * false: Pasif (silinmiş gibi davranılır)
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Getter ve Setter metodları - Getter and Setter methods

    /**
     * ID değerini getirir - Returns the ID value
     * @return Entity'nin benzersiz kimlik numarası
     */
    public Long getId() {
        return id;
    }

    /**
     * ID değerini ayarlar - Sets the ID value
     * @param id Ayarlanacak ID değeri
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Oluşturulma tarihini getirir - Returns the creation date
     * @return Entity'nin oluşturulma tarihi
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Oluşturulma tarihini ayarlar - Sets the creation date
     * @param createdDate Ayarlanacak oluşturulma tarihi
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Son güncelleme tarihini getirir - Returns the last modified date
     * @return Entity'nin son güncelleme tarihi
     */
    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Son güncelleme tarihini ayarlar - Sets the last modified date
     * @param lastModifiedDate Ayarlanacak son güncelleme tarihi
     */
    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Aktif durumunu getirir - Returns the active status
     * @return Entity'nin aktif durumu
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Aktif durumunu ayarlar - Sets the active status
     * @param isActive Ayarlanacak aktif durumu
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * Entity'yi soft delete yapar - Performs soft delete on entity
     * 
     * Bu metod entity'yi veritabanından silmez, sadece isActive değerini false yapar
     * This method doesn't delete the entity from database, just sets isActive to false
     */
    public void softDelete() {
        this.isActive = false;
    }

    /**
     * Entity'yi tekrar aktif hale getirir - Reactivates the entity
     */
    public void activate() {
        this.isActive = true;
    }
}
