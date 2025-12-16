package com.quizapp.service;

import com.quizapp.entity.Role;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.PARTICIPANT);
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setLoginAttempts(0);
        user.setLockTime(null);
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registered = userService.registerUser("newuser", "new@example.com", "Password123!", Role.PARTICIPANT);

        // Assert
        assertNotNull(registered);
        assertEquals("newuser", registered.getUsername());
        assertEquals("new@example.com", registered.getEmail().toLowerCase());
        assertEquals(Role.PARTICIPANT, registered.getRole());
        assertTrue(registered.getEnabled());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateUsername() {
        // Arrange
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser("existinguser", "test@example.com", "Password123!", Role.PARTICIPANT);
        });
    }

    @Test
    void registerUser_DuplicateEmail() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser("newuser", "existing@example.com", "Password123!", Role.PARTICIPANT);
        });
    }

    @Test
    void registerUser_ShortPassword() {
        // Arrange
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser("newuser", "new@example.com", "short", Role.PARTICIPANT);
        });
    }

    @Test
    void enableUserByUsername_Success() {
        // Arrange
        user.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.enableUserByUsername("testuser");

        // Assert
        assertTrue(user.getEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void enableUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.enableUserByUsername("nonexistent");
        });
    }

    @Test
    void ensureUserEnabled_AlreadyEnabled() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        userService.ensureUserEnabled("testuser");

        // Assert
        verify(userRepository, never()).save(any(User.class)); // Already enabled
    }

    @Test
    void ensureUserEnabled_EnableDisabled() {
        // Arrange
        user.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.ensureUserEnabled("testuser");

        // Assert
        assertTrue(user.getEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateLastLogin() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updateLastLogin("testuser");

        // Assert
        assertNotNull(user.getLastLogin());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void findByUsername_Found() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUsername("nonexistent");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findByEmail_Found() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void findById_Found() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void saveUser() {
        // Arrange
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User saved = userService.saveUser(user);

        // Assert
        assertNotNull(saved);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void existsByUsername_True() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean exists = userService.existsByUsername("testuser");

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsByEmail_True() {
        // Arrange
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean exists = userService.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    void findAllParticipants() {
        // Arrange
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> participants = userService.findAllParticipants();

        // Assert
        assertEquals(1, participants.size());
        assertEquals(Role.PARTICIPANT, participants.get(0).getRole());
        assertTrue(participants.get(0).getEnabled());
    }

    @Test
    void findAllUsers() {
        // Arrange
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> allUsers = userService.findAllUsers();

        // Assert
        assertEquals(1, allUsers.size());
    }

    @Test
    void findAllAdmins() {
        // Arrange
        user.setRole(Role.ADMIN);
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> admins = userService.findAllAdmins();

        // Assert
        assertEquals(1, admins.size());
        assertEquals(Role.ADMIN, admins.get(0).getRole());
        assertTrue(admins.get(0).getEnabled());
    }

    @Test
    void disableUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.disableUser(1L);

        // Assert
        assertFalse(user.getEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void enableUserAccount() {
        // Arrange
        user.setEnabled(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.enableUserAccount(1L);

        // Assert
        assertTrue(user.getEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void changeUserRole() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.changeUserRole(1L, Role.ADMIN);

        // Assert
        assertEquals(Role.ADMIN, user.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updatePassword() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.updatePassword(1L, "newPassword");

        // Assert
        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void handleLoginFailure() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.handleLoginFailure("testuser");

        // Assert
        assertEquals(1, user.getLoginAttempts());
        assertFalse(user.getAccountLocked());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void handleLoginFailure_LockAccount() {
        // Arrange
        user.setLoginAttempts(4);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.handleLoginFailure("testuser");

        // Assert
        assertEquals(5, user.getLoginAttempts());
        assertTrue(user.getAccountLocked());
        assertNotNull(user.getLockTime());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void handleLoginSuccess() {
        // Arrange
        user.setLoginAttempts(3);
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.handleLoginSuccess("testuser");

        // Assert
        assertEquals(0, user.getLoginAttempts());
        assertFalse(user.getAccountLocked());
        assertNull(user.getLockTime());
        assertNotNull(user.getLastLogin());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void isAccountLocked_True() {
        // Arrange
        user.setAccountLocked(true);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        boolean locked = userService.isAccountLocked("testuser");

        // Assert
        assertTrue(locked);
    }

    @Test
    void isAccountLocked_False() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        boolean locked = userService.isAccountLocked("testuser");

        // Assert
        assertFalse(locked);
    }

    @Test
    void isEmailVerified_True() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        boolean verified = userService.isEmailVerified("testuser");

        // Assert
        assertTrue(verified);
    }

    @Test
    void isEmailVerified_False() {
        // Arrange
        user.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        boolean verified = userService.isEmailVerified("testuser");

        // Assert
        assertFalse(verified);
    }
}