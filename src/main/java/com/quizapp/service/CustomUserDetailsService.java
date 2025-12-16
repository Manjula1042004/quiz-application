package com.quizapp.service;

import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔐 Authentication attempt for user: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("❌ User not found: " + username);
                    return new UsernameNotFoundException("Invalid username or password");
                });

        System.out.println("✅ User found: " + user.getUsername() +
                " | Enabled: " + user.getEnabled() +
                " | Role: " + user.getRole() +
                " | AccountLocked: " + user.getAccountLocked());

        // Auto-enable user if disabled
        if (!user.getEnabled()) {
            System.out.println("⚠️ User " + username + " is disabled. Auto-enabling...");
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("✅ User " + username + " has been auto-enabled");
        }

        // Check if account is locked
        if (Boolean.TRUE.equals(user.getAccountLocked()) && user.getLockTime() != null) {
            if (user.getLockTime().plusHours(24).isAfter(LocalDateTime.now())) {
                System.out.println("❌ Account locked: " + username);
                throw new UsernameNotFoundException("Account is locked due to too many failed login attempts. Please try again later or reset your password.");
            } else {
                // Auto-unlock after 24 hours
                System.out.println("🔓 Auto-unlocking account: " + username);
                user.setAccountLocked(false);
                user.setLoginAttempts(0);
                user.setLockTime(null);
                userRepository.save(user);
            }
        }

        System.out.println("✅ User details loaded successfully for: " + username);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                !Boolean.TRUE.equals(user.getAccountLocked()),
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    public void increaseFailedAttempts(User user) {
        int newAttempts = (user.getLoginAttempts() != null ? user.getLoginAttempts() : 0) + 1;
        user.setLoginAttempts(newAttempts);

        if (newAttempts >= 5) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
            System.out.println("🔒 Account locked due to too many failed attempts: " + user.getUsername());
        }

        userRepository.save(user);
    }

    public void resetFailedAttempts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.setLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
        System.out.println("🔓 Login attempts reset for: " + username);
    }
}