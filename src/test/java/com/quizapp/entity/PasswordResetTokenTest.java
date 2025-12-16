package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordResetToken Entity Tests")
class PasswordResetTokenTest {

    private PasswordResetToken token;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        token = new PasswordResetToken();
    }

    @Test
    @DisplayName("Should create token with default constructor")
    void constructor_Default() {
        // When
        PasswordResetToken newToken = new PasswordResetToken();

        // Then
        assertNotNull(newToken);
        assertNull(newToken.getToken());
        assertNull(newToken.getUser());
        assertNotNull(newToken.getExpiryDate());
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
        assertTrue(newToken.getExpiryDate().isBefore(LocalDateTime.now().plusDays(2)));
    }

    @Test
    @DisplayName("Should create token with parameters constructor")
    void constructor_WithParameters() {
        // Given
        String tokenString = "reset-token-123";

        // When
        PasswordResetToken newToken = new PasswordResetToken(tokenString, user);

        // Then
        assertNotNull(newToken);
        assertEquals(tokenString, newToken.getToken());
        assertEquals(user, newToken.getUser());
        assertNotNull(newToken.getExpiryDate());
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        token.setId(30L);

        // Then
        assertEquals(30L, token.getId());
    }

    @Test
    @DisplayName("Should set and get token string")
    void setTokenAndGetToken() {
        // Given
        String tokenString = "custom-reset-token";

        // When
        token.setToken(tokenString);

        // Then
        assertEquals(tokenString, token.getToken());
    }

    @Test
    @DisplayName("Should set and get user")
    void setUserAndGetUser() {
        // When
        token.setUser(user);

        // Then
        assertEquals(user, token.getUser());
        assertEquals(1L, token.getUser().getId());
        assertEquals("test@example.com", token.getUser().getEmail());
    }

    @Test
    @DisplayName("Should set and get expiry date")
    void setExpiryDateAndGetExpiryDate() {
        // Given
        LocalDateTime expiry = LocalDateTime.now().plusHours(12);

        // When
        token.setExpiryDate(expiry);

        // Then
        assertEquals(expiry, token.getExpiryDate());
    }

    @Test
    @DisplayName("Should check if token is expired")
    void isExpired_ExpiredToken_ReturnsTrue() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(30);
        token.setExpiryDate(pastTime);

        // When
        boolean expired = token.isExpired();

        // Then
        assertTrue(expired);
    }

    @Test
    @DisplayName("Should check if token is not expired")
    void isExpired_NotExpiredToken_ReturnsFalse() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusHours(6);
        token.setExpiryDate(futureTime);

        // When
        boolean expired = token.isExpired();

        // Then
        assertFalse(expired);
    }

    @Test
    @DisplayName("Should calculate expiry date correctly (24 hours)")
    void calculateExpiryDate_Sets24HoursFromNow() {
        // When - Using default constructor
        PasswordResetToken newToken = new PasswordResetToken();

        // Then
        assertNotNull(newToken.getExpiryDate());
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);

        assertTrue(newToken.getExpiryDate().isAfter(now));
        assertTrue(newToken.getExpiryDate().isBefore(tomorrow.plusHours(1)));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        token.setToken(null);
        token.setUser(null);
        token.setExpiryDate(null);

        // Then
        assertNull(token.getToken());
        assertNull(token.getUser());
        assertNull(token.getExpiryDate());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsTokenInfo() {
        // Given
        token.setId(5L);
        token.setToken("abc123");
        token.setExpiryDate(LocalDateTime.of(2024, 1, 1, 12, 0));

        // When
        String toString = token.toString();

        // Then
        assertTrue(toString.contains("abc123"));
    }

    @Test
    @DisplayName("Should create valid password reset token")
    void createValidPasswordResetToken() {
        // When
        PasswordResetToken newToken = new PasswordResetToken("valid-token", user);

        // Then
        assertEquals("valid-token", newToken.getToken());
        assertEquals(user, newToken.getUser());
        assertFalse(newToken.isExpired());
        assertNotNull(newToken.getExpiryDate());
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should handle multiple tokens for same user")
    void multipleTokensForSameUser() {
        // Given
        PasswordResetToken token1 = new PasswordResetToken("token1", user);
        PasswordResetToken token2 = new PasswordResetToken("token2", user);

        // Then
        assertEquals("token1", token1.getToken());
        assertEquals("token2", token2.getToken());
        assertEquals(user, token1.getUser());
        assertEquals(user, token2.getUser());
        assertNotEquals(token1.getToken(), token2.getToken());
    }

    @Test
    @DisplayName("Should update expiry date")
    void updateExpiryDate() {
        // Given
        token.setExpiryDate(LocalDateTime.now().plusHours(6));
        LocalDateTime originalExpiry = token.getExpiryDate();

        // When
        LocalDateTime newExpiry = LocalDateTime.now().plusHours(12);
        token.setExpiryDate(newExpiry);

        // Then
        assertEquals(newExpiry, token.getExpiryDate());
        assertNotEquals(originalExpiry, token.getExpiryDate());
    }

    @Test
    @DisplayName("Should check token just before expiry")
    void tokenJustBeforeExpiry() {
        // Given
        LocalDateTime almostExpired = LocalDateTime.now().plusSeconds(10);
        token.setExpiryDate(almostExpired);

        // When
        boolean expired = token.isExpired();

        // Then
        assertFalse(expired);
    }

    @Test
    @DisplayName("Should check token just after expiry")
    void tokenJustAfterExpiry() throws InterruptedException {
        // Given
        LocalDateTime justExpired = LocalDateTime.now().minusSeconds(1);
        token.setExpiryDate(justExpired);

        // Wait to ensure it's expired
        Thread.sleep(10);

        // When
        boolean expired = token.isExpired();

        // Then
        assertTrue(expired);
    }

    @Test
    @DisplayName("Should create token with custom time")
    void createTokenWithCustomTime() {
        // Given
        LocalDateTime customTime = LocalDateTime.now().plusHours(48);

        // When
        token.setExpiryDate(customTime);

        // Then
        assertEquals(customTime, token.getExpiryDate());
        assertFalse(token.isExpired());
    }

    @Test
    @DisplayName("Should handle null token string")
    void nullTokenString() {
        // When
        token.setToken(null);

        // Then
        assertNull(token.getToken());
        assertFalse(token.isExpired()); // Doesn't depend on token string
    }
}