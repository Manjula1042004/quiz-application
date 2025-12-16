package com.quizapp.repository;

import com.quizapp.entity.EmailVerificationToken;
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
@DisplayName("EmailVerificationTokenRepository Tests")
class EmailVerificationTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private EmailVerificationToken testToken;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.PARTICIPANT);
        testUser.setEnabled(false); // Not verified yet
        entityManager.persist(testUser);

        // Create test token
        testToken = new EmailVerificationToken();
        testToken.setToken("test-token-123");
        testToken.setUser(testUser);
        testToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        testToken.setUsed(false);
        entityManager.persist(testToken);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find token by token string")
    void findByToken_TokenExists_ReturnsToken() {
        // When
        Optional<EmailVerificationToken> found = tokenRepository.findByToken("test-token-123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("test-token-123", found.get().getToken());
        assertEquals(testUser.getId(), found.get().getUser().getId());
        assertFalse(found.get().getUsed());
    }

    @Test
    @DisplayName("Should return empty when token not found")
    void findByToken_TokenNotFound_ReturnsEmpty() {
        // When
        Optional<EmailVerificationToken> found = tokenRepository.findByToken("nonexistent-token");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find token by user")
    void findByUser_TokenExists_ReturnsToken() {
        // When
        Optional<EmailVerificationToken> found = tokenRepository.findByUser(testUser);

        // Then
        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    @DisplayName("Should delete expired tokens")
    void deleteAllExpiredSince_DeletesExpiredTokens() {
        // Given - create an expired token
        EmailVerificationToken expiredToken = new EmailVerificationToken();
        expiredToken.setToken("expired-token");
        expiredToken.setUser(testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1)); // Expired 1 hour ago
        expiredToken.setUsed(false);
        entityManager.persist(expiredToken);
        entityManager.flush();

        // When
        tokenRepository.deleteAllExpiredSince(LocalDateTime.now());
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<EmailVerificationToken> foundExpired = tokenRepository.findByToken("expired-token");
        Optional<EmailVerificationToken> foundValid = tokenRepository.findByToken("test-token-123");

        assertFalse(foundExpired.isPresent()); // Should be deleted
        assertTrue(foundValid.isPresent()); // Should still exist
    }

    @Test
    @DisplayName("Should delete token by user")
    void deleteByUser_DeletesUserTokens() {
        // When
        tokenRepository.deleteByUser(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<EmailVerificationToken> found = tokenRepository.findByUser(testUser);
        assertFalse(found.isPresent());
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

        EmailVerificationToken newToken = new EmailVerificationToken();
        newToken.setToken("new-token-456");
        newToken.setUser(newUser);
        newToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        newToken.setUsed(false);

        // When
        EmailVerificationToken savedToken = tokenRepository.save(newToken);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedToken.getId());
        EmailVerificationToken foundToken = entityManager.find(EmailVerificationToken.class, savedToken.getId());
        assertEquals("new-token-456", foundToken.getToken());
        assertEquals(newUser.getId(), foundToken.getUser().getId());
        assertFalse(foundToken.getUsed());
    }

    @Test
    @DisplayName("Should update token")
    void save_UpdateToken_SuccessfullyUpdated() {
        // Given
        testToken.setUsed(true);

        // When
        EmailVerificationToken updatedToken = tokenRepository.save(testToken);
        entityManager.flush();
        entityManager.clear();

        // Then
        EmailVerificationToken foundToken = entityManager.find(EmailVerificationToken.class, testToken.getId());
        assertTrue(foundToken.getUsed());
    }

    @Test
    @DisplayName("Should check if token is expired")
    void isExpired_TokenExpired_ReturnsTrue() {
        // Given
        EmailVerificationToken expiredToken = new EmailVerificationToken();
        expiredToken.setToken("expired");
        expiredToken.setUser(testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        expiredToken.setUsed(false);
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
    @DisplayName("Should check if token is valid")
    void isValid_TokenValid_ReturnsTrue() {
        // When & Then
        assertTrue(testToken.isValid());
    }

    @Test
    @DisplayName("Should check if token is invalid when used")
    void isValid_TokenUsed_ReturnsFalse() {
        // Given
        testToken.setUsed(true);
        entityManager.persist(testToken);
        entityManager.flush();

        // When & Then
        assertFalse(testToken.isValid());
    }

    @Test
    @DisplayName("Should check if token is invalid when expired")
    void isValid_TokenExpired_ReturnsFalse() {
        // Given
        EmailVerificationToken expiredToken = new EmailVerificationToken();
        expiredToken.setToken("expired");
        expiredToken.setUser(testUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusHours(1));
        expiredToken.setUsed(false);
        entityManager.persist(expiredToken);
        entityManager.flush();

        // When & Then
        assertFalse(expiredToken.isValid());
    }

    @Test
    @DisplayName("Should generate token with UUID on creation")
    void constructor_GeneratesTokenWithUUID() {
        // When
        EmailVerificationToken newToken = new EmailVerificationToken(testUser);

        // Then
        assertNotNull(newToken.getToken());
        assertFalse(newToken.getToken().isEmpty());
        assertNotNull(newToken.getExpiryDate());
        assertFalse(newToken.getUsed());
        assertEquals(testUser, newToken.getUser());
    }
}