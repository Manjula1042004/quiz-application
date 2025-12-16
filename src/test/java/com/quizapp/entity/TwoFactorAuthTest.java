package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TwoFactorAuth Entity Tests")
class TwoFactorAuthTest {

    private TwoFactorAuth twoFactorAuth;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        twoFactorAuth = new TwoFactorAuth();
    }

    @Test
    @DisplayName("Should create TwoFactorAuth with default values")
    void constructor_DefaultValues() {
        // When
        TwoFactorAuth new2FA = new TwoFactorAuth();

        // Then
        assertNotNull(new2FA);
        assertNull(new2FA.getUser());
        assertFalse(new2FA.getEnabled());
        assertNull(new2FA.getSecretKey());
        assertNull(new2FA.getBackupCodes());
        assertNull(new2FA.getCreatedAt());
        assertNull(new2FA.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        twoFactorAuth.setId(40L);

        // Then
        assertEquals(40L, twoFactorAuth.getId());
    }

    @Test
    @DisplayName("Should set and get user")
    void setUserAndGetUser() {
        // When
        twoFactorAuth.setUser(user);

        // Then
        assertEquals(user, twoFactorAuth.getUser());
        assertEquals(1L, twoFactorAuth.getUser().getId());
        assertEquals("testuser", twoFactorAuth.getUser().getUsername());
    }

    @Test
    @DisplayName("Should set and get enabled status")
    void setEnabledAndGetEnabled() {
        // When
        twoFactorAuth.setEnabled(true);

        // Then
        assertTrue(twoFactorAuth.getEnabled());

        // When
        twoFactorAuth.setEnabled(false);

        // Then
        assertFalse(twoFactorAuth.getEnabled());
    }

    @Test
    @DisplayName("Should set and get secret key")
    void setSecretKeyAndGetSecretKey() {
        // Given
        String secretKey = "JBSWY3DPEHPK3PXP";

        // When
        twoFactorAuth.setSecretKey(secretKey);

        // Then
        assertEquals(secretKey, twoFactorAuth.getSecretKey());
    }

    @Test
    @DisplayName("Should set and get backup codes")
    void setBackupCodesAndGetBackupCodes() {
        // Given
        String backupCodes = "[\"123456\",\"654321\",\"111222\",\"333444\"]";

        // When
        twoFactorAuth.setBackupCodes(backupCodes);

        // Then
        assertEquals(backupCodes, twoFactorAuth.getBackupCodes());
    }

    @Test
    @DisplayName("Should set and get created at timestamp")
    void setCreatedAtAndGetCreatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        twoFactorAuth.setCreatedAt(now);

        // Then
        assertEquals(now, twoFactorAuth.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get updated at timestamp")
    void setUpdatedAtAndGetUpdatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        twoFactorAuth.setUpdatedAt(now);

        // Then
        assertEquals(now, twoFactorAuth.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set timestamps on persist")
    void onPrePersist_SetsTimestamps() {
        // When
        twoFactorAuth.onCreate();

        // Then
        assertNotNull(twoFactorAuth.getCreatedAt());
        assertNotNull(twoFactorAuth.getUpdatedAt());
        assertEquals(twoFactorAuth.getCreatedAt(), twoFactorAuth.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void onPreUpdate_UpdatesTimestamp() throws InterruptedException {
        // Given
        twoFactorAuth.onCreate();
        LocalDateTime originalUpdatedAt = twoFactorAuth.getUpdatedAt();

        // Wait a bit
        Thread.sleep(10);

        // When
        twoFactorAuth.onUpdate();

        // Then
        assertTrue(twoFactorAuth.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        twoFactorAuth.setUser(null);
        twoFactorAuth.setEnabled(null);
        twoFactorAuth.setSecretKey(null);
        twoFactorAuth.setBackupCodes(null);
        twoFactorAuth.setCreatedAt(null);
        twoFactorAuth.setUpdatedAt(null);

        // Then
        assertNull(twoFactorAuth.getUser());
        assertNull(twoFactorAuth.getEnabled());
        assertNull(twoFactorAuth.getSecretKey());
        assertNull(twoFactorAuth.getBackupCodes());
        assertNull(twoFactorAuth.getCreatedAt());
        assertNull(twoFactorAuth.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_Returns2FAInfo() {
        // Given
        twoFactorAuth.setId(8L);
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSecretKey("SECRET123");

        // When
        String toString = twoFactorAuth.toString();

        // Then
        assertTrue(toString.contains("SECRET123"));
    }

    @Test
    @DisplayName("Should create enabled 2FA setup")
    void createEnabled2FASetup() {
        // Given
        String secretKey = "JBSWY3DPEHPK3PXP";
        String backupCodes = "[\"111111\",\"222222\",\"333333\",\"444444\",\"555555\"]";

        // When
        twoFactorAuth.setUser(user);
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSecretKey(secretKey);
        twoFactorAuth.setBackupCodes(backupCodes);
        twoFactorAuth.onCreate();

        // Then
        assertEquals(user, twoFactorAuth.getUser());
        assertTrue(twoFactorAuth.getEnabled());
        assertEquals(secretKey, twoFactorAuth.getSecretKey());
        assertEquals(backupCodes, twoFactorAuth.getBackupCodes());
        assertNotNull(twoFactorAuth.getCreatedAt());
        assertNotNull(twoFactorAuth.getUpdatedAt());
    }

    @Test
    @DisplayName("Should create disabled 2FA setup")
    void createDisabled2FASetup() {
        // When
        twoFactorAuth.setUser(user);
        twoFactorAuth.setEnabled(false);
        twoFactorAuth.setSecretKey(null);
        twoFactorAuth.setBackupCodes(null);

        // Then
        assertEquals(user, twoFactorAuth.getUser());
        assertFalse(twoFactorAuth.getEnabled());
        assertNull(twoFactorAuth.getSecretKey());
        assertNull(twoFactorAuth.getBackupCodes());
    }

    @Test
    @DisplayName("Should enable 2FA after setup")
    void enable2FAAfterSetup() {
        // Given - Initially disabled
        twoFactorAuth.setEnabled(false);
        twoFactorAuth.setSecretKey(null);
        twoFactorAuth.setBackupCodes(null);

        // When - Enable with setup
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSecretKey("NEWSECRETKEY");
        twoFactorAuth.setBackupCodes("[\"backup1\",\"backup2\"]");

        // Then
        assertTrue(twoFactorAuth.getEnabled());
        assertEquals("NEWSECRETKEY", twoFactorAuth.getSecretKey());
        assertEquals("[\"backup1\",\"backup2\"]", twoFactorAuth.getBackupCodes());
    }

    @Test
    @DisplayName("Should disable 2FA")
    void disable2FA() {
        // Given - Initially enabled
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSecretKey("OLDSECRETKEY");
        twoFactorAuth.setBackupCodes("[\"old1\",\"old2\"]");

        // When - Disable
        twoFactorAuth.setEnabled(false);
        twoFactorAuth.setSecretKey(null);
        twoFactorAuth.setBackupCodes(null);

        // Then
        assertFalse(twoFactorAuth.getEnabled());
        assertNull(twoFactorAuth.getSecretKey());
        assertNull(twoFactorAuth.getBackupCodes());
    }

    @Test
    @DisplayName("Should handle base32 secret key")
    void handleBase32SecretKey() {
        // Given
        String base32Key = "JBSWY3DPEHPK3PXP"; // Standard TOTP secret key format

        // When
        twoFactorAuth.setSecretKey(base32Key);

        // Then
        assertEquals(base32Key, twoFactorAuth.getSecretKey());
        assertEquals(16, twoFactorAuth.getSecretKey().length());
    }

    @Test
    @DisplayName("Should handle JSON backup codes")
    void handleJSONBackupCodes() {
        // Given
        String jsonBackupCodes = "[\"12345678\",\"87654321\",\"11112222\",\"33334444\",\"55556666\"]";

        // When
        twoFactorAuth.setBackupCodes(jsonBackupCodes);

        // Then
        assertEquals(jsonBackupCodes, twoFactorAuth.getBackupCodes());
        assertTrue(twoFactorAuth.getBackupCodes().startsWith("["));
        assertTrue(twoFactorAuth.getBackupCodes().endsWith("]"));
        assertTrue(twoFactorAuth.getBackupCodes().contains("\""));
    }

    @Test
    @DisplayName("Should update secret key")
    void updateSecretKey() {
        // Given
        twoFactorAuth.setSecretKey("OLDKEY123");

        // When
        twoFactorAuth.setSecretKey("NEWKEY456");
        twoFactorAuth.onUpdate();

        // Then
        assertEquals("NEWKEY456", twoFactorAuth.getSecretKey());
        assertNotNull(twoFactorAuth.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update backup codes")
    void updateBackupCodes() {
        // Given
        twoFactorAuth.setBackupCodes("[\"old1\",\"old2\"]");

        // When
        twoFactorAuth.setBackupCodes("[\"new1\",\"new2\",\"new3\"]");
        twoFactorAuth.onUpdate();

        // Then
        assertEquals("[\"new1\",\"new2\",\"new3\"]", twoFactorAuth.getBackupCodes());
        assertNotNull(twoFactorAuth.getUpdatedAt());
    }
}