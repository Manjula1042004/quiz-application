package com.quizapp.service;

import com.quizapp.entity.Role;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // ✅ FIX: Remove direct EmailVerificationService dependency

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String email, String password, Role role) {
        logger.info("🔧 Starting user registration for: {}", username);

        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }

        if (password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        try {
            User user = new User();
            user.setUsername(username.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role != null ? role : Role.PARTICIPANT);
            user.setEnabled(true); // ✅ Enabled by default for now
            user.setAccountLocked(false);
            user.setLoginAttempts(0);

            User savedUser = userRepository.save(user);
            logger.info("✅ User registered and ENABLED: {} (email: {})", savedUser.getUsername(), savedUser.getEmail());

            // ✅ FIX: Email verification will be handled separately to avoid circular dependency
            logger.info("📧 User registered - email verification can be triggered separately");

            return savedUser;

        } catch (Exception e) {
            logger.error("❌ Registration failed for user {}: {}", username, e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    // ... ALL YOUR EXISTING METHODS REMAIN EXACTLY THE SAME ...
    // enableUserByUsername, enableAllDisabledUsers, ensureUserEnabled, updateLastLogin, etc.
    // Just copy all your existing methods from the previous version here

    public void enableUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        user.setEnabled(true);
        userRepository.save(user);
        logger.info("✅ User enabled: {}", username);
    }

    @Transactional
    public void enableAllDisabledUsers() {
        List<User> disabledUsers = userRepository.findAll().stream()
                .filter(user -> !user.getEnabled())
                .collect(Collectors.toList());

        for (User user : disabledUsers) {
            user.setEnabled(true);
            userRepository.save(user);
            logger.info("✅ Enabled user: {}", user.getUsername());
        }

        logger.info("✅ Total users enabled: {}", disabledUsers.size());
    }

    public void ensureUserEnabled(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (!user.getEnabled()) {
            logger.warn("⚠️ User {} was disabled, enabling now...", username);
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    public void updateLastLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        logger.info("🔑 Last login updated for user: {}", username);
    }

    public Optional<User> findByUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("👤 Found user: {} (enabled: {})", user.getUsername(), user.getEnabled());
        }
        return userOpt;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    public List<User> findAllParticipants() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.PARTICIPANT && user.getEnabled())
                .collect(Collectors.toList());
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<User> findAllAdmins() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN && user.getEnabled())
                .collect(Collectors.toList());
    }

    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void enableUserAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void handleLoginFailure(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user != null) {
            int newAttempts = (user.getLoginAttempts() != null ? user.getLoginAttempts() : 0) + 1;
            user.setLoginAttempts(newAttempts);

            if (newAttempts >= 5) {
                user.setAccountLocked(true);
                user.setLockTime(LocalDateTime.now());
                logger.info("🔒 Account locked due to too many failed attempts: " + user.getUsername());
            }

            userRepository.save(user);
        }
    }

    public void handleLoginSuccess(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        user.setLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);

        updateLastLogin(username);
        logger.info("🔓 Login attempts reset for: " + username);
    }

    public boolean isAccountLocked(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.getAccountLocked();
    }

    public boolean isEmailVerified(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null && user.getEnabled();
    }
}