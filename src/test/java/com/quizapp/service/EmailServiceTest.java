package com.quizapp.service;

import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {





    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    private User user;
    private QuizAttempt quizAttempt;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.PARTICIPANT);

        quizAttempt = new QuizAttempt();
        quizAttempt.setId(1L);
        quizAttempt.setUser(user);
        quizAttempt.setScore(85.0);
        quizAttempt.setCompletedAt(LocalDateTime.now());
    }

    @Test
    void sendRegistrationEmail_Success() {
        // Arrange
        when(templateEngine.process(eq("email/registration"), any(Context.class)))
                .thenReturn("<html>Registration Email</html>");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // Act
        emailService.sendRegistrationEmail(user);

        // Assert
        verify(templateEngine, times(1)).process(eq("email/registration"), any(Context.class));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void sendRegistrationEmail_NullEmail() {
        // Arrange
        user.setEmail(null);

        // Act
        emailService.sendRegistrationEmail(user);

        // Assert
        verify(templateEngine, never()).process(anyString(), any(Context.class));
        verify(mailSender, never()).createMimeMessage();
    }

    @Test
    void sendQuizResultEmail_Success() {
        // Arrange
        when(templateEngine.process(eq("email/quiz-results"), any(Context.class)))
                .thenReturn("<html>Quiz Results</html>");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // Act
        emailService.sendQuizResultEmail(quizAttempt);

        // Assert
        verify(templateEngine, times(1)).process(eq("email/quiz-results"), any(Context.class));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void sendQuizResultEmail_NullAttempt() {
        // Act
        emailService.sendQuizResultEmail(null);

        // Assert
        verify(templateEngine, never()).process(anyString(), any(Context.class));
    }

    @Test
    void sendQuizResultEmail_NullUser() {
        // Arrange
        quizAttempt.setUser(null);

        // Act
        emailService.sendQuizResultEmail(quizAttempt);

        // Assert
        verify(templateEngine, never()).process(anyString(), any(Context.class));
    }

    @Test
    void sendPasswordResetEmail_Success() {
        // Arrange
        when(templateEngine.process(eq("email/password-reset"), any(Context.class)))
                .thenReturn("<html>Password Reset</html>");
        when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

        // Act
        emailService.sendPasswordResetEmail(user, "reset-token");

        // Assert
        verify(templateEngine, times(1)).process(eq("email/password-reset"), any(Context.class));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    void sendPasswordResetEmail_NullToken() {
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendPasswordResetEmail(user, null);
        });

        assertTrue(exception.getMessage().contains("token is required"));
    }

    @Test
    void sendTestEmail_Success() {
        // Act
        emailService.sendTestEmail("test@example.com", "welcome");

        // Assert
        verify(mailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void sendSimpleEmail_Success() {
        // Act
        emailService.sendSimpleEmail("test@example.com", "Subject", "Text");

        // Assert
        verify(mailSender, times(1)).send(any(org.springframework.mail.SimpleMailMessage.class));
    }
}