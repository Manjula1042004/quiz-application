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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

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
    void loadUserByUsername_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonLocked());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_NotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistent");
        });
    }

    @Test
    void loadUserByUsername_AutoEnableDisabledUser() {
        // Arrange
        user.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails.isEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void loadUserByUsername_AccountLocked() {
        // Arrange
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now().minusHours(12)); // Locked 12 hours ago
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails.isAccountNonLocked()); // Should be auto-unlocked after 12 hours
    }

    @Test
    void loadUserByUsername_AccountRecentlyLocked() {
        // Arrange
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now()); // Just locked
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("testuser");
        });
    }

    @Test
    void increaseFailedAttempts() {
        // Arrange
        user.setLoginAttempts(2);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        customUserDetailsService.increaseFailedAttempts(user);

        // Assert
        assertEquals(3, user.getLoginAttempts());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void increaseFailedAttempts_LockAccount() {
        // Arrange
        user.setLoginAttempts(4);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        customUserDetailsService.increaseFailedAttempts(user);

        // Assert
        assertTrue(user.getAccountLocked());
        assertNotNull(user.getLockTime());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void resetFailedAttempts() {
        // Arrange
        user.setLoginAttempts(3);
        user.setAccountLocked(true);
        user.setLockTime(LocalDateTime.now());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        customUserDetailsService.resetFailedAttempts("testuser");

        // Assert
        assertEquals(0, user.getLoginAttempts());
        assertFalse(user.getAccountLocked());
        assertNull(user.getLockTime());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void resetFailedAttempts_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.resetFailedAttempts("nonexistent");
        });
    }
}