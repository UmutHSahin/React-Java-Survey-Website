package com.anket;

import com.anket.entity.Survey;
import com.anket.entity.SurveyStatus;
import com.anket.entity.User;
import com.anket.entity.UserRole;
import com.anket.entity.Question;
import com.anket.entity.QuestionType;
import com.anket.entity.Option;
import com.anket.entity.Response;
import com.anket.repository.SurveyRepository;
import com.anket.repository.UserRepository;
import com.anket.repository.QuestionRepository;
import com.anket.repository.OptionRepository;
import com.anket.repository.ResponseRepository;
import com.anket.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Ana API Controller - Tüm anket ve kullanıcı işlemlerini yönetir
 * Survey ve User entity'leri ile veritabanı işlemleri yapar
 */
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class SimpleController {

    // Veritabanı işlemleri için gerekli repository'ler
    @Autowired
    private UserRepository userRepository;        // Kullanıcı işlemleri

    @Autowired
    private PasswordEncoder passwordEncoder;      // Şifre şifreleme

    @Autowired
    private SurveyService surveyService;          // Anket servis işlemleri

    @Autowired
    private QuestionRepository questionRepository; // Soru işlemleri

    @Autowired
    private OptionRepository optionRepository;    // Seçenek işlemleri

    @Autowired
    private ResponseRepository responseRepository; // Yanıt işlemleri
    
    @Autowired
    private com.anket.repository.SurveyRepository surveyRepository; // Anket işlemleri

    /**
     * Basit test endpoint - Sistemin çalışıp çalışmadığını kontrol eder
     */
    @GetMapping("/simple")
    public String simple() {
        return "It works!";
    }

    /**
     * Veritabanı inceleme endpoint'i - Sistem durumunu kontrol eder
     * Kullanıcı sayısı, anket sayısı ve detaylarını döndürür
     */
    @GetMapping("/inspect-database")
    public ResponseEntity<?> inspectDatabase() {
        try {
            System.out.println("🔍 Inspecting database directly...");
            
            // Check users
            long userCount = userRepository.count();
            
            // Check surveys directly from repository
            var allSurveysFromRepo = surveyRepository.findAll();
            var activeSurveysFromRepo = surveyRepository.findByIsActiveTrue();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Database inspection completed");
            response.put("userCount", userCount);
            response.put("totalSurveysInDB", allSurveysFromRepo.size());
            response.put("activeSurveysInDB", activeSurveysFromRepo.size());
            response.put("inactiveSurveysInDB", allSurveysFromRepo.size() - activeSurveysFromRepo.size());
            
            if (!allSurveysFromRepo.isEmpty()) {
                response.put("surveyDetails", allSurveysFromRepo.stream().map(survey -> Map.of(
                    "id", survey.getId(),
                    "title", survey.getTitle(),
                    "isActive", survey.getIsActive(),
                    "creatorId", survey.getCreator() != null ? survey.getCreator().getId() : "NULL",
                    "creatorEmail", survey.getCreator() != null ? survey.getCreator().getEmail() : "NULL"
                )).toList());
            }
            
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Database inspection: " + userCount + " users, " + 
                             allSurveysFromRepo.size() + " total surveys (" + 
                             activeSurveysFromRepo.size() + " active)");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Database inspection error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Database inspection failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Admin kullanıcısı oluşturma endpoint'i
     * Sistem yönetimi için gerekli admin hesabını oluşturur
     * Eğer admin zaten varsa mevcut bilgileri döndürür
     */
    @PostMapping("/create-admin-user")
    public ResponseEntity<?> createAdminUser() {
        try {
            System.out.println("👑 Creating admin user...");
            
            // Check if admin already exists
            var existingAdmin = userRepository.findByEmail("admin@anket.com");
            if (existingAdmin.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Admin user already exists");
                response.put("adminEmail", "admin@anket.com");
                response.put("adminPassword", "admin123");
                response.put("timestamp", LocalDateTime.now());
                return ResponseEntity.ok(response);
            }
            
            // Create admin user
            User adminUser = new User();
            adminUser.setEmail("admin@anket.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setIsActive(true);
            
            userRepository.save(adminUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin user created successfully");
            response.put("adminEmail", "admin@anket.com");
            response.put("adminPassword", "admin123");
            response.put("adminId", adminUser.getId());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Admin user created: " + adminUser.getEmail() + " (ID: " + adminUser.getId() + ")");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Create admin user error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Create admin user failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Admin için toplam kullanıcı sayısı endpoint'i
     * Sistemdeki toplam ve aktif kullanıcı sayısını döndürür
     * Admin paneli istatistikleri için kullanılır
     */
    @GetMapping("/admin/total-users")
    public ResponseEntity<?> getTotalUsersCount() {
        try {
            System.out.println("👥 Admin requesting total user count...");
            
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countByIsActiveTrue();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalUsers", totalUsers);
            response.put("activeUsers", activeUsers);
            response.put("message", "Total users count retrieved successfully");
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Total users count: " + totalUsers + " (Active: " + activeUsers + ")");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error getting total users count: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to get total users count");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Admin için tüm anketleri getirme endpoint'i
     * Sistemdeki tüm anketleri (aktif/pasif) getirir
     * Her anket için soru sayısı, yanıt sayısı ve benzersiz kullanıcı sayısını hesaplar
     */
    @GetMapping("/admin/all-surveys")
    public ResponseEntity<?> getAllSurveysForAdmin() {
        try {
            System.out.println("👑 Admin requesting all surveys...");
            
            // Get all surveys including inactive ones
            var allSurveys = surveyRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "All surveys retrieved for admin");
            response.put("totalSurveysCount", allSurveys.size());
            response.put("surveys", allSurveys.stream().map(survey -> {
                Map<String, Object> surveyData = new HashMap<>();
                surveyData.put("id", survey.getId());
                surveyData.put("title", survey.getTitle());
                surveyData.put("description", survey.getDescription() != null ? survey.getDescription() : "");
                surveyData.put("status", survey.getStatus().toString());
                surveyData.put("creatorId", survey.getCreator() != null ? survey.getCreator().getId() : null);
                surveyData.put("creatorEmail", survey.getCreator() != null ? survey.getCreator().getEmail() : "NO_CREATOR");
                surveyData.put("questionCount", survey.getQuestionCount());
                surveyData.put("responseCount", survey.getResponseCount());
                surveyData.put("createdDate", survey.getCreatedDate());
                surveyData.put("isActive", survey.getIsActive());
                
                // Count unique users who completed this survey
                long uniqueUserResponses = responseRepository.countDistinctUsersBySurveyId(survey.getId());
                surveyData.put("uniqueUsers", uniqueUserResponses);
                
                return surveyData;
            }).toList());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Admin retrieved " + allSurveys.size() + " total surveys");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Admin get all surveys error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Admin get all surveys failed");
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Test anketleri oluşturma endpoint'i
     * Geliştirme ve test amaçlı örnek anketler oluşturur
     * İlk kullanıcıyı creator olarak kullanır
     */
    @PostMapping("/create-test-surveys")
    public ResponseEntity<?> createTestSurveys() {
        try {
            System.out.println("📝 Creating test surveys...");
            
            // Get first user as creator
            var users = userRepository.findAll();
            if (users.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No users found");
                errorResponse.put("message", "Cannot create surveys without users");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            var creator = users.get(0);
            int surveysCreated = 0;
            
            // Create test surveys
            for (int i = 1; i <= 3; i++) {
                try {
                    var survey = new com.anket.entity.Survey();
                    survey.setTitle("Test Survey " + i);
                    survey.setDescription("This is a test survey #" + i + " created for testing deletion");
                    survey.setCreator(creator);
                    survey.setStatus(com.anket.entity.SurveyStatus.DRAFT);
                    survey.setIsAnonymous(true);
                    survey.setAllowMultipleResponses(false);
                    
                    var savedSurvey = surveyService.createSurvey(survey);
                    if (savedSurvey != null) {
                        surveysCreated++;
                        System.out.println("✅ Created survey: " + savedSurvey.getTitle() + " (ID: " + savedSurvey.getId() + ")");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Failed to create survey " + i + ": " + e.getMessage());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test surveys creation completed");
            response.put("surveysCreated", surveysCreated);
            response.put("creatorEmail", creator.getEmail());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Created " + surveysCreated + " test surveys");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Create test surveys error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Create test surveys failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Test orphaned surveys endpoint - Yetim anketleri test etme endpoint'i
     */
    @GetMapping("/test-orphaned-surveys")
    public ResponseEntity<?> testOrphanedSurveys() {
        try {
            System.out.println("🔍 Testing orphaned surveys...");
            
            var orphanedSurveys = surveyService.findOrphanedSurveys();
            var surveysWithInactiveCreator = surveyService.findSurveysWithInactiveCreator();
            var surveysWithoutQuestions = surveyService.findSurveysWithoutQuestions();
            var oldSurveys = surveyService.findOldSurveysWithoutResponses(30);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Orphaned surveys test completed");
            response.put("orphanedSurveysCount", orphanedSurveys.size());
            response.put("surveysWithInactiveCreatorCount", surveysWithInactiveCreator.size());
            response.put("surveysWithoutQuestionsCount", surveysWithoutQuestions.size());
            response.put("oldSurveysWithoutResponsesCount", oldSurveys.size());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Orphaned surveys test completed");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Orphaned surveys test error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Orphaned surveys test failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * List all surveys endpoint - Tüm anketleri listele endpoint'i
     */
    @GetMapping("/list-all-surveys")
    public ResponseEntity<?> listAllSurveys() {
        try {
            System.out.println("📋 Listing all surveys...");
            
            var allSurveys = surveyService.getAllActiveSurveys();
            
            // For now, use first available user to check completion status
            // In a real app, you'd get the current authenticated user
            final User currentUser;
            var users = userRepository.findAll();
            if (!users.isEmpty()) {
                currentUser = users.get(0);
                System.out.println("🔄 Checking completion status for user: " + currentUser.getEmail());
            } else {
                currentUser = null;
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "All surveys listed successfully");
            response.put("totalSurveysCount", allSurveys.size());
            response.put("surveys", allSurveys.stream().map(survey -> {
                Map<String, Object> surveyData = new HashMap<>();
                surveyData.put("id", survey.getId());
                surveyData.put("title", survey.getTitle());
                surveyData.put("description", survey.getDescription() != null ? survey.getDescription() : "");
                surveyData.put("status", survey.getStatus().toString());
                surveyData.put("creatorId", survey.getCreator() != null ? survey.getCreator().getId() : null);
                surveyData.put("creatorEmail", survey.getCreator() != null ? survey.getCreator().getEmail() : "NO_CREATOR");
                surveyData.put("questionCount", survey.getQuestionCount());
                
                // Count unique users who completed this survey
                long uniqueUserResponses = responseRepository.countDistinctUsersBySurveyId(survey.getId());
                surveyData.put("responseCount", uniqueUserResponses);
                
                surveyData.put("createdDate", survey.getCreatedDate());
                surveyData.put("isActive", survey.getIsActive());
                
                // Add completion status for current user
                if (currentUser != null) {
                    boolean isCompleted = responseRepository.existsByUserIdAndSurveyId(currentUser.getId(), survey.getId());
                    surveyData.put("isCompleted", isCompleted);
                    System.out.println("📊 Survey " + survey.getId() + " completion status: " + isCompleted);
                } else {
                    surveyData.put("isCompleted", false);
                }
                
                return surveyData;
            }).toList());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Listed " + allSurveys.size() + " surveys");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ List surveys error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "List surveys failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get survey statistics for a specific survey
     * Belirli bir anket için istatistikleri getir
     */
    @GetMapping("/survey-statistics/{surveyId}")
    public ResponseEntity<?> getSurveyStatistics(@PathVariable Long surveyId) {
        try {
            System.out.println("📊 Getting statistics for survey ID: " + surveyId);
            
            // Find the survey
            var surveyOpt = surveyRepository.findById(surveyId);
            if (surveyOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Survey not found");
                errorResponse.put("message", "Survey with ID " + surveyId + " does not exist");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            var survey = surveyOpt.get();
            
            // Get questions for this survey
            var questions = questionRepository.findBySurveyOrderByOrderIndex(survey);
            
            // Get response statistics
            var totalResponses = responseRepository.countBySurveyId(surveyId);
            var uniqueUsers = responseRepository.countDistinctUsersBySurveyId(surveyId);
            
            // Get question-level statistics
            var questionStats = questions.stream().map(question -> {
                Map<String, Object> qStats = new HashMap<>();
                qStats.put("id", question.getId());
                qStats.put("text", question.getQuestionText());
                qStats.put("type", question.getQuestionType().toString());
                qStats.put("orderIndex", question.getOrderIndex());
                
                // Get options and their response counts
                var options = optionRepository.findByQuestion(question);
                var optionStats = options.stream().map(option -> {
                    Map<String, Object> optStats = new HashMap<>();
                    optStats.put("id", option.getId());
                    optStats.put("text", option.getOptionText());
                    
                    // Count responses for this option
                    long optionResponseCount = responseRepository.countByOptionId(option.getId());
                    optStats.put("responseCount", optionResponseCount);
                    
                    return optStats;
                }).toList();
                
                qStats.put("options", optionStats);
                qStats.put("totalResponses", question.getOptions().stream()
                    .mapToLong(opt -> responseRepository.countByOptionId(opt.getId()))
                    .sum());
                
                return qStats;
            }).toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("surveyId", surveyId);
            response.put("surveyTitle", survey.getTitle());
            response.put("surveyDescription", survey.getDescription());
            response.put("totalQuestions", questions.size());
            response.put("totalResponses", totalResponses);
            response.put("uniqueUsers", uniqueUsers);
            response.put("completionRate", uniqueUsers > 0 ? (double) uniqueUsers / uniqueUsers * 100 : 0);
            response.put("createdDate", survey.getCreatedDate());
            response.put("status", survey.getStatus().toString());
            response.put("questionStatistics", questionStats);
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Survey statistics retrieved: " + survey.getTitle() + 
                             " - " + questions.size() + " questions, " + uniqueUsers + " users");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Get survey statistics error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Get survey statistics failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get surveys created by current user - Mevcut kullanıcının oluşturduğu anketleri getir
     * This endpoint returns only surveys that the current user created
     */
    @GetMapping("/my-surveys")
    public ResponseEntity<?> getMySurveys(@RequestParam(required = false) String userEmail) {
        try {
            System.out.println("📋 Getting surveys created by current user...");
            
            User currentUser = null;
            
            // If userEmail is provided, find that specific user
            if (userEmail != null && !userEmail.trim().isEmpty()) {
                currentUser = userRepository.findByEmail(userEmail.trim()).orElse(null);
                System.out.println("🔍 Looking for user with email: " + userEmail);
            }
            
            // If no user found by email, use first available user (fallback for testing)
            if (currentUser == null) {
                var users = userRepository.findAll();
                if (!users.isEmpty()) {
                    currentUser = users.get(0);
                    System.out.println("🔄 Fallback: Using first available user: " + currentUser.getEmail());
                }
            }
            
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "No user found");
                errorResponse.put("message", "User not authenticated");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            System.out.println("🔄 Getting surveys for user: " + currentUser.getEmail());
            
            // Get surveys created by current user
            var mySurveys = surveyRepository.findByCreatorAndIsActiveTrue(currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User surveys retrieved successfully");
            response.put("totalSurveysCount", mySurveys.size());
            response.put("surveys", mySurveys.stream().map(survey -> {
                Map<String, Object> surveyData = new HashMap<>();
                surveyData.put("id", survey.getId());
                surveyData.put("title", survey.getTitle());
                surveyData.put("description", survey.getDescription() != null ? survey.getDescription() : "");
                surveyData.put("status", survey.getStatus().toString());
                surveyData.put("creatorId", survey.getCreator() != null ? survey.getCreator().getId() : null);
                surveyData.put("creatorEmail", survey.getCreator() != null ? survey.getCreator().getEmail() : "NO_CREATOR");
                surveyData.put("questionCount", survey.getQuestionCount());
                
                // Count unique users who completed this survey
                long uniqueUserResponses = responseRepository.countDistinctUsersBySurveyId(survey.getId());
                surveyData.put("responseCount", uniqueUserResponses);
                
                surveyData.put("createdDate", survey.getCreatedDate());
                surveyData.put("isActive", survey.getIsActive());
                
                return surveyData;
            }).toList());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Retrieved " + mySurveys.size() + " surveys for user: " + currentUser.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Get my surveys error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Get my surveys failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * List ALL surveys endpoint including inactive - Tüm anketleri listele (aktif ve pasif dahil)
     */
    @GetMapping("/list-all-surveys-including-inactive")
    public ResponseEntity<?> listAllSurveysIncludingInactive() {
        try {
            System.out.println("📋 Listing ALL surveys including inactive...");
            
            var allSurveys = surveyService.getAllSurveys();
            var activeSurveys = allSurveys.stream().filter(s -> s.getIsActive()).toList();
            var inactiveSurveys = allSurveys.stream().filter(s -> !s.getIsActive()).toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "All surveys (including inactive) listed successfully");
            response.put("totalSurveysCount", allSurveys.size());
            response.put("activeSurveysCount", activeSurveys.size());
            response.put("inactiveSurveysCount", inactiveSurveys.size());
            response.put("allSurveys", allSurveys.stream().map(survey -> Map.of(
                "id", survey.getId(),
                "title", survey.getTitle(),
                "description", survey.getDescription() != null ? survey.getDescription() : "",
                "status", survey.getStatus().toString(),
                "creatorId", survey.getCreator() != null ? survey.getCreator().getId() : null,
                "creatorEmail", survey.getCreator() != null ? survey.getCreator().getEmail() : "NO_CREATOR",
                "questionCount", survey.getQuestionCount(),
                "responseCount", survey.getResponseCount(),
                "createdDate", survey.getCreatedDate(),
                "isActive", survey.getIsActive()
            )).toList());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Listed " + allSurveys.size() + " total surveys (" + 
                             activeSurveys.size() + " active, " + inactiveSurveys.size() + " inactive)");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ List all surveys error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "List all surveys failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Force delete all surveys endpoint - Tüm anketleri zorla sil endpoint'i
     */
    @PostMapping("/force-delete-all-surveys")
    public ResponseEntity<?> forceDeleteAllSurveys() {
        try {
            System.out.println("🗑️ Force deleting all surveys...");
            
            var allSurveys = surveyService.getAllActiveSurveys();
            int totalSurveys = allSurveys.size();
            int deletedCount = 0;
            
            for (var survey : allSurveys) {
                boolean deleted = surveyService.deleteSurvey(survey.getId());
                if (deleted) {
                    deletedCount++;
                }
                System.out.println("Survey ID " + survey.getId() + " (" + survey.getTitle() + "): " + 
                                 (deleted ? "DELETED" : "FAILED"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Force delete all surveys completed");
            response.put("totalSurveysFound", totalSurveys);
            response.put("surveysDeleted", deletedCount);
            response.put("surveysFailed", totalSurveys - deletedCount);
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Force delete completed: " + deletedCount + "/" + totalSurveys + " surveys deleted");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Force delete error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Force delete failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Clean database endpoint - Veritabanı temizleme endpoint'i
     * Removes orphaned and problematic surveys but keeps users
     * Yetim ve sorunlu anketleri siler ama kullanıcıları korur
     */
    @PostMapping("/clean-database")
    public ResponseEntity<?> cleanDatabase() {
        try {
            System.out.println("🧹 Database cleaning started...");
            
            // Get all users count before cleaning
            long userCount = userRepository.count();
            
            // Perform comprehensive survey cleanup using SurveyService
            // SurveyService kullanarak kapsamlı anket temizliği yap
            SurveyService.SurveyCleanupReport cleanupReport = surveyService.performComprehensiveCleanup(30);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Database cleaning completed");
            response.put("success", cleanupReport.isSuccess());
            response.put("usersKept", userCount);
            response.put("cleanupReport", Map.of(
                "orphanedSurveysDeleted", cleanupReport.getOrphanedSurveysDeleted(),
                "surveysWithInactiveCreatorSoftDeleted", cleanupReport.getSurveysWithInactiveCreatorSoftDeleted(),
                "surveysWithoutQuestionsCleaned", cleanupReport.getSurveysWithoutQuestionsCleaned(),
                "oldSurveysWithoutResponsesCleaned", cleanupReport.getOldSurveysWithoutResponsesCleaned(),
                "expiredSurveysClosed", cleanupReport.getExpiredSurveysClosed(),
                "scheduledSurveysActivated", cleanupReport.getScheduledSurveysActivated(),
                "totalProcessed", cleanupReport.getTotalProcessed()
            ));
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Database cleaning completed. Users kept: " + userCount + ", Total surveys processed: " + cleanupReport.getTotalProcessed());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Database cleaning error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Database cleaning failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Kullanıcı giriş endpoint'i
     * E-posta ve şifre ile kullanıcı doğrulaması yapar
     * Başarılı girişte JWT token ve kullanıcı bilgilerini döndürür
     */
    @PostMapping("/simple-login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        try {
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            
            System.out.println("🔐 Login attempt: " + email);
            
            // Input validation - Giriş doğrulama
            if (email == null || email.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing email");
                errorResponse.put("message", "Email is required");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (password == null || password.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing password");
                errorResponse.put("message", "Password is required");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Find user in database - Kullanıcıyı veritabanında bul
            Optional<User> userOptional = userRepository.findByEmail(email.trim().toLowerCase());
            
            if (userOptional.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "User not found");
                errorResponse.put("message", "No account found with this email address");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                System.out.println("❌ Login failed - User not found: " + email);
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            User user = userOptional.get();
            
            // Check if user is active - Kullanıcının aktif olup olmadığını kontrol et
            if (!user.getIsActive()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Account disabled");
                errorResponse.put("message", "Your account has been disabled");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                System.out.println("❌ Login failed - Account disabled: " + email);
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Verify password - Şifreyi doğrula
            if (!passwordEncoder.matches(password, user.getPassword())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid credentials");
                errorResponse.put("message", "Invalid email or password");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                System.out.println("❌ Login failed - Wrong password: " + email);
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Login successful - Giriş başarılı
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token", "jwt-token-" + System.currentTimeMillis()); // Real JWT will be implemented later
            response.put("tokenType", "Bearer");
            response.put("user", Map.of(
                "id", user.getId(),
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "email", user.getEmail(),
                "role", user.getRole().name()
            ));
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Login successful for: " + email + " (ID: " + user.getId() + ")");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Login failed");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Kullanıcı kayıt endpoint'i
     * Yeni kullanıcı hesabı oluşturur ve veritabanına kaydeder
     * Şifre BCrypt ile şifrelenir
     */
    @PostMapping("/simple-register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            String firstName = (String) request.get("firstName");
            String lastName = (String) request.get("lastName");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String confirmPassword = (String) request.get("confirmPassword");
            
            System.out.println("📝 Registration attempt: " + email);
            
            // Input validation - Giriş doğrulama
            if (firstName == null || firstName.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing first name");
                errorResponse.put("message", "First name is required");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (lastName == null || lastName.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing last name");
                errorResponse.put("message", "Last name is required");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (email == null || email.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing email");
                errorResponse.put("message", "Email is required");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (password == null || password.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Missing password");
                errorResponse.put("message", "Password is required");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (!password.equals(confirmPassword)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Password mismatch");
                errorResponse.put("message", "Passwords do not match");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            if (password.length() < 6) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Password too short");
                errorResponse.put("message", "Password must be at least 6 characters");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Email format validation - Email format doğrulama
            String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
            if (!email.matches(emailPattern)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid email format");
                errorResponse.put("message", "Please enter a valid email address");
                errorResponse.put("timestamp", LocalDateTime.now());
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Check if user already exists - Kullanıcının zaten var olup olmadığını kontrol et
            String normalizedEmail = email.trim().toLowerCase();
            Optional<User> existingUser = userRepository.findByEmail(normalizedEmail);
            
            if (existingUser.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Email already exists");
                errorResponse.put("message", "An account with this email address already exists");
                errorResponse.put("timestamp", LocalDateTime.now());
                
                System.out.println("❌ Registration failed - Email already exists: " + email);
                return ResponseEntity.status(409).body(errorResponse);
            }
            
            // Create new user - Yeni kullanıcı oluştur
            User newUser = new User();
            newUser.setFirstName(firstName.trim());
            newUser.setLastName(lastName.trim());
            newUser.setEmail(normalizedEmail);
            newUser.setPassword(passwordEncoder.encode(password)); // Encrypt password - Şifreyi şifrele
            newUser.setRole(UserRole.USER); // Default role - Varsayılan rol
            newUser.setIsActive(true); // Active by default - Varsayılan olarak aktif
            
            // Save user to database - Kullanıcıyı veritabanına kaydet
            User savedUser = userRepository.save(newUser);
            
            // Success response - Başarı yanıtı
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registration successful");
            response.put("token", "jwt-token-" + System.currentTimeMillis()); // Real JWT will be implemented later
            response.put("tokenType", "Bearer");
            response.put("user", Map.of(
                "id", savedUser.getId(),
                "firstName", savedUser.getFirstName(),
                "lastName", savedUser.getLastName(),
                "email", savedUser.getEmail(),
                "role", savedUser.getRole().toString()
            ));
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Registration successful for: " + email + " (ID: " + savedUser.getId() + ")");
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "Registration failed");
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Yeni anket oluşturma endpoint'i
     * Kullanıcı tarafından yeni anket oluşturulmasını sağlar
     * Anket başlığı, açıklaması ve soruları ile birlikte kaydedilir
     */
    @PostMapping("/create-survey")
    public ResponseEntity<?> createSurvey(@RequestBody Map<String, Object> surveyRequest) {
        try {
            System.out.println("📝 Creating new survey...");
            
            // Extract survey data - Anket verilerini al
            String title = (String) surveyRequest.get("title");
            String description = (String) surveyRequest.get("description");
            String category = (String) surveyRequest.get("category");
            String creatorEmail = (String) surveyRequest.get("creatorEmail");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) surveyRequest.get("questions");
            
            System.out.println("📊 Survey data - Title: " + title + ", Category: " + category);
            
            // Validation - Doğrulama
            if (title == null || title.trim().isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Survey title is required");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            // Find user or use first available user - Kullanıcı bul veya ilk kullanıcıyı kullan
            User creator = null;
            if (creatorEmail != null && !creatorEmail.trim().isEmpty()) {
                Optional<User> userOpt = userRepository.findByEmail(creatorEmail);
                creator = userOpt.orElse(null);
            }
            
            // If no user found, use first active user - Kullanıcı bulunamazsa ilk aktif kullanıcıyı kullan
            if (creator == null) {
                var users = userRepository.findAll();
                if (!users.isEmpty()) {
                    creator = users.get(0);
                    System.out.println("🔄 Using default user: " + creator.getEmail());
                } else {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "No users found in system");
                    return ResponseEntity.status(500).body(errorResponse);
                }
            }
            
            // Create new survey - Yeni anket oluştur
            Survey newSurvey = new Survey();
            newSurvey.setTitle(title.trim());
            newSurvey.setDescription(description != null ? description.trim() : "");
            newSurvey.setCreator(creator);
            newSurvey.setStatus(SurveyStatus.ACTIVE);
            newSurvey.setIsAnonymous(true);
            newSurvey.setAllowMultipleResponses(false);
            
            // Add category to description if provided - Kategori varsa açıklamaya ekle
            if (category != null && !category.trim().isEmpty()) {
                String categoryInfo = "Category: " + category.trim();
                if (newSurvey.getDescription().isEmpty()) {
                    newSurvey.setDescription(categoryInfo);
                } else {
                    newSurvey.setDescription(newSurvey.getDescription() + " | " + categoryInfo);
                }
            }
            
            // Save survey - Anketi kaydet
            Survey savedSurvey = surveyService.createSurvey(newSurvey);
            
            // Create questions if provided - Sorular verilmişse oluştur
            int questionCount = 0;
            if (questions != null && !questions.isEmpty()) {
                questionCount = createQuestionsForSurvey(savedSurvey, questions);
                System.out.println("✅ Created " + questionCount + " questions for survey");
            }
            
            // Create response - Yanıt oluştur
            Map<String, Object> surveyData = new HashMap<>();
            surveyData.put("id", savedSurvey.getId());
            surveyData.put("title", savedSurvey.getTitle());
            surveyData.put("description", savedSurvey.getDescription());
            surveyData.put("status", savedSurvey.getStatus());
            surveyData.put("createdDate", savedSurvey.getCreatedDate());
            surveyData.put("creatorId", savedSurvey.getCreator().getId());
            surveyData.put("creatorEmail", savedSurvey.getCreator().getEmail());
            surveyData.put("questionCount", questionCount);
            surveyData.put("responseCount", 0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Survey created successfully");
            response.put("survey", surveyData);
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Survey created successfully: " + savedSurvey.getTitle() + " (ID: " + savedSurvey.getId() + ")");
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            System.err.println("❌ Survey creation error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create survey: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get All Surveys Endpoint - Tüm Anketleri Getir Endpoint'i
     * Returns all active surveys - Tüm aktif anketleri döndürür
     */
    @GetMapping("/get-surveys")
    public ResponseEntity<?> getAllSurveys() {
        try {
            System.out.println("📊 Getting all active surveys...");
            
            var surveys = surveyService.getAllActiveSurveys();
            
            // Transform surveys to response format - Anketleri yanıt formatına dönüştür
            var surveyList = surveys.stream().map(survey -> {
                Map<String, Object> surveyData = new HashMap<>();
                surveyData.put("id", survey.getId());
                surveyData.put("title", survey.getTitle());
                surveyData.put("description", survey.getDescription());
                surveyData.put("status", survey.getStatus());
                surveyData.put("createdDate", survey.getCreatedDate());
                surveyData.put("creatorEmail", survey.getCreator() != null ? survey.getCreator().getEmail() : "Unknown");
                surveyData.put("questionCount", 0);
                surveyData.put("responseCount", 0);
                return surveyData;
            }).toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Surveys retrieved successfully");
            response.put("surveys", surveyList);
            response.put("totalSurveysCount", surveyList.size());
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Retrieved " + surveyList.size() + " active surveys");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error getting surveys: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get surveys: " + e.getMessage());
            errorResponse.put("surveys", new java.util.ArrayList<>());
            errorResponse.put("totalSurveysCount", 0);
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get Survey Details with Questions - Soru detayları ile anket bilgilerini al
     * Returns detailed survey information including sample questions
     */
    @GetMapping("/survey-details/{surveyId}")
    public ResponseEntity<?> getSurveyDetails(@PathVariable Long surveyId) {
        try {
            System.out.println("📋 Getting survey details for ID: " + surveyId);
            
            // Find the survey
            var surveyOpt = surveyRepository.findById(surveyId);
            if (surveyOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Survey not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            var survey = surveyOpt.get();
            
            // Get actual questions from database - Veritabanından gerçek soruları al
            var questions = getQuestionsForSurvey(survey);
            
            Map<String, Object> surveyDetails = new HashMap<>();
            surveyDetails.put("id", survey.getId());
            surveyDetails.put("title", survey.getTitle());
            surveyDetails.put("description", survey.getDescription());
            surveyDetails.put("status", survey.getStatus());
            surveyDetails.put("createdDate", survey.getCreatedDate());
            surveyDetails.put("creatorEmail", survey.getCreator() != null ? survey.getCreator().getEmail() : "Unknown");
            surveyDetails.put("questions", questions);
            surveyDetails.put("questionCount", questions.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Survey details retrieved successfully");
            response.put("survey", surveyDetails);
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Retrieved survey details: " + survey.getTitle() + " with " + questions.size() + " questions");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error getting survey details: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to get survey details: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Submit Survey Response - Anket yanıtını gönder
     * Handles survey response submission and saves to database
     */
    @PostMapping("/submit-survey-response")
    @Transactional
    public ResponseEntity<?> submitSurveyResponse(@RequestBody Map<String, Object> responseRequest) {
        try {
            System.out.println("📝 Submitting survey response...");
            
            Long surveyId = Long.valueOf(responseRequest.get("surveyId").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> responses = (Map<String, Object>) responseRequest.get("responses");
            String respondentName = (String) responseRequest.get("respondentName");
            
            System.out.println("📊 Survey ID: " + surveyId + ", Responses: " + responses.size());
            
            // Validate survey exists
            var surveyOpt = surveyRepository.findById(surveyId);
            if (surveyOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Survey not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            var survey = surveyOpt.get();
            
            // Find or create user (for now, use first available user)
            User respondent = null;
            var users = userRepository.findAll();
            if (!users.isEmpty()) {
                respondent = users.get(0);
                System.out.println("🔄 Using respondent: " + respondent.getEmail());
            }
            
            // Save each response to database
            int savedResponses = 0;
            for (Map.Entry<String, Object> entry : responses.entrySet()) {
                String questionIdStr = entry.getKey();
                Object answerValue = entry.getValue();
                
                try {
                    Long questionId = Long.valueOf(questionIdStr);
                    
                    // Find the question
                    var questionOpt = questionRepository.findById(questionId);
                    if (questionOpt.isPresent()) {
                        Question question = questionOpt.get();
                        
                        // Create response entity
                        Response response = new Response();
                        response.setSurvey(survey);
                        response.setQuestion(question);
                        response.setUser(respondent);
                        response.setResponseDate(LocalDateTime.now());
                        
                        // Handle different question types
                        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE && answerValue instanceof String) {
                            String answerText = (String) answerValue;
                            
                            // Find the selected option
                            var options = optionRepository.findByQuestionOrderByOrderIndex(question);
                            for (Option option : options) {
                                if (option.getOptionText().equals(answerText)) {
                                    response.setSelectedOption(option);
                                    break;
                                }
                            }
                            
                            // Set text response as fallback
                            if (response.getSelectedOption() == null) {
                                response.setTextResponse(answerText);
                            }
                        } else {
                            // For other question types, store as text
                            response.setTextResponse(answerValue != null ? answerValue.toString() : "");
                        }
                        
                        // Save the response
                        responseRepository.save(response);
                        savedResponses++;
                        
                        System.out.println("💾 Saved response for question " + questionId + ": " + answerValue);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Invalid question ID format: " + questionIdStr);
                }
            }
            
            System.out.println("📋 Survey Response Details:");
            System.out.println("   Survey: " + survey.getTitle());
            System.out.println("   Respondent: " + (respondent != null ? respondent.getEmail() : "Anonymous"));
            System.out.println("   Responses saved: " + savedResponses);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Survey response submitted successfully!");
            response.put("surveyId", surveyId);
            response.put("responseCount", savedResponses);
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Survey response submitted successfully with " + savedResponses + " responses saved");
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error submitting survey response: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to submit survey response: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Create sample questions for a survey - Anket için örnek sorular oluştur
     * This generates realistic questions based on the survey topic
     */
    private java.util.List<Map<String, Object>> createSampleQuestions(Survey survey) {
        java.util.List<Map<String, Object>> questions = new java.util.ArrayList<>();
        
        String title = survey.getTitle().toLowerCase();
        String description = survey.getDescription().toLowerCase();
        
        if (title.contains("sport") || description.contains("sport") || title.contains("athlete")) {
            // Sports-related questions
            questions.add(createQuestion(1, "How often do you exercise per week?", 
                java.util.Arrays.asList("Never", "1-2 times", "3-4 times", "5+ times"), "multiple_choice"));
            questions.add(createQuestion(2, "What is your favorite type of exercise?", 
                java.util.Arrays.asList("Running", "Weight lifting", "Swimming", "Team sports", "Yoga"), "multiple_choice"));
            questions.add(createQuestion(3, "Do you think performance enhancing supplements should be allowed in professional sports?", 
                java.util.Arrays.asList("Yes, completely", "Yes, with restrictions", "No, never", "Undecided"), "multiple_choice"));
            questions.add(createQuestion(4, "What motivates you to stay active?", 
                java.util.Arrays.asList(), "text"));
            
        } else if (title.contains("health") || description.contains("health") || title.contains("mental")) {
            // Health-related questions
            questions.add(createQuestion(1, "How would you rate your overall health?", 
                java.util.Arrays.asList("Excellent", "Good", "Fair", "Poor"), "multiple_choice"));
            questions.add(createQuestion(2, "How often do you visit a healthcare provider?", 
                java.util.Arrays.asList("Monthly", "Every 3-6 months", "Yearly", "Only when sick", "Never"), "multiple_choice"));
            questions.add(createQuestion(3, "What health topics are most important to you?", 
                java.util.Arrays.asList("Mental health", "Physical fitness", "Nutrition", "Preventive care", "Chronic disease management"), "multiple_choice"));
            questions.add(createQuestion(4, "What barriers prevent you from maintaining good health?", 
                java.util.Arrays.asList(), "text"));
                
        } else if (title.contains("work") || description.contains("work") || title.contains("remote")) {
            // Work-related questions
            questions.add(createQuestion(1, "How do you prefer to work?", 
                java.util.Arrays.asList("Fully remote", "Hybrid (2-3 days office)", "Mostly in office", "Fully in office"), "multiple_choice"));
            questions.add(createQuestion(2, "What is your biggest challenge with remote work?", 
                java.util.Arrays.asList("Communication", "Work-life balance", "Technology issues", "Feeling isolated", "Productivity"), "multiple_choice"));
            questions.add(createQuestion(3, "How has remote work affected your productivity?", 
                java.util.Arrays.asList("Significantly increased", "Slightly increased", "No change", "Slightly decreased", "Significantly decreased"), "multiple_choice"));
            questions.add(createQuestion(4, "What tools or resources would improve your remote work experience?", 
                java.util.Arrays.asList(), "text"));
                
        } else if (title.contains("climate") || description.contains("environment") || title.contains("climate")) {
            // Environment/Climate questions
            questions.add(createQuestion(1, "How concerned are you about climate change?", 
                java.util.Arrays.asList("Extremely concerned", "Very concerned", "Somewhat concerned", "Not very concerned", "Not at all concerned"), "multiple_choice"));
            questions.add(createQuestion(2, "What actions do you take to help the environment?", 
                java.util.Arrays.asList("Recycling", "Using renewable energy", "Reducing car usage", "Buying eco-friendly products", "None"), "multiple_choice"));
            questions.add(createQuestion(3, "Who should be primarily responsible for addressing climate change?", 
                java.util.Arrays.asList("Governments", "Corporations", "Individuals", "All equally", "International organizations"), "multiple_choice"));
            questions.add(createQuestion(4, "What would motivate you to be more environmentally conscious?", 
                java.util.Arrays.asList(), "text"));
                
        } else {
            // General questions for any survey
            questions.add(createQuestion(1, "How did you hear about this survey?", 
                java.util.Arrays.asList("Social media", "Email", "Website", "Friend/colleague", "Other"), "multiple_choice"));
            questions.add(createQuestion(2, "How would you rate your interest in this topic?", 
                java.util.Arrays.asList("Very interested", "Somewhat interested", "Neutral", "Not very interested", "Not interested at all"), "multiple_choice"));
            questions.add(createQuestion(3, "What is your age group?", 
                java.util.Arrays.asList("18-25", "26-35", "36-45", "46-55", "56+"), "multiple_choice"));
            questions.add(createQuestion(4, "Any additional comments or suggestions?", 
                java.util.Arrays.asList(), "text"));
        }
        
        return questions;
    }
    
    /**
     * Helper method to create a question object
     */
    private Map<String, Object> createQuestion(int id, String questionText, java.util.List<String> options, String type) {
        Map<String, Object> question = new HashMap<>();
        question.put("id", id);
        question.put("question", questionText);
        question.put("type", type);
        question.put("options", options);
        question.put("required", true);
        return question;
    }

    /**
     * Anket silme endpoint'i
     * Belirtilen ID'ye sahip anketi sistemden tamamen kaldırır
     * Önce yanıtları, sonra seçenekleri, sonra soruları, en son anketi siler
     */
    @DeleteMapping("/delete-survey/{surveyId}")
    @Transactional
    public ResponseEntity<?> deleteSurvey(@PathVariable Long surveyId) {
        try {
            System.out.println("🗑️ Deleting survey with ID: " + surveyId);
            
            // Find the existing survey
            var surveyOpt = surveyRepository.findById(surveyId);
            if (surveyOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Survey not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            var survey = surveyOpt.get();
            
            // Delete in correct order: responses -> options -> questions -> survey
            try {
                System.out.println("🗑️ Deleting responses for survey ID: " + surveyId);
                // Delete responses first (they reference options)
                responseRepository.deleteBySurveyId(surveyId);
                System.out.println("✅ Deleted responses for survey ID: " + surveyId);
                
                System.out.println("🗑️ Deleting options for survey ID: " + surveyId);
                // Then delete options (they reference questions)
                optionRepository.deleteByQuestionSurveyId(surveyId);
                System.out.println("✅ Deleted options for survey ID: " + surveyId);
                
                System.out.println("🗑️ Deleting questions for survey ID: " + surveyId);
                // Then delete questions (they reference survey)
                questionRepository.deleteBySurveyId(surveyId);
                System.out.println("✅ Deleted questions for survey ID: " + surveyId);
                
            } catch (Exception deleteError) {
                System.err.println("❌ Error deleting survey components: " + deleteError.getMessage());
                deleteError.printStackTrace();
                throw deleteError; // Re-throw to trigger rollback
            }
            
            // Finally delete the survey
            surveyRepository.deleteById(surveyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Survey deleted successfully");
            response.put("deletedSurveyId", surveyId);
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Survey deleted successfully: " + survey.getTitle() + " (ID: " + surveyId + ")");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Survey deletion error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to delete survey: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Anket güncelleme endpoint'i
     * Mevcut anketin başlık, açıklama ve sorularını günceller
     * Güncelleme işlemi transaction içinde yapılır
     */
    @PutMapping("/update-survey/{surveyId}")
    @Transactional
    public ResponseEntity<?> updateSurvey(@PathVariable Long surveyId, @RequestBody Map<String, Object> surveyRequest) {
        try {
            System.out.println("✏️ Updating survey with ID: " + surveyId);
            
            // Find the existing survey
            var surveyOpt = surveyRepository.findById(surveyId);
            if (surveyOpt.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Survey not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            var existingSurvey = surveyOpt.get();
            
            // Extract update data
            String title = (String) surveyRequest.get("title");
            String description = (String) surveyRequest.get("description");
            String category = (String) surveyRequest.get("category");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> questions = (List<Map<String, Object>>) surveyRequest.get("questions");
            
            System.out.println("📊 Updating survey - Title: " + title + ", Category: " + category);
            
            // Update survey fields
            if (title != null && !title.trim().isEmpty()) {
                existingSurvey.setTitle(title.trim());
            }
            
            if (description != null) {
                existingSurvey.setDescription(description.trim());
            } else if (category != null && !category.trim().isEmpty()) {
                // If no description provided but category is, create description from category
                existingSurvey.setDescription("Category: " + category.trim());
            }
            
            // Save updated survey
            var updatedSurvey = surveyRepository.save(existingSurvey);
            
            // Update questions if provided - Sorular verilmişse güncelle
            int questionCount = 0;
            if (questions != null && !questions.isEmpty()) {
                // First, delete existing questions - Önce mevcut soruları sil
                System.out.println("🗑️ Deleting existing questions for survey ID: " + updatedSurvey.getId());
                
                try {
                    // Delete options first, then questions
                    optionRepository.deleteByQuestionSurveyId(updatedSurvey.getId());
                    questionRepository.deleteBySurveyId(updatedSurvey.getId());
                    System.out.println("✅ Deleted existing questions for survey ID: " + updatedSurvey.getId());
                } catch (Exception deleteError) {
                    System.err.println("❌ Error deleting questions: " + deleteError.getMessage());
                    deleteError.printStackTrace();
                }
                
                // Then create new questions - Sonra yeni soruları oluştur
                questionCount = createQuestionsForSurvey(updatedSurvey, questions);
                System.out.println("✅ Updated " + questionCount + " questions for survey");
            }
            
            // Create response
            Map<String, Object> surveyData = new HashMap<>();
            surveyData.put("id", updatedSurvey.getId());
            surveyData.put("title", updatedSurvey.getTitle());
            surveyData.put("description", updatedSurvey.getDescription());
            surveyData.put("status", updatedSurvey.getStatus());
            surveyData.put("createdDate", updatedSurvey.getCreatedDate());
            surveyData.put("creatorId", updatedSurvey.getCreator().getId());
            surveyData.put("creatorEmail", updatedSurvey.getCreator().getEmail());
            surveyData.put("questionCount", questionCount);
            surveyData.put("responseCount", 0);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Survey updated successfully");
            response.put("survey", surveyData);
            response.put("timestamp", LocalDateTime.now());
            
            System.out.println("✅ Survey updated successfully: " + updatedSurvey.getTitle() + " (ID: " + updatedSurvey.getId() + ")");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("❌ Survey update error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update survey: " + e.getMessage());
            errorResponse.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Helper method to get questions for a survey from database
     * Bir anketin sorularını veritabanından getiren yardımcı metod
     */
    private List<Map<String, Object>> getQuestionsForSurvey(Survey survey) {
        try {
            System.out.println("📋 Getting questions for survey ID: " + survey.getId());
            
            // Get questions from database - Veritabanından soruları al
            List<Question> questions = questionRepository.findBySurveyOrderByOrderIndex(survey);
            
            if (questions.isEmpty()) {
                System.out.println("⚠️ No questions found for survey ID: " + survey.getId() + ", returning sample questions");
                // If no questions in database, return sample questions as fallback
                // Veritabanında soru yoksa, yedek olarak örnek sorular döndür
                return createSampleQuestions(survey);
            }
            
            // Convert questions to response format - Soruları yanıt formatına çevir
            return questions.stream().map(question -> {
                Map<String, Object> questionMap = new HashMap<>();
                questionMap.put("id", question.getId());
                questionMap.put("question", question.getQuestionText());
                questionMap.put("type", getStandardizedQuestionType(question.getQuestionType()));
                questionMap.put("required", question.getIsRequired());
                
                // Get options for this question - Bu soru için seçenekleri al
                List<Option> options = optionRepository.findByQuestionOrderByOrderIndex(question);
                List<String> optionTexts = options.stream()
                    .map(Option::getOptionText)
                    .toList();
                questionMap.put("options", optionTexts);
                
                return questionMap;
            }).toList();
            
        } catch (Exception e) {
            System.err.println("❌ Error getting questions for survey ID: " + survey.getId() + " - " + e.getMessage());
            // Return sample questions as fallback - Yedek olarak örnek sorular döndür
            return createSampleQuestions(survey);
        }
    }

    /**
     * Helper method to create questions for a survey
     * Bir anket için sorular oluşturan yardımcı metod
     */
    private int createQuestionsForSurvey(Survey survey, List<Map<String, Object>> questionsData) {
        int createdCount = 0;
        
        for (int i = 0; i < questionsData.size(); i++) {
            Map<String, Object> questionData = questionsData.get(i);
            
            // Extract question data - Soru verilerini al
            String questionText = (String) questionData.get("question");
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) questionData.get("options");
            
            // Skip empty questions - Boş soruları atla
            if (questionText == null || questionText.trim().isEmpty()) {
                continue;
            }
            
            // Create question entity - Soru entity'si oluştur
            Question question = new Question();
            question.setSurvey(survey);
            question.setQuestionText(questionText.trim());
            question.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            question.setOrderIndex(i + 1);
            question.setIsRequired(true);
            
            // Save question - Soruyu kaydet
            Question savedQuestion = questionRepository.save(question);
            
            // Create options if provided - Seçenekler verilmişse oluştur
            if (options != null && !options.isEmpty()) {
                for (int j = 0; j < options.size(); j++) {
                    String optionText = options.get(j);
                    if (optionText != null && !optionText.trim().isEmpty()) {
                        Option option = new Option();
                        option.setQuestion(savedQuestion);
                        option.setOptionText(optionText.trim());
                        option.setOrderIndex(j + 1);
                        optionRepository.save(option);
                    }
                }
            }
            
            createdCount++;
        }
        
        return createdCount;
    }

    /**
     * Helper method to get standardized question type string
     * Standart soru tipi string'i döndüren yardımcı metod
     */
    private String getStandardizedQuestionType(QuestionType questionType) {
        switch (questionType) {
            case MULTIPLE_CHOICE:
                return "multiple_choice";
            case MULTIPLE_SELECT:
                return "multiple_select";
            case TEXT_INPUT:
                return "text_input";
            case NUMERIC_INPUT:
                return "numeric_input";
            case RATING_SCALE:
                return "rating_scale";
            default:
                return "multiple_choice"; // Default to multiple choice
        }
    }

    /**
     * Helper method to delete all questions for a survey
     * Bir anketin tüm sorularını silen yardımcı metod
     */
    @Transactional
    private void deleteQuestionsForSurvey(Survey survey) {
        try {
            System.out.println("🗑️ Deleting existing questions for survey ID: " + survey.getId());
            
            // First delete all options for questions in this survey
            // Önce bu anketteki soruların tüm seçeneklerini sil
            optionRepository.deleteByQuestionSurveyId(survey.getId());
            
            // Then delete all questions for this survey
            // Sonra bu anketin tüm sorularını sil
            questionRepository.deleteBySurvey(survey);
            
            System.out.println("✅ Deleted existing questions and options for survey ID: " + survey.getId());
        } catch (Exception e) {
            System.err.println("❌ Error deleting questions for survey ID: " + survey.getId() + " - " + e.getMessage());
        }
    }
}
