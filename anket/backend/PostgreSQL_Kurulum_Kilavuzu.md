# 🐘 PostgreSQL ve pgAdmin 4 Kurulum ve Kullanım Kılavuzu

## 📋 İçindekiler
1. [PostgreSQL Kurulumu](#postgresql-kurulumu)
2. [pgAdmin 4 Kurulumu](#pgadmin-4-kurulumu)
3. [Veritabanı Oluşturma](#veritabanı-oluşturma)
4. [SQL Sorgularını Çalıştırma](#sql-sorgularını-çalıştırma)
5. [Anket Uygulaması Veritabanı Şeması](#anket-uygulaması-veritabanı-şeması)
6. [Örnek SQL Sorguları](#örnek-sql-sorguları)
7. [Sorun Giderme](#sorun-giderme)

---

## 🚀 PostgreSQL Kurulumu

### MacOS için Kurulum
```bash
# Homebrew ile kurulum
brew install postgresql@15

# PostgreSQL'i başlat
brew services start postgresql@15

# PostgreSQL'e bağlan
psql postgres
```

### Windows için Kurulum
1. [PostgreSQL resmi sitesinden](https://www.postgresql.org/download/windows/) indirin
2. İndirilen .exe dosyasını çalıştırın
3. Kurulum sırasında:
   - Port: 5432 (varsayılan)
   - Süper kullanıcı şifresi: güçlü bir şifre belirleyin
   - Locale: Turkish, Turkey (TR)

### Ubuntu/Linux için Kurulum
```bash
# Paket listesini güncelle
sudo apt update

# PostgreSQL kurulumu
sudo apt install postgresql postgresql-contrib

# PostgreSQL servisini başlat
sudo systemctl start postgresql
sudo systemctl enable postgresql

# postgres kullanıcısına geç
sudo -u postgres psql
```

---

## 🔧 pgAdmin 4 Kurulumu

### MacOS için
```bash
# Homebrew ile kurulum
brew install --cask pgadmin4
```

### Windows için
1. [pgAdmin resmi sitesinden](https://www.pgadmin.org/download/pgadmin-4-windows/) indirin
2. İndirilen .exe dosyasını çalıştırın
3. Kurulum tamamlandıktan sonra pgAdmin 4'ü başlatın

### Ubuntu/Linux için
```bash
# Paket deposunu ekle
curl https://www.pgadmin.org/static/packages_pgadmin_org.pub | sudo apt-key add
sudo sh -c 'echo "deb https://ftp.postgresql.org/pub/pgadmin/pgadmin4/apt/$(lsb_release -cs) pgadmin4 main" > /etc/apt/sources.list.d/pgadmin4.list'

# Kurulum
sudo apt update
sudo apt install pgadmin4

# Web modu için kurulum
sudo apt install pgadmin4-web
sudo /usr/pgadmin4/bin/setup-web.sh
```

---

## 🗄️ Veritabanı Oluşturma

### 1. pgAdmin 4'ü Başlatın
- pgAdmin 4'ü açın
- İlk açılışta master password belirleyin

### 2. PostgreSQL Sunucusuna Bağlanın
1. Sol panelde "Servers" üzerinde sağ tık yapın
2. "Create" > "Server..." seçin
3. **General** sekmesinde:
   - Name: `Local PostgreSQL`
4. **Connection** sekmesinde:
   - Host: `localhost`
   - Port: `5432`
   - Username: `postgres`
   - Password: (kurulum sırasında belirlediğiniz şifre)

### 3. Anket Veritabanını Oluşturun
1. Sunucu bağlantısı kurulduktan sonra sunucu adına sağ tık yapın
2. "Create" > "Database..." seçin
3. **General** sekmesinde:
   - Database: `anket_db`
   - Owner: `postgres`
   - Comment: `Akıllı Anket Uygulaması Veritabanı`

---

## 📊 SQL Sorgularını Çalıştırma

### pgAdmin 4'te SQL Sorgusu Çalıştırma
1. Sol panelde `anket_db` veritabanını seçin
2. Üst menüden "Tools" > "Query Tool" seçin (veya F5 tuşu)
3. SQL sorgunuzu yazın
4. Çalıştırmak için F5 tuşuna basın veya ▶️ butonuna tıklayın

### Komut Satırından SQL Çalıştırma
```bash
# Veritabanına bağlan
psql -U postgres -d anket_db

# SQL dosyası çalıştır
\i /path/to/your/script.sql

# Tek satır sorgu
SELECT * FROM users;

# Çıkış
\q
```

---

## 🏗️ Anket Uygulaması Veritabanı Şeması

### Tabloları Oluşturan SQL Script

```sql
-- Kullanıcılar Tablosu - Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Anketler Tablosu - Surveys Table
CREATE TABLE surveys (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    creator_id BIGINT NOT NULL REFERENCES users(id),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_anonymous BOOLEAN NOT NULL DEFAULT TRUE,
    allow_multiple_responses BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sorular Tablosu - Questions Table
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    question_text VARCHAR(500) NOT NULL,
    question_type VARCHAR(30) NOT NULL DEFAULT 'MULTIPLE_CHOICE',
    order_index INTEGER NOT NULL,
    is_required BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(300),
    survey_id BIGINT NOT NULL REFERENCES surveys(id) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seçenekler Tablosu - Options Table
CREATE TABLE options (
    id BIGSERIAL PRIMARY KEY,
    option_text VARCHAR(200) NOT NULL,
    order_index INTEGER NOT NULL,
    option_label VARCHAR(5),
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    description VARCHAR(300),
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Yanıtlar Tablosu - Responses Table
CREATE TABLE responses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    survey_id BIGINT NOT NULL REFERENCES surveys(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    selected_option_id BIGINT REFERENCES options(id),
    text_response TEXT,
    numeric_response DECIMAL,
    response_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    session_id VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- İndeksler - Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_surveys_creator ON surveys(creator_id);
CREATE INDEX idx_surveys_status ON surveys(status);
CREATE INDEX idx_questions_survey ON questions(survey_id);
CREATE INDEX idx_options_question ON options(question_id);
CREATE INDEX idx_responses_survey ON responses(survey_id);
CREATE INDEX idx_responses_user ON responses(user_id);
CREATE INDEX idx_responses_question ON responses(question_id);

-- Örnek Veri Ekleme - Sample Data Insertion
INSERT INTO users (first_name, last_name, email, password, role) VALUES
('Admin', 'User', 'admin@anket.com', '$2a$10$example.hash.here', 'ADMIN'),
('Demo', 'User', 'demo@example.com', '$2a$10$example.hash.here', 'USER'),
('Test', 'User', 'test@example.com', '$2a$10$example.hash.here', 'USER');

-- Örnek anket
INSERT INTO surveys (title, description, status, creator_id) VALUES
('Memnuniyet Anketi', 'Hizmet kalitemizi değerlendirin', 'ACTIVE', 1);

-- Örnek sorular
INSERT INTO questions (question_text, order_index, survey_id) VALUES
('Hizmetimizden ne kadar memnunsunuz?', 1, 1),
('Tekrar tercih eder misiniz?', 2, 1);

-- Örnek seçenekler
INSERT INTO options (option_text, order_index, option_label, question_id) VALUES
('Çok memnunum', 1, 'A', 1),
('Memnunum', 2, 'B', 1),
('Kararsızım', 3, 'C', 1),
('Memnun değilim', 4, 'D', 1),
('Kesinlikle evet', 1, 'A', 2),
('Evet', 2, 'B', 2),
('Hayır', 3, 'C', 2),
('Kesinlikle hayır', 4, 'D', 2);
```

---

## 🔍 Örnek SQL Sorguları

### Temel Sorgular

```sql
-- Tüm kullanıcıları listele
SELECT id, first_name, last_name, email, role, created_date 
FROM users 
WHERE is_active = true 
ORDER BY created_date DESC;

-- Aktif anketleri listele
SELECT s.id, s.title, s.status, u.first_name || ' ' || u.last_name as creator_name
FROM surveys s
JOIN users u ON s.creator_id = u.id
WHERE s.is_active = true AND s.status = 'ACTIVE'
ORDER BY s.created_date DESC;

-- Bir anketin tüm sorularını ve seçeneklerini getir
SELECT 
    q.id as question_id,
    q.question_text,
    q.order_index as question_order,
    o.id as option_id,
    o.option_text,
    o.option_label,
    o.order_index as option_order
FROM questions q
LEFT JOIN options o ON q.id = o.question_id
WHERE q.survey_id = 1 AND q.is_active = true AND o.is_active = true
ORDER BY q.order_index, o.order_index;

-- Anket sonuçlarını analiz et
SELECT 
    q.question_text,
    o.option_label,
    o.option_text,
    COUNT(r.id) as response_count,
    ROUND(COUNT(r.id) * 100.0 / SUM(COUNT(r.id)) OVER (PARTITION BY q.id), 2) as percentage
FROM questions q
JOIN options o ON q.id = o.question_id
LEFT JOIN responses r ON o.id = r.selected_option_id AND r.is_active = true
WHERE q.survey_id = 1 AND q.is_active = true AND o.is_active = true
GROUP BY q.id, q.question_text, q.order_index, o.id, o.option_label, o.option_text, o.order_index
ORDER BY q.order_index, o.order_index;
```

### İstatistik Sorguları

```sql
-- Kullanıcı istatistikleri
SELECT 
    COUNT(*) as total_users,
    COUNT(CASE WHEN role = 'ADMIN' THEN 1 END) as admin_count,
    COUNT(CASE WHEN role = 'USER' THEN 1 END) as user_count,
    COUNT(CASE WHEN created_date >= CURRENT_DATE - INTERVAL '30 days' THEN 1 END) as new_users_last_30_days
FROM users 
WHERE is_active = true;

-- Anket istatistikleri
SELECT 
    COUNT(*) as total_surveys,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) as active_surveys,
    COUNT(CASE WHEN status = 'DRAFT' THEN 1 END) as draft_surveys,
    COUNT(CASE WHEN status = 'CLOSED' THEN 1 END) as closed_surveys
FROM surveys 
WHERE is_active = true;

-- En popüler anketler (yanıt sayısına göre)
SELECT 
    s.title,
    COUNT(r.id) as response_count,
    u.first_name || ' ' || u.last_name as creator_name
FROM surveys s
LEFT JOIN responses r ON s.id = r.survey_id AND r.is_active = true
JOIN users u ON s.creator_id = u.id
WHERE s.is_active = true
GROUP BY s.id, s.title, u.first_name, u.last_name
ORDER BY response_count DESC
LIMIT 10;

-- Aylık anket oluşturma istatistikleri
SELECT 
    DATE_TRUNC('month', created_date) as month,
    COUNT(*) as survey_count
FROM surveys 
WHERE is_active = true 
    AND created_date >= CURRENT_DATE - INTERVAL '12 months'
GROUP BY DATE_TRUNC('month', created_date)
ORDER BY month;
```

### Veri Temizleme ve Bakım

```sql
-- Eski yanıtları temizle (6 aydan eski)
UPDATE responses 
SET is_active = false 
WHERE created_date < CURRENT_DATE - INTERVAL '6 months';

-- Boş anketleri bul (hiç sorusu olmayan)
SELECT s.id, s.title, s.created_date
FROM surveys s
LEFT JOIN questions q ON s.id = q.survey_id AND q.is_active = true
WHERE s.is_active = true 
    AND q.id IS NULL;

-- Yanıt almamış anketleri bul
SELECT s.id, s.title, s.status, s.created_date
FROM surveys s
LEFT JOIN responses r ON s.id = r.survey_id AND r.is_active = true
WHERE s.is_active = true 
    AND s.status = 'ACTIVE'
    AND r.id IS NULL;

-- Veritabanı boyut bilgisi
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
```

---

## 🛠️ Sorun Giderme

### Yaygın Hatalar ve Çözümleri

#### 1. Bağlantı Hatası
```
FATAL: password authentication failed for user "postgres"
```
**Çözüm:**
```bash
# Şifreyi sıfırla
sudo -u postgres psql
ALTER USER postgres PASSWORD 'yeni_sifre';
```

#### 2. Veritabanı Bulunamadı
```
FATAL: database "anket_db" does not exist
```
**Çözüm:**
```sql
-- PostgreSQL'e bağlan ve veritabanı oluştur
CREATE DATABASE anket_db;
```

#### 3. Tablo Bulunamadı
```
ERROR: relation "users" does not exist
```
**Çözüm:**
- Yukarıdaki şema script'ini çalıştırın
- Spring Boot uygulamasını çalıştırarak tabloların otomatik oluşturulmasını sağlayın

#### 4. Port Çakışması
```
Is the server running on host "localhost" and accepting TCP/IP connections on port 5432?
```
**Çözüm:**
```bash
# PostgreSQL'in çalışıp çalışmadığını kontrol et
sudo systemctl status postgresql

# Başlat
sudo systemctl start postgresql

# Port kontrolü
netstat -an | grep 5432
```

### Performans Optimizasyonu

```sql
-- İndeks kullanımını kontrol et
EXPLAIN ANALYZE SELECT * FROM surveys WHERE status = 'ACTIVE';

-- Yavaş sorguları bul
SELECT query, mean_time, calls 
FROM pg_stat_statements 
ORDER BY mean_time DESC 
LIMIT 10;

-- Veritabanı istatistiklerini güncelle
ANALYZE;

-- Gereksiz alanı temizle
VACUUM;
```

### Yedekleme ve Geri Yükleme

```bash
# Veritabanı yedeği al
pg_dump -U postgres -h localhost anket_db > anket_backup.sql

# Yedeği geri yükle
psql -U postgres -h localhost anket_db < anket_backup.sql

# Sadece şema yedeği
pg_dump -U postgres -h localhost --schema-only anket_db > anket_schema.sql

# Sadece veri yedeği
pg_dump -U postgres -h localhost --data-only anket_db > anket_data.sql
```

---

## 📞 Yardım ve Destek

### Faydalı Komutlar
```sql
-- Mevcut veritabanlarını listele
\l

-- Mevcut tabloları listele
\dt

-- Tablo yapısını görüntüle
\d users

-- Mevcut kullanıcıları listele
\du

-- Yardım
\?

-- Çıkış
\q
```

### Kaynaklar
- [PostgreSQL Resmi Dokümantasyonu](https://www.postgresql.org/docs/)
- [pgAdmin 4 Kullanım Kılavuzu](https://www.pgadmin.org/docs/)
- [PostgreSQL Tutorial](https://www.postgresqltutorial.com/)

---

## ✅ Kontrol Listesi

- [ ] PostgreSQL kuruldu ve çalışıyor
- [ ] pgAdmin 4 kuruldu ve PostgreSQL'e bağlandı
- [ ] `anket_db` veritabanı oluşturuldu
- [ ] Şema script'i çalıştırıldı
- [ ] Örnek veriler eklendi
- [ ] Temel sorgular test edildi
- [ ] Spring Boot uygulaması veritabanına bağlanabilir

Bu kılavuzu takip ederek PostgreSQL ve pgAdmin 4'ü başarıyla kurabilir ve anket uygulamanızın veritabanını yönetebilirsiniz! 🎉
