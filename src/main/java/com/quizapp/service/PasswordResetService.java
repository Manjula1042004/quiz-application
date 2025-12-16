// File: src/main/java/com/quizapp/service/PasswordResetService.java
package com.quizapp.service;

import com.quizapp.entity.PasswordResetToken;
import com.quizapp.entity.User;
import com.quizapp.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String createPasswordResetToken(String email) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found with this email";
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);

        // Delete existing tokens for this user
        tokenRepository.deleteByUser(user);

        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(user, token);

        return "Password reset link sent to your email";
    }

    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passToken = tokenRepository.findByToken(token);
        if (passToken.isEmpty()) {
            return "Invalid token";
        }

        if (passToken.get().isExpired()) {
            return "Token has expired";
        }

        return "valid";
    }

    public String resetPassword(String token, String newPassword) {
        String validationResult = validatePasswordResetToken(token);
        if (!validationResult.equals("valid")) {
            return validationResult;
        }

        Optional<PasswordResetToken> passToken = tokenRepository.findByToken(token);
        if (passToken.isPresent()) {
            User user = passToken.get().getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.saveUser(user);

            // Delete the used token
            tokenRepository.delete(passToken.get());

            return "Password reset successfully";
        }

        return "Invalid token";
    }

    public void cleanExpiredTokens() {
        tokenRepository.deleteAllByExpiryDateBefore(LocalDateTime.now());
    }
}