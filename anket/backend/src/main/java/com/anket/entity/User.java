package com.anket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Kullanıcı Entity Sınıfı - User Entity Class
 * 
 * Sistemdeki kullanıcıları temsil eder (normal kullanıcılar ve adminler)
 * Represents users in the system (regular users and admins)
 * 
 * Veritabanı Tablosu: users
 * Database Table: users
 * 
 * İlişkili Entity'ler - Related Entities:
 * - Survey (Bir kullanıcı birden fazla anket oluşturabilir - One user can create multiple surveys)
 * - Response (Bir kullanıcı birden fazla yanıt verebilir - One user can give multiple responses)
 * 
 * İlişkili Servisler - Related Services:
 * - UserService: Kullanıcı işlemleri için
 * - AuthService: Kimlik doğrulama işlemleri için
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    /**
     * Kullanıcı Adı - First Name
     * 
     * Kullanıcının adı (zorunlu alan)
     * User's first name (required field)
     * 
     * @NotBlank: Boş olamaz
     * @Size: Minimum 2, maksimum 50 karakter
     */
    @NotBlank(message = "Ad alanı boş olamaz - First name cannot be empty")
    @Size(min = 2, max = 50, message = "Ad 2-50 karakter arasında olmalıdır - First name must be between 2-50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * Kullanıcı Soyadı - Last Name
     * 
     * Kullanıcının soyadı (zorunlu alan)
     * User's last name (required field)
     */
    @NotBlank(message = "Soyad alanı boş olamaz - Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Soyad 2-50 karakter arasında olmalıdır - Last name must be between 2-50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * E-posta Adresi - Email Address
     * 
     * Kullanıcının benzersiz e-posta adresi (giriş için kullanılır)
     * User's unique email address (used for login)
     * 
     * @Email: Geçerli e-posta formatında olmalı
     * unique = true: Aynı e-posta ile birden fazla hesap açılamaz
     */
    @NotBlank(message = "E-posta alanı boş olamaz - Email cannot be empty")
    @Email(message = "Geçerli bir e-posta adresi giriniz - Please enter a valid email address")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Şifre - Password
     * 
     * Kullanıcının şifresi (hashlenmiş halde saklanır)
     * User's password (stored in hashed format)
     * 
     * @JsonIgnore: JSON çıktısında gösterilmez (güvenlik için)
     */
    @NotBlank(message = "Şifre alanı boş olamaz - Password cannot be empty")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır - Password must be at least 6 characters")
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * Kullanıcı Rolü - User Role
     * 
     * Kullanıcının sistemdeki yetkisini belirler
     * Determines user's authority in the system
     * 
     * Değerler - Values:
     * - USER: Normal kullanıcı (anket oluşturabilir, yanıtlayabilir)
     * - ADMIN: Yönetici (tüm anketleri görebilir, silebilir)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    /**
     * Kullanıcının Oluşturduğu Anketler - Surveys Created by User
     * 
     * Bir kullanıcı birden fazla anket oluşturabilir (One-to-Many ilişki)
     * One user can create multiple surveys (One-to-Many relationship)
     * 
     * @OneToMany: Bir kullanıcı - birden fazla anket
     * mappedBy = "creator": Survey entity'sindeki creator alanı ile eşleşir
     * cascade = CascadeType.ALL: Kullanıcı silindiğinde anketleri de silinir
     * fetch = FetchType.LAZY: İhtiyaç duyulduğunda yüklenir (performans için)
     */
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // JSON çıktısında sonsuz döngü önlemek için
    private List<Survey> createdSurveys = new ArrayList<>();

    /**
     * Kullanıcının Verdiği Yanıtlar - Responses Given by User
     * 
     * Bir kullanıcı birden fazla ankete yanıt verebilir
     * One user can respond to multiple surveys
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Response> responses = new ArrayList<>();

    // Constructors - Yapıcı Metodlar

    /**
     * Varsayılan Constructor - Default Constructor
     * JPA tarafından kullanılır
     */
    public User() {
    }

    /**
     * Parametreli Constructor - Parameterized Constructor
     * 
     * Yeni kullanıcı oluştururken kullanılır
     * Used when creating new users
     * 
     * @param firstName Kullanıcının adı
     * @param lastName Kullanıcının soyadı
     * @param email Kullanıcının e-posta adresi
     * @param password Kullanıcının şifresi
     */
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = UserRole.USER; // Varsayılan olarak normal kullanıcı
    }

    // Getter ve Setter Metodları - Getter and Setter Methods

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<Survey> getCreatedSurveys() {
        return createdSurveys;
    }

    public void setCreatedSurveys(List<Survey> createdSurveys) {
        this.createdSurveys = createdSurveys;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    // Yardımcı Metodlar - Helper Methods

    /**
     * Kullanıcının tam adını getirir - Returns user's full name
     * @return Ad + Soyad
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Kullanıcının admin olup olmadığını kontrol eder
     * Checks if user is admin
     * @return true: admin, false: normal kullanıcı
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    /**
     * Kullanıcıya anket ekler - Adds survey to user
     * @param survey Eklenecek anket
     */
    public void addSurvey(Survey survey) {
        createdSurveys.add(survey);
        survey.setCreator(this);
    }

    /**
     * Kullanıcıdan anket çıkarır - Removes survey from user
     * @param survey Çıkarılacak anket
     */
    public void removeSurvey(Survey survey) {
        createdSurveys.remove(survey);
        survey.setCreator(null);
    }

    /**
     * Kullanıcıya yanıt ekler - Adds response to user
     * @param response Eklenecek yanıt
     */
    public void addResponse(Response response) {
        responses.add(response);
        response.setUser(this);
    }

    /**
     * String temsili - String representation
     * Debug ve log işlemleri için kullanılır
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
