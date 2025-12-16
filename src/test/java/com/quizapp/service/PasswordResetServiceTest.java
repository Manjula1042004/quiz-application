package com.quizapp.service;

import com.quizapp.entity.PasswordResetToken;
import com.quizapp.entity.User;
import com.quizapp.repository.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private User user;
    private PasswordResetToken resetToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("oldPassword");

        resetToken = new PasswordResetToken("test-token", user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
    }

    @Test
    void createPasswordResetToken_Success() {
        // Arrange
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        doNothing().when(tokenRepository).deleteByUser(any(User.class));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(resetToken);

        // Act
        String result = passwordResetService.createPasswordResetToken("test@example.com");

        // Assert
        assertEquals("Password reset link sent to your email", result);
        verify(tokenRepository, times(1)).deleteByUser(user);
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
        verify(emailService, times(1)).sendPasswordResetEmail(user, "test-token");
    }

    @Test
    void createPasswordResetToken_UserNotFound() {
        // Arrange
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        String result = passwordResetService.createPasswordResetToken("nonexistent@example.com");

        // Assert
        assertEquals("User not found with this email", result);
        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    void validatePasswordResetToken_Valid() {
        // Arrange
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(resetToken));

        // Act
        String result = passwordResetService.validatePasswordResetToken("valid-token");

        // Assert
        assertEquals("valid", result);
    }

    @Test
    void validatePasswordResetToken_Invalid() {
        // Arrange
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        String result = passwordResetService.validatePasswordResetToken("invalid-token");

        // Assert
        assertEquals("Invalid token", result);
    }

    @Test
    void validatePasswordResetToken_Expired() {
        // Arrange
        resetToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(resetToken));

        // Act
        String result = passwordResetService.validatePasswordResetToken("expired-token");

        // Assert
        assertEquals("Token has expired", result);
    }

    @Test
    void resetPassword_Success() {
        // Arrange
        when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(resetToken));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userService.saveUser(any(User.class))).thenReturn(user);
        doNothing().when(tokenRepository).delete(any(PasswordResetToken.class));

        // Act
        String result = passwordResetService.resetPassword("valid-token", "newPassword");

        // Assert
        assertEquals("Password reset successfully", result);
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userService, times(1)).saveUser(user);
        verify(tokenRepository, times(1)).delete(resetToken);
    }

    @Test
    void resetPassword_InvalidToken() {
        // Arrange
        when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        // Act
        String result = passwordResetService.resetPassword("invalid-token", "newPassword");

        // Assert
        assertEquals("Invalid token", result);
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void resetPassword_ExpiredToken() {
        // Arrange
        resetToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(resetToken));

        // Act
        String result = passwordResetService.resetPassword("expired-token", "newPassword");

        // Assert
        assertEquals("Token has expired", result);
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void cleanExpiredTokens() {
        // Act
        passwordResetService.cleanExpiredTokens();

        // Assert
        verify(tokenRepository, times(1)).deleteAllByExpiryDateBefore(any(LocalDateTime.class));
    }
}