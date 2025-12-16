package com.quizapp.service;

import com.quizapp.entity.TwoFactorAuth;
import com.quizapp.entity.User;
import com.quizapp.repository.TwoFactorAuthRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoFactorAuthServiceTest {

    @Mock
    private TwoFactorAuthRepository twoFactorAuthRepository;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private GoogleAuthenticator googleAuthenticator;

    @InjectMocks
    private TwoFactorAuthService twoFactorAuthService;

    private User user;
    private TwoFactorAuth twoFactorAuth;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setId(1L);
        twoFactorAuth.setUser(user);
        twoFactorAuth.setSecretKey("SECRET123");
        twoFactorAuth.setEnabled(false);
        twoFactorAuth.setBackupCodes("[\"BACKUP1\",\"BACKUP2\",\"BACKUP3\"]");
    }

    @Test
    void setup2FA_Success() {
        // Arrange
        when(userService.findById(1L)).thenReturn(Optional.of(user));
        when(twoFactorAuthRepository.findByUser(user)).thenReturn(Optional.empty());
        when(twoFactorAuthRepository.save(any(TwoFactorAuth.class))).thenReturn(twoFactorAuth);

        // Act
        TwoFactorAuth result = twoFactorAuthService.setup2FA(1L);

        // Assert
        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getSecretKey());
        assertNotNull(result.getBackupCodes());
        assertFalse(result.getEnabled());
        verify(twoFactorAuthRepository, times(1)).save(any(TwoFactorAuth.class));
    }

    @Test
    void setup2FA_UserNotFound() {
        // Arrange
        when(userService.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            twoFactorAuthService.setup2FA(1L);
        });
    }

    @Test
    void enable2FA_Success() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(googleAuthenticator.authorize(anyString(), anyInt())).thenReturn(true);
        when(twoFactorAuthRepository.save(any(TwoFactorAuth.class))).thenReturn(twoFactorAuth);
        doNothing().when(emailService).sendTestEmail(anyString(), anyString());

        // Act
        boolean result = twoFactorAuthService.enable2FA(1L, "123456");

        // Assert
        assertTrue(result);
        assertTrue(twoFactorAuth.getEnabled());
        verify(twoFactorAuthRepository, times(1)).save(twoFactorAuth);
        verify(emailService, times(1)).sendTestEmail(anyString(), anyString());
    }

    @Test
    void enable2FA_InvalidCode() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(googleAuthenticator.authorize(anyString(), anyInt())).thenReturn(false);

        // Act
        boolean result = twoFactorAuthService.enable2FA(1L, "wrongcode");

        // Assert
        assertFalse(result);
        assertFalse(twoFactorAuth.getEnabled());
        verify(twoFactorAuthRepository, never()).save(any(TwoFactorAuth.class));
    }

    @Test
    void enable2FA_NotSetup() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            twoFactorAuthService.enable2FA(1L, "123456");
        });
    }

    @Test
    void disable2FA_Success() {
        // Arrange
        twoFactorAuth.setEnabled(true);
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(twoFactorAuthRepository.save(any(TwoFactorAuth.class))).thenReturn(twoFactorAuth);
        doNothing().when(emailService).sendTestEmail(anyString(), anyString());

        // Act
        boolean result = twoFactorAuthService.disable2FA(1L);

        // Assert
        assertTrue(result);
        assertFalse(twoFactorAuth.getEnabled());
        assertNull(twoFactorAuth.getSecretKey());
        assertNull(twoFactorAuth.getBackupCodes());
        verify(twoFactorAuthRepository, times(1)).save(twoFactorAuth);
        verify(emailService, times(1)).sendTestEmail(anyString(), anyString());
    }

    @Test
    void verify2FACode_ValidTOTP() {
        // Arrange
        twoFactorAuth.setEnabled(true);
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(googleAuthenticator.authorize(anyString(), anyInt())).thenReturn(true);

        // Act
        boolean result = twoFactorAuthService.verify2FACode(1L, "123456");

        // Assert
        assertTrue(result);
    }

    @Test
    void verify2FACode_ValidBackupCode() {
        // Arrange
        twoFactorAuth.setEnabled(true);
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(twoFactorAuthRepository.save(any(TwoFactorAuth.class))).thenReturn(twoFactorAuth);

        // Act
        boolean result = twoFactorAuthService.verify2FACode(1L, "BACKUP1");

        // Assert
        assertTrue(result);
        verify(twoFactorAuthRepository, times(1)).save(twoFactorAuth);
    }

    @Test
    void verify2FACode_InvalidCode() {
        // Arrange
        twoFactorAuth.setEnabled(true);
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(googleAuthenticator.authorize(anyString(), anyInt())).thenReturn(false);

        // Act
        boolean result = twoFactorAuthService.verify2FACode(1L, "invalid");

        // Assert
        assertFalse(result);
    }

    @Test
    void generateNewBackupCodes() {
        // Arrange
        twoFactorAuth.setEnabled(true);
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));
        when(twoFactorAuthRepository.save(any(TwoFactorAuth.class))).thenReturn(twoFactorAuth);
        doNothing().when(emailService).sendTestEmail(anyString(), anyString());

        // Act
        List<String> backupCodes = twoFactorAuthService.generateNewBackupCodes(1L);

        // Assert
        assertNotNull(backupCodes);
        assertEquals(10, backupCodes.size()); // Should generate 10 backup codes
        verify(twoFactorAuthRepository, times(1)).save(twoFactorAuth);
        verify(emailService, times(1)).sendTestEmail(anyString(), anyString());
    }

    @Test
    void is2FAEnabled_Enabled() {
        // Arrange
        twoFactorAuth.setEnabled(true);
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));

        // Act
        boolean result = twoFactorAuthService.is2FAEnabled(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void is2FAEnabled_Disabled() {
        // Arrange
        twoFactorAuth.setEnabled(false);
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));

        // Act
        boolean result = twoFactorAuthService.is2FAEnabled(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void is2FAEnabled_NotSetup() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = twoFactorAuthService.is2FAEnabled(1L);

        // Assert
        assertFalse(result);
    }

    @Test
    void getSecretKey() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.of(twoFactorAuth));

        // Act
        String secretKey = twoFactorAuthService.getSecretKey(1L);

        // Assert
        assertEquals("SECRET123", secretKey);
    }

    @Test
    void getSecretKey_NotSetup() {
        // Arrange
        when(twoFactorAuthRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            twoFactorAuthService.getSecretKey(1L);
        });
    }
}