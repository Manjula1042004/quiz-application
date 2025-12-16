package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Tests")
class UserEntityTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should create user with default values")
    void constructor_DefaultValues() {
        // When
        User newUser = new User();

        // Then
        assertNotNull(newUser);
        assertFalse(newUser.getEnabled()); // Changed to false for email verification
        assertEquals(0, newUser.getLoginAttempts());
        assertFalse(newUser.getAccountLocked());
        assertNotNull(newUser.getQuizAttempts());
        assertTrue(newUser.getQuizAttempts().isEmpty());
    }

    @Test
    @DisplayName("Should create user with parameters")
    void constructor_WithParameters() {
        // When
        User newUser = new User("testuser", "test@example.com", "password123", Role.ADMIN);

        // Then
        assertEquals("testuser", newUser.getUsername());
        assertEquals("test@example.com", newUser.getEmail());
        assertEquals("password123", newUser.getPassword());
        assertEquals(Role.ADMIN, newUser.getRole());
        assertFalse(newUser.getEnabled()); // Changed to false
        assertEquals(0, newUser.getLoginAttempts());
        assertFalse(newUser.getAccountLocked());
    }

    @Test
    @DisplayName("Should create user with default role when null")
    void constructor_NullRole_UsesDefault() {
        // When
        User newUser = new User("testuser", "test@example.com", "password123", null);

        // Then
        assertEquals(Role.PARTICIPANT, newUser.getRole());
    }

    @Test
    @DisplayName("Should set and get user ID")
    void setIdAndGetId() {
        // When
        user.setId(123L);

        // Then
        assertEquals(123L, user.getId());
    }

    @Test
    @DisplayName("Should set and get username")
    void setUsernameAndGetUsername() {
        // When
        user.setUsername("john_doe");

        // Then
        assertEquals("john_doe", user.getUsername());
    }

    @Test
    @DisplayName("Should set and get email")
    void setEmailAndGetEmail() {
        // When
        user.setEmail("john@example.com");

        // Then
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should set and get password")
    void setPasswordAndGetPassword() {
        // When
        user.setPassword("securePassword123!");

        // Then
        assertEquals("securePassword123!", user.getPassword());
    }

    @Test
    @DisplayName("Should set and get role")
    void setRoleAndGetRole() {
        // When
        user.setRole(Role.ADMIN);

        // Then
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should set and get enabled status")
    void setEnabledAndGetEnabled() {
        // When
        user.setEnabled(true);

        // Then
        assertTrue(user.getEnabled());

        // When
        user.setEnabled(false);

        // Then
        assertFalse(user.getEnabled());
    }

    @Test
    @DisplayName("Should handle null enabled status")
    void getEnabled_Null_ReturnsFalse() {
        // When
        user.setEnabled(null);

        // Then
        assertFalse(user.getEnabled());
    }

    @Test
    @DisplayName("Should set and get created at timestamp")
    void setCreatedAtAndGetCreatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        user.setCreatedAt(now);

        // Then
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get updated at timestamp")
    void setUpdatedAtAndGetUpdatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        user.setUpdatedAt(now);

        // Then
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get last login timestamp")
    void setLastLoginAndGetLastLogin() {
        // Given
        LocalDateTime loginTime = LocalDateTime.now();

        // When
        user.setLastLogin(loginTime);

        // Then
        assertEquals(loginTime, user.getLastLogin());
    }

    @Test
    @DisplayName("Should set and get login attempts")
    void setLoginAttemptsAndGetLoginAttempts() {
        // When
        user.setLoginAttempts(5);

        // Then
        assertEquals(5, user.getLoginAttempts());
    }

    @Test
    @DisplayName("Should handle null login attempts")
    void getLoginAttempts_Null_ReturnsZero() {
        // When
        user.setLoginAttempts(null);

        // Then
        assertEquals(0, user.getLoginAttempts());
    }

    @Test
    @DisplayName("Should set and get account locked status")
    void setAccountLockedAndGetAccountLocked() {
        // When
        user.setAccountLocked(true);

        // Then
        assertTrue(user.getAccountLocked());

        // When
        user.setAccountLocked(false);

        // Then
        assertFalse(user.getAccountLocked());
    }

    @Test
    @DisplayName("Should handle null account locked status")
    void getAccountLocked_Null_ReturnsFalse() {
        // When
        user.setAccountLocked(null);

        // Then
        assertFalse(user.getAccountLocked());
    }

    @Test
    @DisplayName("Should set and get lock time")
    void setLockTimeAndGetLockTime() {
        // Given
        LocalDateTime lockTime = LocalDateTime.now();

        // When
        user.setLockTime(lockTime);

        // Then
        assertEquals(lockTime, user.getLockTime());
    }

    @Test
    @DisplayName("Should set and get quiz attempts")
    void setQuizAttemptsAndGetQuizAttempts() {
        // Given
        ArrayList<QuizAttempt> attempts = new ArrayList<>();
        QuizAttempt attempt = new QuizAttempt();
        attempts.add(attempt);

        // When
        user.setQuizAttempts(attempts);

        // Then
        assertEquals(1, user.getQuizAttempts().size());
        assertEquals(attempt, user.getQuizAttempts().get(0));
    }

    @Test
    @DisplayName("Should add quiz attempt")
    void addQuizAttempt() {
        // Given
        QuizAttempt attempt = new QuizAttempt();

        // When
        user.getQuizAttempts().add(attempt);

        // Then
        assertEquals(1, user.getQuizAttempts().size());
        assertEquals(attempt, user.getQuizAttempts().get(0));
    }

    @Test
    @DisplayName("Should set timestamps on persist")
    void onPrePersist_SetsTimestamps() {
        // When
        user.onCreate();

        // Then
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertEquals(user.getCreatedAt(), user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void onPreUpdate_UpdatesTimestamp() throws InterruptedException {
        // Given
        user.onCreate();
        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        // Wait a bit
        Thread.sleep(10);

        // When
        user.onUpdate();

        // Then
        assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsUserInfo() {
        // Given
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.PARTICIPANT);

        // When
        String toString = user.toString();

        // Then
        assertTrue(toString.contains("testuser"));
        assertTrue(toString.contains("test@example.com"));
        assertTrue(toString.contains("PARTICIPANT"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "ab", "verylongusernameexceedingthemaximumallowedlengthoffiftycharacters"})
    @DisplayName("Should handle username length validation")
    void username_LengthValidation(String username) {
        // This test is for documentation - actual validation is done by JPA annotations
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    @DisplayName("Should handle null username")
    void username_Null() {
        user.setUsername(null);
        assertNull(user.getUsername());
    }

    @Test
    @DisplayName("Should handle null email")
    void email_Null() {
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("Should handle null password")
    void password_Null() {
        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Should handle null role")
    void role_Null() {
        user.setRole(null);
        assertNull(user.getRole());
    }

    @Test
    @DisplayName("Should increment login attempts")
    void incrementLoginAttempts() {
        // Given
        user.setLoginAttempts(2);

        // When
        user.setLoginAttempts(user.getLoginAttempts() + 1);

        // Then
        assertEquals(3, user.getLoginAttempts());
    }

    @Test
    @DisplayName("Should reset login attempts")
    void resetLoginAttempts() {
        // Given
        user.setLoginAttempts(5);
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now());

        // When
        user.setLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);

        // Then
        assertEquals(0, user.getLoginAttempts());
        assertFalse(user.getAccountLocked());
        assertNull(user.getLockTime());
    }

    @Test
    @DisplayName("Should check if user is admin")
    void isAdmin_AdminRole_ReturnsTrue() {
        // Given
        user.setRole(Role.ADMIN);

        // Then
        assertEquals(Role.ADMIN, user.getRole());
        assertTrue(user.getRole() == Role.ADMIN);
    }

    @Test
    @DisplayName("Should check if user is participant")
    void isParticipant_ParticipantRole_ReturnsTrue() {
        // Given
        user.setRole(Role.PARTICIPANT);

        // Then
        assertEquals(Role.PARTICIPANT, user.getRole());
        assertTrue(user.getRole() == Role.PARTICIPANT);
    }
}