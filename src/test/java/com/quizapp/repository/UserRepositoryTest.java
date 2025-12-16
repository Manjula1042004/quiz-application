package com.quizapp.repository;

import com.quizapp.entity.Role;
import com.quizapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User participantUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        userRepository.deleteAll();

        // Create participant user
        participantUser = new User();
        participantUser.setUsername("participant1");
        participantUser.setEmail("participant1@example.com");
        participantUser.setPassword("password123");
        participantUser.setRole(Role.PARTICIPANT);
        participantUser.setEnabled(true);
        participantUser.setCreatedAt(LocalDateTime.now());
        entityManager.persist(participantUser);

        // Create admin user
        adminUser = new User();
        adminUser.setUsername("admin1");
        adminUser.setEmail("admin1@example.com");
        adminUser.setPassword("admin123");
        adminUser.setRole(Role.ADMIN);
        adminUser.setEnabled(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        entityManager.persist(adminUser);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_UserExists_ReturnsUser() {
        // When
        Optional<User> found = userRepository.findByUsername("participant1");

        // Then
        assertTrue(found.isPresent());
        assertEquals("participant1", found.get().getUsername());
        assertEquals(Role.PARTICIPANT, found.get().getRole());
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void findByUsername_UserNotFound_ReturnsEmpty() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_UserExists_ReturnsUser() {
        // When
        Optional<User> found = userRepository.findByEmail("participant1@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("participant1@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername_UsernameExists_ReturnsTrue() {
        // When
        boolean exists = userRepository.existsByUsername("participant1");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should check if username doesn't exist")
    void existsByUsername_UsernameNotExists_ReturnsFalse() {
        // When
        boolean exists = userRepository.existsByUsername("unknown");

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should find users by role")
    void findByRole_ReturnsUsersWithRole() {
        // When
        List<User> participants = userRepository.findByRole(Role.PARTICIPANT);
        List<User> admins = userRepository.findByRole(Role.ADMIN);

        // Then
        assertEquals(1, participants.size());
        assertEquals(1, admins.size());
        assertEquals("participant1", participants.get(0).getUsername());
        assertEquals("admin1", admins.get(0).getUsername());
    }

    @Test
    @DisplayName("Should find enabled users")
    void findByEnabledTrue_ReturnsEnabledUsers() {
        // Given - create a disabled user
        User disabledUser = new User();
        disabledUser.setUsername("disabled");
        disabledUser.setEmail("disabled@example.com");
        disabledUser.setPassword("password");
        disabledUser.setRole(Role.PARTICIPANT);
        disabledUser.setEnabled(false);
        entityManager.persist(disabledUser);
        entityManager.flush();

        // When
        List<User> enabledUsers = userRepository.findByEnabledTrue();

        // Then
        assertEquals(2, enabledUsers.size()); // participant1 + admin1
        assertTrue(enabledUsers.stream().allMatch(User::getEnabled));
    }

    @Test
    @DisplayName("Should count active participants")
    void countActiveParticipants_ReturnsCorrectCount() {
        // When
        Long count = userRepository.countActiveParticipants();

        // Then
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Should count active admins")
    void countActiveAdmins_ReturnsCorrectCount() {
        // When
        Long count = userRepository.countActiveAdmins();

        // Then
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Should save new user")
    void save_NewUser_SuccessfullySaved() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("newpass");
        newUser.setRole(Role.PARTICIPANT);
        newUser.setEnabled(true);
        newUser.setCreatedAt(LocalDateTime.now());

        // When
        User savedUser = userRepository.save(newUser);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedUser.getId());
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertEquals("newuser", foundUser.getUsername());
        assertEquals("newuser@example.com", foundUser.getEmail());
    }
}