package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;  // Add this
import com.quizapp.repository.UserRepository;  // Add this
import com.quizapp.repository.QuizRepository;  // Add this
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class TestController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuizService quizService;

    @Autowired  // Add this
    private UserRepository userRepository;

    @Autowired  // Add this
    private QuizRepository quizRepository;

    @GetMapping("/api/test/users")
    public List<UserInfo> getAllUsers() {
        return userService.findAllUsers().stream()
                .map(user -> new UserInfo(user.getUsername(), user.getEmail(), user.getEnabled(), user.getRole()))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/test/enable-user/{username}")
    public String enableUser(@PathVariable String username) {
        try {
            userService.enableUserByUsername(username);
            return "✅ User " + username + " enabled successfully";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    @GetMapping("/api/debug/quizzes")
    public Map<String, Object> debugQuizzes() {
        Map<String, Object> response = new HashMap<>();

        List<Quiz> allQuizzes = quizService.getAllQuizzes();
        List<Quiz> publicQuizzes = quizService.getPublicQuizzes();

        List<Map<String, Object>> allQuizzesInfo = allQuizzes.stream()
                .map(quiz -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("id", quiz.getId());
                    info.put("title", quiz.getTitle());
                    info.put("isPublic", quiz.getIsPublic());
                    info.put("enabled", quiz.getEnabled());
                    info.put("isTemplate", quiz.getIsTemplate());
                    info.put("questionsCount", quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
                    return info;
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> publicQuizzesInfo = publicQuizzes.stream()
                .map(quiz -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("id", quiz.getId());
                    info.put("title", quiz.getTitle());
                    info.put("questionsCount", quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
                    return info;
                })
                .collect(Collectors.toList());

        response.put("allQuizzes", allQuizzesInfo);
        response.put("publicQuizzes", publicQuizzesInfo);
        response.put("allCount", allQuizzes.size());
        response.put("publicCount", publicQuizzes.size());

        return response;
    }

    @GetMapping("/api/create-test-quizzes")
    public String createTestQuizzes() {
        try {
            // Get the first admin or create one
            List<User> allUsers = userRepository.findAll();
            if (allUsers.isEmpty()) {
                return "❌ No users found. Please register first.";
            }

            User admin = allUsers.stream()
                    .filter(user -> user.getRole() == Role.ADMIN)
                    .findFirst()
                    .orElse(allUsers.get(0)); // Use first user if no admin

            System.out.println("👑 Creating quizzes with user: " + admin.getUsername());

            // Create a simple quiz for testing
            Quiz quiz = new Quiz();
            quiz.setTitle("Test Quiz 1");
            quiz.setDescription("A simple test quiz");
            quiz.setTimeLimit(30);
            quiz.setCreatedBy(admin);
            quiz.setDifficultyLevel(com.quizapp.entity.DifficultyLevel.MEDIUM);
            quiz.setIsPublic(true);
            quiz.setEnabled(true);
            quiz.setIsTemplate(false);
            quiz.setCreatedAt(LocalDateTime.now());

            quizRepository.save(quiz);

            long quizCount = quizRepository.count();
            return "✅ Created test quiz! Total quizzes now: " + quizCount;

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error: " + e.getMessage();
        }
    }

    @GetMapping("/api/fix/all-quizzes-public")
    public String makeAllQuizzesPublic() {
        try {
            List<Quiz> allQuizzes = quizRepository.findAll();
            int fixedCount = 0;

            for (Quiz quiz : allQuizzes) {
                boolean needsFix = false;

                if (!Boolean.TRUE.equals(quiz.getIsPublic())) {
                    quiz.setIsPublic(true);
                    needsFix = true;
                }

                if (!Boolean.TRUE.equals(quiz.getEnabled())) {
                    quiz.setEnabled(true);
                    needsFix = true;
                }

                if (Boolean.TRUE.equals(quiz.getIsTemplate())) {
                    quiz.setIsTemplate(false);
                    needsFix = true;
                }

                if (needsFix) {
                    quizRepository.save(quiz);
                    fixedCount++;
                }
            }

            return "✅ Fixed " + fixedCount + " quizzes to be public";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    public static class UserInfo {
        public String username;
        public String email;
        public Boolean enabled;
        public String role;

        public UserInfo(String username, String email, Boolean enabled, Object role) {
            this.username = username;
            this.email = email;
            this.enabled = enabled;
            this.role = role.toString();
        }
    }
}