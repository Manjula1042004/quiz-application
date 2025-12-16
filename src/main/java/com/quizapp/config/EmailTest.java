package com.quizapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailTest implements CommandLineRunner {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void run(String... args) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("thilagavathi222006@gmail.com");
            message.setSubject("✅ QuizApp Email Test");
            message.setText("If you receive this, email is working!");
            mailSender.send(message);
            System.out.println("✅ EMAIL TEST PASSED!");
        } catch (Exception e) {
            System.out.println("❌ EMAIL TEST FAILED: " + e.getMessage());
            e.printStackTrace(); // This will show detailed error
        }
    }
}