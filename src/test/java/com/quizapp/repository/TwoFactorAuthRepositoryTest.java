package com.quizapp.repository;

import com.quizapp.entity.TwoFactorAuth;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TwoFactorAuthRepository Tests")
class TwoFactorAuthRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TwoFactorAuthRepository twoFactorAuthRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private TwoFactorAuth test2FA;

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

        // Create test 2FA
        test2FA = new TwoFactorAuth();
        test2FA.setUser(testUser);
        test2FA.setEnabled(true);
        test2FA.setSecretKey("TEST-SECRET-KEY-123");
        test2FA.setBackupCodes("[\"code1\",\"code2\",\"code3\"]");
        entityManager.persist(test2FA);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find 2FA by user")
    void findByUser_2FAExists_Returns2FA() {
        // When
        Optional<TwoFactorAuth> found = twoFactorAuthRepository.findByUser(testUser);

        // Then
        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getUser().getId());
        assertTrue(found.get().getEnabled());
        assertEquals("TEST-SECRET-KEY-123", found.get().getSecretKey());
    }

    @Test
    @DisplayName("Should return empty when 2FA not found for user")
    void findByUser_2FANotExists_ReturnsEmpty() {
        // Given - create another user without 2FA
        User anotherUser = new User();
        anotherUser.setUsername("another");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setRole(Role.PARTICIPANT);
        entityManager.persist(anotherUser);
        entityManager.flush();

        // When
        Optional<TwoFactorAuth> found = twoFactorAuthRepository.findByUser(anotherUser);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find 2FA by user ID")
    void findByUserId_2FAExists_Returns2FA() {
        // When
        Optional<TwoFactorAuth> found = twoFactorAuthRepository.findByUserId(testUser.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(testUser.getId(), found.get().getUser().getId());
    }

    @Test
    @DisplayName("Should check if 2FA is enabled for user")
    void existsByUserAndEnabledTrue_2FAEnabled_ReturnsTrue() {
        // When
        boolean exists = twoFactorAuthRepository.existsByUserAndEnabledTrue(testUser);

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should check if 2FA is not enabled for user")
    void existsByUserAndEnabledTrue_2FADisabled_ReturnsFalse() {
        // Given - create user with disabled 2FA
        User disabledUser = new User();
        disabledUser.setUsername("disabled");
        disabledUser.setEmail("disabled@example.com");
        disabledUser.setPassword("password");
        disabledUser.setRole(Role.PARTICIPANT);
        entityManager.persist(disabledUser);

        TwoFactorAuth disabled2FA = new TwoFactorAuth();
        disabled2FA.setUser(disabledUser);
        disabled2FA.setEnabled(false);
        disabled2FA.setSecretKey("DISABLED-KEY");
        entityManager.persist(disabled2FA);
        entityManager.flush();

        // When
        boolean exists = twoFactorAuthRepository.existsByUserAndEnabledTrue(disabledUser);

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should delete 2FA by user")
    void deleteByUser_Deletes2FA() {
        // When
        twoFactorAuthRepository.deleteByUser(testUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<TwoFactorAuth> found = twoFactorAuthRepository.findByUser(testUser);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should check if 2FA is enabled for user ID")
    void is2FAEnabledForUser_Enabled_ReturnsTrue() {
        // When
        boolean enabled = twoFactorAuthRepository.is2FAEnabledForUser(testUser.getId());

        // Then
        assertTrue(enabled);
    }

    @Test
    @DisplayName("Should check if 2FA is disabled for user ID")
    void is2FAEnabledForUser_Disabled_ReturnsFalse() {
        // Given - create user without 2FA
        User no2FAUser = new User();
        no2FAUser.setUsername("no2fa");
        no2FAUser.setEmail("no2fa@example.com");
        no2FAUser.setPassword("password");
        no2FAUser.setRole(Role.PARTICIPANT);
        entityManager.persist(no2FAUser);
        entityManager.flush();

        // When
        boolean enabled = twoFactorAuthRepository.is2FAEnabledForUser(no2FAUser.getId());

        // Then
        assertFalse(enabled);
    }

    @Test
    @DisplayName("Should save new 2FA")
    void save_New2FA_SuccessfullySaved() {
        // Given - create another user
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");
        newUser.setRole(Role.PARTICIPANT);
        entityManager.persist(newUser);
        entityManager.flush();

        TwoFactorAuth new2FA = new TwoFactorAuth();
        new2FA.setUser(newUser);
        new2FA.setEnabled(true);
        new2FA.setSecretKey("NEW-SECRET-KEY");
        new2FA.setBackupCodes("[\"backup1\",\"backup2\"]");

        // When
        TwoFactorAuth saved2FA = twoFactorAuthRepository.save(new2FA);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(saved2FA.getId());
        TwoFactorAuth found2FA = entityManager.find(TwoFactorAuth.class, saved2FA.getId());
        assertEquals(newUser.getId(), found2FA.getUser().getId());
        assertTrue(found2FA.getEnabled());
        assertEquals("NEW-SECRET-KEY", found2FA.getSecretKey());
        assertNotNull(found2FA.getCreatedAt());
        assertNotNull(found2FA.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update 2FA")
    void save_Update2FA_SuccessfullyUpdated() {
        // Given
        test2FA.setEnabled(false);
        test2FA.setSecretKey("UPDATED-KEY");
        test2FA.setBackupCodes("[\"new1\",\"new2\"]");

        // When
        TwoFactorAuth updated2FA = twoFactorAuthRepository.save(test2FA);
        entityManager.flush();
        entityManager.clear();

        // Then
        TwoFactorAuth found2FA = entityManager.find(TwoFactorAuth.class, test2FA.getId());
        assertFalse(found2FA.getEnabled());
        assertEquals("UPDATED-KEY", found2FA.getSecretKey());
        assertEquals("[\"new1\",\"new2\"]", found2FA.getBackupCodes());
    }

    @Test
    @DisplayName("Should have automatic timestamps on create")
    void onCreate_ShouldSetTimestamps() {
        // Given
        User newUser = new User();
        newUser.setUsername("timestampuser");
        newUser.setEmail("timestamp@example.com");
        newUser.setPassword("password");
        newUser.setRole(Role.PARTICIPANT);
        entityManager.persist(newUser);

        TwoFactorAuth new2FA = new TwoFactorAuth();
        new2FA.setUser(newUser);
        new2FA.setEnabled(false);
        new2FA.setSecretKey("TIMESTAMP-KEY");

        // When
        TwoFactorAuth saved2FA = twoFactorAuthRepository.save(new2FA);
        entityManager.flush();
        entityManager.clear();

        // Then
        TwoFactorAuth found2FA = entityManager.find(TwoFactorAuth.class, saved2FA.getId());
        assertNotNull(found2FA.getCreatedAt());
        assertNotNull(found2FA.getUpdatedAt());
        assertEquals(found2FA.getCreatedAt(), found2FA.getUpdatedAt());
    }

    @Test
    @DisplayName("Should have automatic timestamps on update")
    void onUpdate_ShouldUpdateUpdatedAt() throws InterruptedException {
        // Given
        Thread.sleep(10); // Small delay to ensure different timestamps

        // When
        test2FA.setEnabled(false);
        TwoFactorAuth updated2FA = twoFactorAuthRepository.save(test2FA);
        entityManager.flush();
        entityManager.clear();

        // Then
        TwoFactorAuth found2FA = entityManager.find(TwoFactorAuth.class, test2FA.getId());
        assertTrue(found2FA.getUpdatedAt().isAfter(found2FA.getCreatedAt()));
    }
}