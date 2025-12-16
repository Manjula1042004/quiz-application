package com.quizapp.service;

import com.quizapp.entity.EmailVerificationToken;
import com.quizapp.entity.User;
import com.quizapp.repository.EmailVerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;

    // ✅ FIX: Remove direct UserService dependency
    @Autowired
    public EmailVerificationService(EmailVerificationTokenRepository tokenRepository,
                                    EmailService emailService) {
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    // ✅ FIX: Accept UserService as parameter when needed
    public void createVerificationToken(User user, UserService userService) {
        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Create new token
        EmailVerificationToken token = new EmailVerificationToken(user);
        tokenRepository.save(token);

        // Send verification email
        sendVerificationEmail(user, token.getToken());
    }

    // ✅ FIX: Accept UserService as parameter
    public boolean verifyEmail(String token, UserService userService) {
        Optional<EmailVerificationToken> verificationToken = tokenRepository.findByToken(token);

        if (verificationToken.isPresent()) {
            EmailVerificationToken emailToken = verificationToken.get();

            if (emailToken.isValid()) {
                // Mark token as used
                emailToken.setUsed(true);
                tokenRepository.save(emailToken);

                // Enable user account using provided UserService
                User user = emailToken.getUser();
                user.setEnabled(true);
                userService.saveUser(user);

                // Send welcome email
                emailService.sendRegistrationEmail(user);

                return true;
            }
        }

        return false;
    }

    // ✅ FIX: Accept UserService as parameter
    public void resendVerificationToken(String email, UserService userService) {
        Optional<User> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent() && !userOpt.get().getEnabled()) {
            createVerificationToken(userOpt.get(), userService);
        } else if (userOpt.isPresent() && userOpt.get().getEnabled()) {
            throw new RuntimeException("Email is already verified");
        } else {
            throw new RuntimeException("User not found with this email");
        }
    }

    public boolean isTokenValid(String token) {
        Optional<EmailVerificationToken> verificationToken = tokenRepository.findByToken(token);
        return verificationToken.isPresent() && verificationToken.get().isValid();
    }

    private void sendVerificationEmail(User user, String token) {
        String verificationLink = "http://localhost:8080/verify-email?token=" + token;

        // Create email content
        String subject = "Verify Your Email - QuizApp";
        String text = String.format(
                "Dear %s,\n\n" +
                        "Thank you for registering with QuizApp! Please verify your email address by clicking the link below:\n\n" +
                        "%s\n\n" +
                        "This link will expire in 24 hours.\n\n" +
                        "If you didn't create an account, please ignore this email.\n\n" +
                        "Best regards,\nQuizApp Team",
                user.getUsername(), verificationLink
        );

        // Send the email using the existing email service
        emailService.sendSimpleEmail(user.getEmail(), subject, text);
    }

    // Clean up expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredTokens() {
        tokenRepository.deleteAllExpiredSince(LocalDateTime.now());
    }
}