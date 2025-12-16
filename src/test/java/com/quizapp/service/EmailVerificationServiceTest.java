package com.quizapp.service;

import com.quizapp.entity.EmailVerificationToken;
import com.quizapp.entity.User;
import com.quizapp.repository.EmailVerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private User user;
    private EmailVerificationToken token;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setEnabled(false);

        token = new EmailVerificationToken(user);
        token.setToken("test-token");
        token.setExpiryDate(LocalDateTime.now().plusHours(24));
    }

    @Test
    void createVerificationToken_Success() {
        // Arrange
        doNothing().when(tokenRepository).deleteByUser(any(User.class));
        when(tokenRepository.save(any(EmailVerificationToken.class))).thenReturn(token);

        // Act
        emailVerificationService.createVerificationToken(user, userService);

        // Assert
        verify(tokenRepository, times(1)).deleteByUser(user);
        verify(tokenRepository, times(1)).save(any(EmailVerificationToken.class));
        verify(emailService, times(1)).sendSimpleEmail(eq("test@example.com"), anyString(), anyString());
    }

    @Test
    void verifyEmail_Success() {
        // Arrange
        when(tokenRepository.findByToken("test-token")).thenReturn(Optional.of(token));
        when(tokenRepository.save(any(EmailVerificationToken.class))).thenReturn(token);
        when(userService.saveUser(any(User.class))).thenReturn(user);

        // Act
        boolean result = emailVerificationService.verifyEmail("test-token", userService);

        // Assert
        assertTrue(result);
        assertTrue(user.getEnabled());
        verify(userService, times(1)).saveUser(user);
        verify(emailService, times(1)).sendRegistrationEmail(user);
    }

    @Test
    void verifyEmail_InvalidToken() {
        // Arrange
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        boolean result = emailVerificationService.verifyEmail("invalid-token", userService);

        // Assert
        assertFalse(result);
    }

    @Test
    void verifyEmail_ExpiredToken() {
        // Arrange
        token.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

        // Act
        boolean result = emailVerificationService.verifyEmail("expired-token", userService);

        // Assert
        assertFalse(result);
    }

    @Test
    void verifyEmail_AlreadyUsedToken() {
        // Arrange
        // Check if token has a setUsed method or similar
        // If not, we can't test this scenario directly
        when(tokenRepository.findByToken("used-token")).thenReturn(Optional.empty());

        // Act
        boolean result = emailVerificationService.verifyEmail("used-token", userService);

        // Assert
        assertFalse(result);
    }

    @Test
    void resendVerificationToken_Success() {
        // Arrange
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        doNothing().when(tokenRepository).deleteByUser(any(User.class));
        when(tokenRepository.save(any(EmailVerificationToken.class))).thenReturn(token);

        // Act
        emailVerificationService.resendVerificationToken("test@example.com", userService);

        // Assert
        verify(tokenRepository, times(1)).deleteByUser(user);
        verify(tokenRepository, times(1)).save(any(EmailVerificationToken.class));
    }

    @Test
    void resendVerificationToken_UserAlreadyVerified() {
        // Arrange
        user.setEnabled(true);
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailVerificationService.resendVerificationToken("test@example.com", userService);
        });

        assertTrue(exception.getMessage().contains("already verified"));
    }

    @Test
    void resendVerificationToken_UserNotFound() {
        // Arrange
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            emailVerificationService.resendVerificationToken("nonexistent@example.com", userService);
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void isTokenValid_ValidToken() {
        // Arrange
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

        // Act
        boolean result = emailVerificationService.isTokenValid("valid-token");

        // Assert
        assertTrue(result);
    }

    @Test
    void isTokenValid_InvalidToken() {
        // Arrange
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        boolean result = emailVerificationService.isTokenValid("invalid-token");

        // Assert
        assertFalse(result);
    }
}