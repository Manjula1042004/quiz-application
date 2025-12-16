package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EmailVerificationToken Entity Tests")
class EmailVerificationTokenTest {

    private EmailVerificationToken token;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        token = new EmailVerificationToken();
    }

    @Test
    @DisplayName("Should create token with default constructor")
    void constructor_Default() {
        // When
        EmailVerificationToken newToken = new EmailVerificationToken();

        // Then
        assertNotNull(newToken);
        assertNotNull(newToken.getToken());
        assertFalse(newToken.getToken().isEmpty());
        assertNotNull(newToken.getExpiryDate());
        assertFalse(newToken.getUsed());
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should create token with user constructor")
    void constructor_WithUser() {
        // When
        EmailVerificationToken newToken = new EmailVerificationToken(user);

        // Then
        assertNotNull(newToken);
        assertNotNull(newToken.getToken());
        assertEquals(user, newToken.getUser());
        assertFalse(newToken.getUsed());
        assertNotNull(newToken.getExpiryDate());
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        token.setId(50L);

        // Then
        assertEquals(50L, token.getId());
    }

    @Test
    @DisplayName("Should set and get token string")
    void setTokenAndGetToken() {
        // Given
        String tokenString = UUID.randomUUID().toString();

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
        LocalDateTime expiry = LocalDateTime.now().plusHours(48);

        // When
        token.setExpiryDate(expiry);

        // Then
        assertEquals(expiry, token.getExpiryDate());
    }

    @Test
    @DisplayName("Should set and get used flag")
    void setUsedAndGetUsed() {
        // When
        token.setUsed(true);

        // Then
        assertTrue(token.getUsed());

        // When
        token.setUsed(false);

        // Then
        assertFalse(token.getUsed());
    }

    @Test
    @DisplayName("Should check if token is expired")
    void isExpired_ExpiredToken_ReturnsTrue() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusHours(1);
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
        LocalDateTime futureTime = LocalDateTime.now().plusHours(24);
        token.setExpiryDate(futureTime);

        // When
        boolean expired = token.isExpired();

        // Then
        assertFalse(expired);
    }

    @Test
    @DisplayName("Should check if token is valid")
    void isValid_ValidToken_ReturnsTrue() {
        // Given
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        // When
        boolean valid = token.isValid();

        // Then
        assertTrue(valid);
    }

    @Test
    @DisplayName("Should check if token is invalid when used")
    void isValid_UsedToken_ReturnsFalse() {
        // Given
        token.setUsed(true);
        token.setExpiryDate(LocalDateTime.now().plusHours(24));

        // When
        boolean valid = token.isValid();

        // Then
        assertFalse(valid);
    }

    @Test
    @DisplayName("Should check if token is invalid when expired")
    void isValid_ExpiredToken_ReturnsFalse() {
        // Given
        token.setUsed(false);
        token.setExpiryDate(LocalDateTime.now().minusHours(1));

        // When
        boolean valid = token.isValid();

        // Then
        assertFalse(valid);
    }

    @Test
    @DisplayName("Should check if token is invalid when both used and expired")
    void isValid_UsedAndExpiredToken_ReturnsFalse() {
        // Given
        token.setUsed(true);
        token.setExpiryDate(LocalDateTime.now().minusHours(1));

        // When
        boolean valid = token.isValid();

        // Then
        assertFalse(valid);
    }

    @Test
    @DisplayName("Should calculate expiry date correctly")
    void calculateExpiryDate_Sets24HoursFromNow() {
        // When - Using constructor
        EmailVerificationToken newToken = new EmailVerificationToken();

        // Then
        assertNotNull(newToken.getExpiryDate());
        LocalDateTime expectedExpiry = LocalDateTime.now().plusHours(24);
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
        assertTrue(newToken.getExpiryDate().isBefore(LocalDateTime.now().plusHours(25)));
    }

    @Test
    @DisplayName("Should generate unique token UUID")
    void constructor_GeneratesUniqueToken() {
        // When
        EmailVerificationToken token1 = new EmailVerificationToken();
        EmailVerificationToken token2 = new EmailVerificationToken();

        // Then
        assertNotNull(token1.getToken());
        assertNotNull(token2.getToken());
        assertNotEquals(token1.getToken(), token2.getToken());
        assertTrue(isValidUUID(token1.getToken()));
        assertTrue(isValidUUID(token2.getToken()));
    }

    private boolean isValidUUID(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        token.setToken(null);
        token.setUser(null);
        token.setExpiryDate(null);
        token.setUsed(null);

        // Then
        assertNull(token.getToken());
        assertNull(token.getUser());
        assertNull(token.getExpiryDate());
        assertNull(token.getUsed());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsTokenInfo() {
        // Given
        token.setId(10L);
        token.setToken("test-token-123");
        token.setUsed(false);

        // When
        String toString = token.toString();

        // Then
        assertTrue(toString.contains("test-token-123"));
    }

    @Test
    @DisplayName("Should create token with custom expiry")
    void createTokenWithCustomExpiry() {
        // Given
        LocalDateTime customExpiry = LocalDateTime.now().plusDays(2);

        // When
        token.setExpiryDate(customExpiry);

        // Then
        assertEquals(customExpiry, token.getExpiryDate());
        assertFalse(token.isExpired());
    }

    @Test
    @DisplayName("Should mark token as used")
    void markTokenAsUsed() {
        // When
        token.setUsed(true);

        // Then
        assertTrue(token.getUsed());
        assertFalse(token.isValid());
    }

    @Test
    @DisplayName("Should create valid verification token")
    void createValidVerificationToken() {
        // When
        EmailVerificationToken newToken = new EmailVerificationToken(user);

        // Then
        assertNotNull(newToken.getToken());
        assertEquals(user, newToken.getUser());
        assertFalse(newToken.getUsed());
        assertFalse(newToken.isExpired());
        assertTrue(newToken.isValid());
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should handle token just before expiry")
    void tokenJustBeforeExpiry() {
        // Given
        LocalDateTime almostExpired = LocalDateTime.now().plusSeconds(1);
        token.setExpiryDate(almostExpired);
        token.setUsed(false);

        // When
        boolean valid = token.isValid();

        // Then
        assertTrue(valid);
        assertFalse(token.isExpired());
    }

    @Test
    @DisplayName("Should handle token just after expiry")
    void tokenJustAfterExpiry() throws InterruptedException {
        // Given
        LocalDateTime justExpired = LocalDateTime.now().minusSeconds(1);
        token.setExpiryDate(justExpired);
        token.setUsed(false);

        // Wait to ensure it's expired
        Thread.sleep(10);

        // When
        boolean expired = token.isExpired();
        boolean valid = token.isValid();

        // Then
        assertTrue(expired);
        assertFalse(valid);
    }
}