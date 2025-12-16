package com.quizapp.service;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Primary
@Profile("test")
public class MockEmailService {

    // This is a completely new service that won't extend EmailService
    // but will be used instead of it during tests

    public void sendTestEmail(String toEmail) {
        System.out.println("📧 [TEST MOCK] Test email would be sent to: " + toEmail);
    }

    public void sendRegistrationEmail(String toEmail, String username, String token) {
        System.out.println("📧 [TEST MOCK] Registration email would be sent to: " + toEmail);
    }

    // Add any other email methods your tests might call
}