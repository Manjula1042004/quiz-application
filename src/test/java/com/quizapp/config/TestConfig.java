package com.quizapp.config;

import com.quizapp.service.EmailService;
import com.quizapp.entity.User;
import com.quizapp.entity.QuizAttempt;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public EmailService testEmailService() {
        return new EmailService() {

            @Override
            public void sendTestEmail(String toEmail, String templateType) {
                System.out.println("📧 [TEST MOCK] Test email suppressed to: " + toEmail);
            }

            @Override
            public void sendRegistrationEmail(User user) {
                System.out.println("📧 [TEST MOCK] Registration email suppressed for user: " +
                        (user != null ? user.getEmail() : "null"));
            }

            @Override
            public void sendPasswordResetEmail(User user, String resetToken) {
                System.out.println("📧 [TEST MOCK] Password reset email suppressed");
            }

            @Override
            public void sendQuizResultEmail(QuizAttempt quizAttempt) {
                System.out.println("📧 [TEST MOCK] Quiz result email suppressed");
            }

            @Override
            public void sendSimpleEmail(String to, String subject, String text) {
                System.out.println("📧 [TEST MOCK] Simple email suppressed");
            }
        };
    }
}