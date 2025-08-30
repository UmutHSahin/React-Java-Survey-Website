# 🚀 Akıllı Anket Uygulaması - Smart Survey Application

## 📋 Proje Hakkında - About Project

Bu proje, kullanıcıların anket oluşturabildiği, yanıtlayabildiği ve sonuçları analiz edebildiği modern bir web uygulamasıdır. Spring Boot ve React teknolojileri kullanılarak geliştirilmiştir.

This project is a modern web application where users can create surveys, respond to them, and analyze results. It's developed using Spring Boot and React technologies.

## ✨ Özellikler - Features

### 🔐 Kimlik Doğrulama - Authentication
- JWT tabanlı güvenli giriş sistemi
- Kullanıcı kaydı ve profil yönetimi
- Role tabanlı yetkilendirme (USER, ADMIN)

### 📊 Anket Yönetimi - Survey Management
- Anket oluşturma, düzenleme ve silme
- Çoktan seçmeli sorular
- Anonim ve kayıtlı kullanıcı anketleri
- Anket durumu yönetimi (DRAFT, ACTIVE, CLOSED)

### 📈 Analiz ve Raporlama - Analysis & Reporting
- Gerçek zamanlı sonuç görüntüleme
- Yüzdelik dilim analizleri
- İstatistiksel raporlar

### 👑 Admin Paneli - Admin Panel
- Tüm anketleri görüntüleme ve yönetme
- Kullanıcı yönetimi
- Sistem istatistikleri

## 🛠️ Teknolojiler - Technologies

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2** - Application framework
- **Spring Security** - Security framework
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Database
- **JWT** - Authentication tokens
- **Maven** - Dependency management

### Frontend
- **React 18** - UI library
- **Vite** - Build tool
- **JavaScript/JSX** - Programming language

## 📁 Proje Yapısı - Project Structure

```
anket/
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/com/anket/
│   │   ├── config/            # Konfigürasyon sınıfları
│   │   ├── controller/        # REST API Controller'lar
│   │   ├── dto/              # Data Transfer Objects
│   │   ├── entity/           # JPA Entity'ler
│   │   ├── repository/       # Data Access Layer
│   │   ├── security/         # Güvenlik sınıfları
│   │   └── service/          # İş mantığı servisleri
│   ├── src/main/resources/
│   │   └── application.yml   # Uygulama konfigürasyonu
│   └── pom.xml              # Maven dependencies
├── frontend/anketFe/          # React Frontend
│   ├── src/
│   │   ├── components/       # React bileşenleri
│   │   └── assets/          # Statik dosyalar
│   └── package.json         # NPM dependencies
└── README.md               # Bu dosya
```

## 🚀 Kurulum ve Çalıştırma - Installation & Running

### Ön Gereksinimler - Prerequisites

- Java 17 veya üzeri
- Node.js 18 veya üzeri
- PostgreSQL 13 veya üzeri
- Maven 3.8 veya üzeri

### 1. Veritabanı Kurulumu - Database Setup

PostgreSQL kurulumu ve konfigürasyonu için [PostgreSQL Kurulum Kılavuzu](PostgreSQL_Kurulum_Kilavuzu.md) dosyasını inceleyin.

```sql
-- PostgreSQL'de veritabanı oluştur
CREATE DATABASE anket_db;
```

### 2. Backend Kurulumu - Backend Setup

```bash
# Backend dizinine git
cd backend

# Dependencies'leri yükle
mvn clean install

# Uygulamayı çalıştır
mvn spring-boot:run
```

Backend şu adreste çalışacak: `http://localhost:8080`

### 3. Frontend Kurulumu - Frontend Setup

```bash
# Frontend dizinine git
cd frontend/anketFe

# Dependencies'leri yükle
npm install

# Development server'ı başlat
npm run dev
```

Frontend şu adreste çalışacak: `http://localhost:5173`

## 🔧 Konfigürasyon - Configuration

### Backend Konfigürasyonu

`src/main/resources/application.yml` dosyasında şu ayarları yapın:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/anket_db
    username: postgres
    password: your_password_here  # Kendi şifrenizi yazın

jwt:
  secret: anketUygulamasiGizliAnahtar2024!  # Production'da değiştirin
  expiration: 86400000  # 24 saat
```

### Frontend Konfigürasyonu

API endpoint'lerini `src/components/` altındaki dosyalarda güncelleyin:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## 📚 API Dokümantasyonu - API Documentation

### Kimlik Doğrulama Endpoints - Authentication Endpoints

| Method | Endpoint | Açıklama | Auth Required |
|--------|----------|----------|---------------|
| POST | `/api/auth/login` | Kullanıcı girişi | ❌ |
| POST | `/api/auth/register` | Kullanıcı kaydı | ❌ |
| GET | `/api/auth/me` | Mevcut kullanıcı bilgisi | ✅ |
| POST | `/api/auth/logout` | Kullanıcı çıkışı | ✅ |

### Anket Endpoints - Survey Endpoints

| Method | Endpoint | Açıklama | Auth Required |
|--------|----------|----------|---------------|
| GET | `/api/surveys/public` | Aktif anketleri listele | ❌ |
| POST | `/api/surveys` | Yeni anket oluştur | ✅ |
| GET | `/api/surveys/{id}` | Anket detayı | ✅ |
| PUT | `/api/surveys/{id}` | Anket güncelle | ✅ |
| DELETE | `/api/surveys/{id}` | Anket sil | ✅ |

### Admin Endpoints - Admin Endpoints

| Method | Endpoint | Açıklama | Auth Required |
|--------|----------|----------|---------------|
| GET | `/api/admin/users` | Tüm kullanıcıları listele | 👑 Admin |
| GET | `/api/admin/surveys` | Tüm anketleri listele | 👑 Admin |
| GET | `/api/admin/stats` | Sistem istatistikleri | 👑 Admin |

## 🔐 Güvenlik - Security

### JWT Token Kullanımı

API isteklerinde Authorization header'ı kullanın:

```javascript
headers: {
  'Authorization': 'Bearer YOUR_JWT_TOKEN_HERE',
  'Content-Type': 'application/json'
}
```

### Roller ve Yetkiler - Roles and Permissions

- **USER**: Normal kullanıcı, kendi anketlerini yönetebilir
- **ADMIN**: Yönetici, tüm anketleri ve kullanıcıları yönetebilir

## 🧪 Test Kullanıcıları - Test Users

Uygulama başlangıçta şu test kullanıcıları ile gelir:

```
Admin Kullanıcı:
Email: admin@anket.com
Password: admin123

Demo Kullanıcı:
Email: demo@example.com
Password: password
```

## 📊 Veritabanı Şeması - Database Schema

### Ana Tablolar - Main Tables

- **users**: Kullanıcı bilgileri
- **surveys**: Anket bilgileri
- **questions**: Soru bilgileri
- **options**: Seçenek bilgileri
- **responses**: Yanıt bilgileri

### İlişkiler - Relationships

```
User (1) -----> (*) Survey
Survey (1) ---> (*) Question
Question (1) -> (*) Option
Question (1) -> (*) Response
Option (1) ---> (*) Response
User (1) -----> (*) Response
```

## 🔍 Örnek Kullanım - Example Usage

### 1. Kullanıcı Kaydı

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123",
    "confirmPassword": "password123"
  }'
```

### 2. Giriş Yapma

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. Anket Oluşturma

```bash
curl -X POST http://localhost:8080/api/surveys \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Memnuniyet Anketi",
    "description": "Hizmet kalitemizi değerlendirin",
    "questions": [
      {
        "questionText": "Hizmetimizden memnun musunuz?",
        "options": [
          {"optionText": "Çok memnunum"},
          {"optionText": "Memnunum"},
          {"optionText": "Kararsızım"},
          {"optionText": "Memnun değilim"}
        ]
      }
    ]
  }'
```

## 🐛 Sorun Giderme - Troubleshooting

### Yaygın Hatalar - Common Issues

1. **Database Connection Error**
   ```
   Çözüm: PostgreSQL'in çalıştığından ve application.yml'deki 
   bağlantı bilgilerinin doğru olduğundan emin olun.
   ```

2. **JWT Token Error**
   ```
   Çözüm: Token'ın süresi dolmuş olabilir. Yeniden giriş yapın.
   ```

3. **CORS Error**
   ```
   Çözüm: SecurityConfig.java'daki CORS ayarlarını kontrol edin.
   ```

### Log Seviyeleri - Log Levels

```yaml
logging:
  level:
    com.anket: DEBUG
    org.springframework.security: DEBUG
```

## 📈 Performans - Performance

### Veritabanı Optimizasyonu

- İndeksler otomatik olarak oluşturulur
- Lazy loading kullanılır
- Connection pooling aktiftir

### Cache Stratejisi

- JWT token'ları memory'de cache'lenir
- Kullanıcı bilgileri session boyunca saklanır

## 🚀 Deployment

### Production Hazırlığı

1. **application-prod.yml** oluşturun
2. JWT secret'ını değiştirin
3. Database connection pool ayarlarını yapın
4. HTTPS kullanın
5. Log seviyelerini INFO/WARN yapın

### Docker Kullanımı

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/anket-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Katkıda Bulunma - Contributing

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'Add amazing feature'`)
4. Push yapın (`git push origin feature/amazing-feature`)
5. Pull Request açın

## 📝 Lisans - License

Bu proje MIT lisansı altında lisanslanmıştır. Detaylar için [LICENSE](LICENSE) dosyasına bakın.

## 📞 İletişim - Contact

- **Proje Sahibi**: [Your Name]
- **Email**: your.email@example.com
- **GitHub**: [Your GitHub Profile]

## 🎯 Gelecek Özellikler - Future Features

- [ ] E-posta bildirimler
- [ ] Anket şablonları
- [ ] Gelişmiş analitik
- [ ] Mobil uygulama
- [ ] API rate limiting
- [ ] Redis cache entegrasyonu
- [ ] Elasticsearch entegrasyonu

## 📚 Kaynaklar - Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [React Documentation](https://reactjs.org/docs/getting-started.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT.io](https://jwt.io/)

---

**Not**: Bu README dosyası sürekli güncellenmektedir. Son güncellemeler için GitHub repository'sini kontrol edin.

**Note**: This README file is continuously updated. Check the GitHub repository for the latest updates.
