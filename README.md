# React-Java-Survey-Website

# 🗂️ Smart Survey Application

<div align="center">

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.x-blue.svg)](https://reactjs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## 🌟 **Project Overview**

A modern, full-stack survey application that enables organizations and individuals to create, distribute, and analyze surveys efficiently. Built with enterprise-grade technologies, this application provides a seamless experience for survey creators and respondents while offering powerful analytics and administrative capabilities.

**Key Highlights:**
- Secure user authentication and role-based access control
- Intuitive survey creation with multiple question types
- Real-time response tracking and analytics
- Responsive design for desktop and mobile devices
- Comprehensive admin dashboard for system management

---

## ✨ **Core Features**

### 🔐 **Authentication & Security**
- **User Registration & Login**: Secure account creation with email verification
- **JWT Token Authentication**: Stateless, secure session management
- **Role-Based Access Control**: Distinct permissions for users and administrators
- **Password Security**: BCrypt encryption for sensitive data protection

### 📊 **Survey Management**
- **Dynamic Survey Creation**: Build surveys with unlimited multiple-choice questions
- **Category Organization**: Organize surveys by topics for better discoverability
- **Status Management**: Control survey availability (Active/Inactive/Draft)
- **Edit & Delete**: Full CRUD operations for survey management

### 📝 **Response System**
- **Anonymous Responses**: Privacy-focused survey participation
- **Real-time Tracking**: Live response monitoring and progress updates
- **Completion Validation**: Ensure data integrity with response validation
- **User History**: Track participation history for registered users

### 📈 **Analytics Dashboard**
- **Response Statistics**: Detailed breakdown of survey participation
- **Visual Charts**: Graphical representation of survey results
- **Completion Rates**: Monitor survey performance metrics
- **Export Capabilities**: Download results in various formats

### 👑 **Administrative Panel**
- **System Overview**: Comprehensive dashboard for system monitoring
- **User Management**: View and manage user accounts and activity
- **Survey Moderation**: Oversee all surveys across the platform
- **Performance Metrics**: Monitor application health and usage statistics

---

## 🏗️ **Technical Architecture**

### **Backend Stack**
- **Spring Boot 3.x**: Production-ready application framework
- **Java 17+**: Modern Java with enhanced performance and features
- **PostgreSQL**: Robust relational database with advanced features
- **Spring Data JPA**: Simplified data access with Hibernate ORM
- **Spring Security**: Comprehensive security framework
- **Maven**: Dependency management and build automation

### **Frontend Stack**
- **React 18**: Modern UI library with latest features
- **Vite**: Lightning-fast build tool and development server
- **React Router**: Declarative routing for single-page application
- **Modern CSS**: Responsive design with Flexbox and Grid
- **Fetch API**: HTTP client for seamless backend communication

### **Database Schema**
<pre>
Users
├── Authentication & Profile Management
│
Surveys
├── Survey Metadata & Configuration
│
Questions
├── Question Content & Types
│
Options
├── Multiple Choice Options
│
Responses
└── User Response Data & Analytics
</pre>

---

## 🚀 **Quick Start Guide**

### **Prerequisites**
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 14+
- Node.js 16+
- npm or yarn

### **Installation Steps**

1. **Clone the Repository**
```bash
git clone https://github.com/yourusername/smart-survey-app.git
cd smart-survey-app
```

2. **Backend Setup**
```bash
cd backend
mvn clean install
```

3. **Database Configuration**
```sql
CREATE DATABASE survey_app;
```

Configure `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/survey_app
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. **Frontend Setup**
```bash
cd frontend/anketFe
npm install
```

5. **Launch Application**

Backend:
```bash
cd backend
mvn spring-boot:run
# Access at: http://localhost:8080
```

Frontend:
```bash
cd frontend/anketFe
npm run dev
# Access at: http://localhost:5173
```

---

## 📱 **User Guide**

### **For Survey Participants**
1. **Access Surveys**: Browse available surveys without registration
2. **Participate**: Complete surveys with intuitive interface
3. **View Results**: See aggregated results after completion

### **For Survey Creators**
1. **Create Account**: Register as a survey creator
2. **Design Surveys**: Use the survey builder with drag-and-drop interface
3. **Publish & Share**: Make surveys live and share with participants
4. **Monitor Performance**: Track responses and analyze results in real-time

### **For Administrators**
1. **Admin Dashboard**: Access comprehensive system overview
2. **User Management**: Monitor user activity and manage accounts
3. **Content Moderation**: Review and manage surveys across the platform
4. **System Analytics**: Monitor application performance and usage

---

## 🔧 **API Documentation**

### **Authentication Endpoints**
- `POST /api/simple-login` - User authentication
- `POST /api/simple-register` - New user registration
- `POST /api/create-admin-user` - Admin account creation

### **Survey Management**
- `GET /api/surveys` - Retrieve all surveys
- `POST /api/create-survey` - Create new survey
- `PUT /api/update-survey/{id}` - Update existing survey
- `DELETE /api/delete-survey/{id}` - Remove survey

### **Response Handling**
- `POST /api/submit-response` - Submit survey response
- `GET /api/survey/{id}/results` - Fetch survey analytics

### **Administrative**
- `GET /api/admin/all-surveys` - Admin survey overview
- `GET /api/admin/total-users` - User statistics

---

## 👨‍💻 **Author**

**Developer**: Umut Hasan Sahin
**GitHub**: https://github.com/UmutHSahin
---



# 🗂️ Akıllı Anket Uygulaması (TURKCE)

---

## 🌟 **Proje Genel Bakış**

Organizasyonların ve bireylerin anketleri verimli bir şekilde oluşturmasına, dağıtmasına ve analiz etmesine olanak tanıyan modern, tam yığın anket uygulaması. Kurumsal düzeyde teknolojilerle geliştirilmiş bu uygulama, anket oluşturucuları ve katılımcılar için sorunsuz bir deneyim sunarken güçlü analitik ve yönetim yetenekleri sağlar.

**Temel Özellikler:**
- Güvenli kullanıcı kimlik doğrulama ve rol tabanlı erişim kontrolü
- Çoklu soru türleri ile sezgisel anket oluşturma
- Gerçek zamanlı yanıt takibi ve analitik
- Masaüstü ve mobil cihazlar için duyarlı tasarım
- Sistem yönetimi için kapsamlı admin paneli

---

## ✨ **Temel Özellikler**

### 🔐 **Kimlik Doğrulama ve Güvenlik**
- **Kullanıcı Kaydı ve Girişi**: E-posta doğrulama ile güvenli hesap oluşturma
- **JWT Token Kimlik Doğrulama**: Durumsuz, güvenli oturum yönetimi
- **Rol Tabanlı Erişim Kontrolü**: Kullanıcılar ve yöneticiler için farklı izinler
- **Şifre Güvenliği**: Hassas veri koruması için BCrypt şifreleme

### 📊 **Anket Yönetimi**
- **Dinamik Anket Oluşturma**: Sınırsız çoktan seçmeli sorularla anket inşa etme
- **Kategori Organizasyonu**: Daha iyi keşfedilebilirlik için anketleri konulara göre düzenleme
- **Durum Yönetimi**: Anket erişilebilirliğini kontrol etme (Aktif/Pasif/Taslak)
- **Düzenle ve Sil**: Anket yönetimi için tam CRUD işlemleri

### 📝 **Yanıt Sistemi**
- **Anonim Yanıtlar**: Gizlilik odaklı anket katılımı
- **Gerçek Zamanlı Takip**: Canlı yanıt izleme ve ilerleme güncellemeleri
- **Tamamlama Doğrulama**: Yanıt doğrulama ile veri bütünlüğü sağlama
- **Kullanıcı Geçmişi**: Kayıtlı kullanıcılar için katılım geçmişi takibi

### 📈 **Analitik Dashboard**
- **Yanıt İstatistikleri**: Anket katılımının detaylı dökümü
- **Görsel Grafikler**: Anket sonuçlarının grafiksel gösterimi
- **Tamamlama Oranları**: Anket performans metriklerini izleme
- **Dışa Aktarma**: Sonuçları çeşitli formatlarda indirme

### 👑 **Yönetici Paneli**
- **Sistem Genel Bakış**: Sistem izleme için kapsamlı dashboard
- **Kullanıcı Yönetimi**: Kullanıcı hesaplarını ve aktivitelerini görüntüleme ve yönetme
- **Anket Moderasyonu**: Platform genelindeki tüm anketleri denetleme
- **Performans Metrikleri**: Uygulama sağlığı ve kullanım istatistiklerini izleme

---

## 🏗️ **Teknik Mimari**

### **Backend Teknolojileri**
- **Spring Boot 3.x**: Üretime hazır uygulama framework'ü
- **Java 17+**: Gelişmiş performans ve özelliklerle modern Java
- **PostgreSQL**: Gelişmiş özelliklerle güçlü ilişkisel veritabanı
- **Spring Data JPA**: Hibernate ORM ile basitleştirilmiş veri erişimi
- **Spring Security**: Kapsamlı güvenlik framework'ü
- **Maven**: Bağımlılık yönetimi ve yapı otomasyonu

### **Frontend Teknolojileri**
- **React 18**: En son özelliklerle modern UI kütüphanesi
- **Vite**: Yıldırım hızında yapı aracı ve geliştirme sunucusu
- **React Router**: Tek sayfa uygulaması için bildirimsel yönlendirme
- **Modern CSS**: Flexbox ve Grid ile duyarlı tasarım
- **Fetch API**: Sorunsuz backend iletişimi için HTTP istemcisi

---

## 🚀 **Hızlı Başlangıç**

### **Gereksinimler**
- Java 17 veya üzeri
- Maven 3.6+
- PostgreSQL 14+
- Node.js 16+
- npm veya yarn

### **Kurulum Adımları**

1. **Depoyu Klonlayın**
```bash
git clone https://github.com/yourusername/smart-survey-app.git
cd smart-survey-app
```

2. **Backend Kurulumu**
```bash
cd backend
mvn clean install
```

3. **Veritabanı Yapılandırması**
```sql
CREATE DATABASE survey_app;
```

4. **Frontend Kurulumu**
```bash
cd frontend/anketFe
npm install
```

5. **Uygulamayı Başlatın**

Backend:
```bash
cd backend
mvn spring-boot:run
# Erişim: http://localhost:8080
```

Frontend:
```bash
cd frontend/anketFe
npm run dev
# Erişim: http://localhost:5173
```

---

## 📱 **Kullanım Kılavuzu**

### **Anket Katılımcıları İçin**
1. **Anketlere Erişim**: Kayıt olmadan mevcut anketleri görüntüleyin
2. **Katılım**: Sezgisel arayüzle anketleri tamamlayın
3. **Sonuçları Görüntüleyin**: Tamamlama sonrası toplu sonuçları inceleyin

### **Anket Oluşturucuları İçin**
1. **Hesap Oluşturun**: Anket oluşturucu olarak kayıt olun
2. **Anket Tasarlayın**: Sürükle-bırak arayüzü ile anketler oluşturun
3. **Yayınlayın ve Paylaşın**: Anketleri yayına alın ve katılımcılarla paylaşın
4. **Performansı İzleyin**: Yanıtları takip edin ve sonuçları gerçek zamanlı analiz edin

### **Yöneticiler İçin**
1. **Admin Dashboard**: Kapsamlı sistem genel bakışına erişin
2. **Kullanıcı Yönetimi**: Kullanıcı aktivitelerini izleyin ve hesapları yönetin
3. **İçerik Moderasyonu**: Platform genelindeki anketleri inceleyin ve yönetin
4. **Sistem Analitikleri**: Uygulama performansını ve kullanımını izleyin

---

## 🔧 **API Dokümantasyonu**

### **Kimlik Doğrulama**
- `POST /api/simple-login` - Kullanıcı girişi
- `POST /api/simple-register` - Yeni kullanıcı kaydı
- `POST /api/create-admin-user` - Admin hesabı oluşturma

### **Anket Yönetimi**
- `GET /api/surveys` - Tüm anketleri getir
- `POST /api/create-survey` - Yeni anket oluştur
- `PUT /api/update-survey/{id}` - Mevcut anketi güncelle
- `DELETE /api/delete-survey/{id}` - Anketi kaldır

### **Yanıt İşleme**
- `POST /api/submit-response` - Anket yanıtını gönder
- `GET /api/survey/{id}/results` - Anket analitiğini getir


---

## 👨‍💻 **Geliştirici**

**İsim**: Umut Hasan Sahin  
**GitHub**: https://github.com/UmutHSahin

---

<div align="center">

**❤️ ile geliştirildi | Built with ❤️**

</div>
