package com.quizapp.config;

import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.service.EmailService;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class EmailDiagnosticTest implements CommandLineRunner {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🚀 EMAIL DIAGNOSTIC TEST START");
        System.out.println("=".repeat(50));

        // Test 1: Simple Direct Email Test
        testSimpleEmail();

        // Test 2: Registration Email Test
        testRegistrationEmail();

        // Test 3: Quiz Results Email Test
        testQuizResultsEmail();

        System.out.println("=".repeat(50));
        System.out.println("✅ EMAIL DIAGNOSTIC TEST COMPLETE");
        System.out.println("=".repeat(50) + "\n");
    }

    private void testSimpleEmail() {
        try {
            System.out.println("\n🧪 TEST 1: Testing Simple Direct Email...");
            emailService.sendTestEmail("thilagavathi222006@gmail.com", "Direct SMTP");
            System.out.println("✅ Simple email test completed");
        } catch (Exception e) {
            System.out.println("❌ Simple email test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testRegistrationEmail() {
        try {
            System.out.println("\n🧪 TEST 2: Testing Registration Email...");

            // Find an existing user to test with
            Optional<User> testUser = userService.findByUsername("participant");
            if (testUser.isPresent()) {
                User user = testUser.get();
                System.out.println("📧 Sending registration email to: " + user.getEmail());
                emailService.sendRegistrationEmail(user);
                System.out.println("✅ Registration email test completed for: " + user.getEmail());
            } else {
                System.out.println("⚠️ No test user found for registration email test");
                // Create a mock user for testing
                User mockUser = new User();
                mockUser.setUsername("TestUser");
                mockUser.setEmail("thilagavathi222006@gmail.com");
                mockUser.setRole(com.quizapp.entity.Role.PARTICIPANT);
                emailService.sendRegistrationEmail(mockUser);
                System.out.println("✅ Registration email test completed with mock user");
            }

        } catch (Exception e) {
            System.out.println("❌ Registration email test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    private void testQuizResultsEmail() {
        try {
            System.out.println("\n🧪 TEST 3: Testing Quiz Results Email...");

            // Find a completed quiz attempt
            Optional<User> testUser = userService.findByUsername("participant");
            if (testUser.isPresent()) {
                User user = testUser.get();
                List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(user.getId());

                if (!attempts.isEmpty()) {
                    System.out.println("📊 Found " + attempts.size() + " quiz attempts");

                    // Find a completed attempt
                    Optional<QuizAttempt> completedAttempt = attempts.stream()
                            .filter(attempt -> attempt.getCompletedAt() != null && attempt.getScore() != null)
                            .findFirst();

                    if (completedAttempt.isPresent()) {
                        QuizAttempt attempt = completedAttempt.get();

                        if (attempt.getQuiz() != null) {
                            String quizTitle = attempt.getQuiz().getTitle();
                            System.out.println("📧 Sending quiz results email for attempt: " + attempt.getId());
                            System.out.println("📝 Quiz: " + quizTitle);
                            System.out.println("🎯 Score: " + attempt.getScore() + "%");

                            emailService.sendQuizResultEmail(attempt);
                            System.out.println("✅ Quiz results email test completed");
                        } else {
                            System.out.println("⚠️ Quiz is null in attempt");
                        }
                    } else {
                        System.out.println("⚠️ No completed quiz attempts found");
                    }
                } else {
                    System.out.println("⚠️ No quiz attempts found for user");
                }
            } else {
                System.out.println("⚠️ No test user found for quiz results email test");
            }

        } catch (Exception e) {
            System.out.println("❌ Quiz results email test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}