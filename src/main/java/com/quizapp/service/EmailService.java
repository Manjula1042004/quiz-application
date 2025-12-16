package com.quizapp.service;

import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
@Transactional
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // 1. REGISTRATION CONFIRMATION EMAIL - COMPLETE
    public void sendRegistrationEmail(User user) {
        String userEmail = user.getEmail();
        System.out.println("🚀 Starting registration email process for: " + userEmail);

        try {
            if (userEmail == null || userEmail.trim().isEmpty()) {
                System.err.println("❌ Cannot send registration email: User email is null or empty");
                return;
            }

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("role", user.getRole().name());
            context.setVariable("email", userEmail);
            context.setVariable("registrationDate", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

            if (user.getRole().name().equals("ADMIN")) {
                context.setVariable("welcomeMessage", "Welcome to QuizApp Administrator Panel!");
                context.setVariable("features", Arrays.asList(
                        "Create and Manage Quizzes",
                        "Add Multiple Choice Questions",
                        "Monitor Participant Progress",
                        "View Detailed Analytics"
                ));
                context.setVariable("nextSteps", "Start by creating your first quiz in the Admin Dashboard.");
            } else {
                context.setVariable("welcomeMessage", "Welcome to QuizApp - Test Your Knowledge!");
                context.setVariable("features", Arrays.asList(
                        "Take Various Quizzes",
                        "Track Your Learning Progress",
                        "Compete with Friends",
                        "Earn Certificates"
                ));
                context.setVariable("nextSteps", "Browse available quizzes and start testing your knowledge!");
            }

            String htmlContent = templateEngine.process("email/registration", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("🎉 Welcome to QuizApp - Account Created Successfully!");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ REGISTRATION EMAIL SENT SUCCESSFULLY to: " + userEmail);

        } catch (Exception e) {
            System.err.println("❌ Registration email failed for " + userEmail + ": " + e.getMessage());
            sendSimpleRegistrationEmail(user);
        }
    }

    // 2. QUIZ RESULTS EMAIL - COMPLETE WITH ALL FIXES
    @Transactional(readOnly = true)
    public void sendQuizResultEmail(QuizAttempt attempt) {
        if (attempt == null || attempt.getUser() == null) {
            System.err.println("❌ Cannot send quiz results: Attempt or user is null");
            return;
        }

        User user = attempt.getUser();
        String userEmail = user.getEmail();

        try {
            if (userEmail == null || userEmail.trim().isEmpty()) {
                System.err.println("❌ Cannot send quiz results email: User email is null or empty");
                return;
            }

            if (attempt.getCompletedAt() == null) {
                System.err.println("❌ Cannot send quiz results email: Quiz attempt not completed");
                return;
            }

            // Extract data safely with null checks
            String quizTitle = "Quiz";
            int totalQuestions = 0;
            double scoreValue = 0.0;

            try {
                if (attempt.getQuiz() != null) {
                    quizTitle = attempt.getQuiz().getTitle();
                    if (attempt.getQuiz().getQuestions() != null) {
                        totalQuestions = attempt.getQuiz().getQuestions().size();
                    }
                }
                if (attempt.getScore() != null) {
                    scoreValue = attempt.getScore();
                }
            } catch (Exception e) {
                System.err.println("⚠️ Could not extract quiz data, using defaults");
            }

            int correctAnswers = totalQuestions > 0 ? (int) Math.round((scoreValue / 100) * totalQuestions) : 0;

            // Determine score category
            String scoreClass = "score-average";
            String performanceEmoji = "👍";

            if (scoreValue >= 80) {
                scoreClass = "score-excellent";
                performanceEmoji = "🏆";
            } else if (scoreValue >= 60) {
                scoreClass = "score-good";
                performanceEmoji = "⭐";
            } else if (scoreValue < 40) {
                scoreClass = "score-poor";
                performanceEmoji = "📚";
            }

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("quizTitle", quizTitle);
            context.setVariable("score", String.format("%.2f", scoreValue));
            context.setVariable("totalQuestions", totalQuestions);
            context.setVariable("correctAnswers", correctAnswers);
            context.setVariable("completionDate", attempt.getCompletedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm")));
            context.setVariable("performance", getPerformanceFeedback(scoreValue));
            context.setVariable("performanceEmoji", performanceEmoji);
            context.setVariable("scoreClass", scoreClass);

            String htmlContent = templateEngine.process("email/quiz-results", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("📊 Quiz Results: " + quizTitle + " - Score: " + String.format("%.2f", scoreValue) + "%");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ QUIZ RESULTS EMAIL SENT SUCCESSFULLY to: " + userEmail);

        } catch (Exception e) {
            System.err.println("❌ Quiz results email failed for " + userEmail + ": " + e.getMessage());
            sendSimpleQuizResultEmail(attempt, userEmail);
        }
    }

    // 3. PASSWORD RESET EMAIL - COMPLETE
    public void sendPasswordResetEmail(User user, String token) {
        String userEmail = user.getEmail();
        System.out.println("🚀 Starting password reset email process for: " + userEmail);

        try {
            if (userEmail == null || userEmail.trim().isEmpty()) {
                throw new RuntimeException("User email is required for password reset");
            }

            if (token == null || token.trim().isEmpty()) {
                throw new RuntimeException("Password reset token is required");
            }

            Context context = new Context();
            context.setVariable("username", user.getUsername());
            context.setVariable("resetLink", "http://localhost:8080/reset-password?token=" + token);
            context.setVariable("expiryTime", "24 hours");
            context.setVariable("supportEmail", "support@quizapp.com");

            String htmlContent = templateEngine.process("email/password-reset", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(userEmail);
            helper.setSubject("🔒 Password Reset Request - QuizApp");
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("✅ PASSWORD RESET EMAIL SENT SUCCESSFULLY to: " + userEmail);

        } catch (Exception e) {
            System.err.println("❌ Password reset email failed for " + userEmail + ": " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    // 4. TEST EMAIL - COMPLETE
    public void sendTestEmail(String toEmail, String testType) {
        try {
            System.out.println("🧪 Sending " + testType + " test email to: " + toEmail);

            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("✅ QuizApp - " + testType + " Test");
            message.setText(
                    "This is a " + testType + " test email from QuizApp.\n\n" +
                            "If you receive this email, it means:\n" +
                            "✅ SMTP configuration is correct\n" +
                            "✅ JavaMailSender is working\n" +
                            "✅ Email delivery is functional\n\n" +
                            "Timestamp: " + java.time.LocalDateTime.now() + "\n\n" +
                            "Best regards,\nQuizApp Team"
            );

            mailSender.send(message);
            System.out.println("✅ " + testType + " TEST EMAIL SENT SUCCESSFULLY to: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ " + testType + " test email failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 5. SIMPLE EMAIL METHOD - COMPLETE
    public void sendSimpleEmail(String toEmail, String subject, String text) {
        try {
            System.out.println("📧 Sending simple email to: " + toEmail);

            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("✅ Simple email sent successfully to: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Simple email failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // FALLBACK METHODS
    private void sendSimpleRegistrationEmail(User user) {
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Welcome to QuizApp!");
            message.setText(
                    "Dear " + user.getUsername() + ",\n\n" +
                            "Welcome to QuizApp! Your account has been successfully created.\n\n" +
                            "Account Details:\n" +
                            "- Username: " + user.getUsername() + "\n" +
                            "- Role: " + user.getRole() + "\n\n" +
                            "You can now login and start using QuizApp.\n\n" +
                            "Best regards,\nQuizApp Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Fallback registration email also failed: " + e.getMessage());
        }
    }

    private void sendSimpleQuizResultEmail(QuizAttempt attempt, String userEmail) {
        try {
            String quizTitle = "Quiz";
            try {
                quizTitle = attempt.getQuiz().getTitle();
            } catch (Exception e) {
                System.err.println("⚠️ Could not get quiz title, using default");
            }

            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setTo(userEmail);
            message.setSubject("Quiz Results - " + quizTitle);
            message.setText(
                    "Dear " + attempt.getUser().getUsername() + ",\n\n" +
                            "You have completed the quiz: " + quizTitle + "\n" +
                            "Your Score: " + String.format("%.2f", attempt.getScore()) + "%\n" +
                            "Completed on: " + attempt.getCompletedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm")) + "\n\n" +
                            getPerformanceFeedback(attempt.getScore()) + "\n\n" +
                            "Thank you for participating!\n\n" +
                            "Best regards,\nQuizApp Team"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("❌ Fallback quiz results email also failed: " + e.getMessage());
        }
    }

    // HELPER METHODS
    private String getPerformanceFeedback(double score) {
        if (score >= 90) return "Outstanding! You've mastered this topic! 🎯";
        if (score >= 80) return "Excellent work! You have a strong understanding. 🌟";
        if (score >= 70) return "Good job! You're on the right track. 👍";
        if (score >= 60) return "Not bad! Keep practicing to improve. 💪";
        if (score >= 50) return "You passed! Review the material and try again. 📚";
        return "Don't give up! Review the material and retake the quiz. 🔄";
    }
}