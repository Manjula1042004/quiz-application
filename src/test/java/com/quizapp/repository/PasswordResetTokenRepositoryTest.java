package com.quizapp.repository;

import com.quizapp.entity.PasswordResetToken;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("PasswordResetTokenRepository Tests")
class PasswordResetTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private PasswordResetToken testToken;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.PARTICIPANT);
        testUser.setEnabled(true);
        entityManager.persist(testUser);

        // Create test token
        testToken = new PasswordResetToken();
        testToken.setToken("reset-token-123");
        testToken.setUser(testUser);
        testToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        entityManager.persist(testToken);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find token by token string")
    void findByToken_TokenExists_ReturnsToken() {
        // When
        Optional<PasswordResetToken> found = tokenRepository.findByToken("reset-token-123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("reset-token-123", found.get().getToken());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    @DisplayName("Should return empty when token not found")
    void findByToken_TokenNotFound_ReturnsEmpty() {
        // When
        Optional<PasswordResetToken> found = tokenRepository.findByToken("nonexistent-token");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should delete token by user")
    void deleteByUser_DeletesUserTokens() {
        // When
        tokenRepository.deleteByUser(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<PasswordResetToken> found = tokenRepository.findByToken("reset-token-123");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should delete expired tokens")
    void deleteAllByExpiryDateBefore_DeletesExpiredTokens() {
        // Given - create an expired token
        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setToken("expired-reset-token");
        expiredToken.setUser(testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1)); // Expired 1 hour ago
        entityManager.persist(expiredToken);
        entityManager.flush();

        // When
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<PasswordResetToken> foundExpired = tokenRepository.findByToken("expired-reset-token");
        Optional<PasswordResetToken> foundValid = tokenRepository.findByToken("reset-token-123");

        assertFalse(foundExpired.isPresent()); // Should be deleted
        assertTrue(foundValid.isPresent()); // Should still exist
    }

    @Test
    @DisplayName("Should save new token")
    void save_NewToken_SuccessfullySaved() {
        // Given - create another user
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");
        newUser.setRole(Role.PARTICIPANT);
        entityManager.persist(newUser);
        entityManager.flush();

        PasswordResetToken newToken = new PasswordResetToken();
        newToken.setToken("new-reset-token");
        newToken.setUser(newUser);
        newToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        // When
        PasswordResetToken savedToken = tokenRepository.save(newToken);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedToken.getId());
        PasswordResetToken foundToken = entityManager.find(PasswordResetToken.class, savedToken.getId());
        assertEquals("new-reset-token", foundToken.getToken());
        assertEquals(newUser.getId(), foundToken.getUser().getId());
        assertNotNull(foundToken.getExpiryDate());
    }

    @Test
    @DisplayName("Should update token")
    void save_UpdateToken_SuccessfullyUpdated() {
        // Given
        LocalDateTime newExpiry = LocalDateTime.now().plusHours(48);
        testToken.setExpiryDate(newExpiry);

        // When
        PasswordResetToken updatedToken = tokenRepository.save(testToken);
        entityManager.flush();
        entityManager.clear();

        // Then
        PasswordResetToken foundToken = entityManager.find(PasswordResetToken.class, testToken.getId());
        assertEquals(newExpiry, foundToken.getExpiryDate());
    }

    @Test
    @DisplayName("Should check if token is expired")
    void isExpired_TokenExpired_ReturnsTrue() {
        // Given
        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setToken("expired");
        expiredToken.setUser(testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        entityManager.persist(expiredToken);
        entityManager.flush();

        // When & Then
        assertTrue(expiredToken.isExpired());
    }

    @Test
    @DisplayName("Should check if token is not expired")
    void isExpired_TokenNotExpired_ReturnsFalse() {
        // When & Then
        assertFalse(testToken.isExpired());
    }

    @Test
    @DisplayName("Should create token with constructor")
    void constructor_CreatesTokenWithExpiry() {
        // When
        PasswordResetToken newToken = new PasswordResetToken("constructor-token", testUser);

        // Then
        assertNotNull(newToken);
        assertEquals("constructor-token", newToken.getToken());
        assertEquals(testUser, newToken.getUser());
        assertNotNull(newToken.getExpiryDate());
        assertFalse(newToken.isExpired());
    }

    @Test
    @DisplayName("Should calculate expiry date correctly")
    void calculateExpiryDate_SetsCorrectExpiry() {
        // When
        PasswordResetToken newToken = new PasswordResetToken();

        // Then
        assertNotNull(newToken.getExpiryDate());
        assertTrue(newToken.getExpiryDate().isAfter(LocalDateTime.now()));
        assertTrue(newToken.getExpiryDate().isBefore(LocalDateTime.now().plusDays(2))); // Should be within 2 days
    }

    @Test
    @DisplayName("Should allow multiple tokens per user")
    void save_MultipleTokensForSameUser_SuccessfullySaved() {
        // Given
        PasswordResetToken secondToken = new PasswordResetToken();
        secondToken.setToken("second-token");
        secondToken.setUser(testUser);
        secondToken.setExpiryDate(LocalDateTime.now().plusHours(12));

        // When
        PasswordResetToken savedToken = tokenRepository.save(secondToken);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedToken.getId());
        PasswordResetToken foundToken = entityManager.find(PasswordResetToken.class, savedToken.getId());
        assertEquals("second-token", foundToken.getToken());
        assertEquals(testUser.getId(), foundToken.getUser().getId());
    }
}