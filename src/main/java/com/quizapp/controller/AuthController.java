package com.quizapp.controller;

import com.quizapp.dto.UserRegistrationDto;
import com.quizapp.entity.Role;
import com.quizapp.entity.User;
import com.quizapp.security.PasswordValidator;
import com.quizapp.service.EmailVerificationService;
import com.quizapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "locked", required = false) String locked,
                                @RequestParam(value = "expired", required = false) String expired,
                                @RequestParam(value = "invalidSession", required = false) String invalidSession,
                                @RequestParam(value = "verified", required = false) String verified,
                                @RequestParam(value = "unverified", required = false) String unverified,
                                @RequestParam(value = "disabled", required = false) String disabled,
                                HttpServletRequest request,
                                Model model) {

        // ... your existing login form logic ...
        if (error != null) {
            String errorMessage = "Invalid username or password!";
            Exception exception = (Exception) request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
            if (exception != null) {
                String exceptionMessage = exception.getMessage();
                logger.debug("Authentication exception: {}", exceptionMessage);

                if (exception instanceof DisabledException) {
                    errorMessage = "Account not verified. Please check your email to verify your account before logging in. " +
                            "If you didn't receive the verification email, use the 'Resend Verification' option below.";
                    model.addAttribute("showResendLink", true);
                } else if (exceptionMessage != null) {
                    if (exceptionMessage.contains("locked")) {
                        errorMessage = "Account is locked due to too many failed login attempts. Please try again later.";
                    } else if (exceptionMessage.contains("credentials")) {
                        errorMessage = "Invalid username or password!";
                    } else if (exceptionMessage.contains("disabled")) {
                        errorMessage = "Account not verified. Please check your email to verify your account before logging in.";
                        model.addAttribute("showResendLink", true);
                    }
                }
            }
            model.addAttribute("error", errorMessage);
        }

        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully.");
        }
        if (locked != null) {
            model.addAttribute("error", "Account is locked due to too many failed login attempts. Please try again later.");
        }
        if (expired != null) {
            model.addAttribute("error", "Your session has expired. Please login again.");
        }
        if (invalidSession != null) {
            model.addAttribute("error", "Invalid session. Please login again.");
        }
        if (verified != null) {
            model.addAttribute("success", "Email verified successfully! You can now login.");
        }
        if (unverified != null) {
            model.addAttribute("error", "Please verify your email before logging in. Check your inbox for the verification link.");
            model.addAttribute("showResendLink", true);
        }
        if (disabled != null) {
            model.addAttribute("error", "Account is disabled. Please contact administrator.");
        }

        return "auth/login";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result, Model model) {
        logger.info("Registration attempt for user: {}", registrationDto.getUsername());

        // Basic validation
        if (result.hasErrors()) {
            logger.warn("Registration validation errors: {}", result.getAllErrors());
            return "auth/register";
        }

        // Password confirmation validation
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Passwords do not match");
            logger.warn("Password confirmation failed for user: {}", registrationDto.getUsername());
            return "auth/register";
        }

        // Password strength validation
        PasswordValidator.PasswordValidationResult passwordValidation = registrationDto.validatePasswordStrength();
        if (!passwordValidation.isValid()) {
            for (String error : passwordValidation.getErrors()) {
                result.rejectValue("password", "error.user", error);
            }
            logger.warn("Password strength validation failed for user: {}", registrationDto.getUsername());
            model.addAttribute("passwordStrength", passwordValidation.getStrengthLevel());
            model.addAttribute("passwordScore", passwordValidation.getStrengthScore());
            return "auth/register";
        }

        try {
            // Determine role
            Role role = registrationDto.getRole() != null && registrationDto.getRole().equals("ADMIN")
                    ? Role.ADMIN : Role.PARTICIPANT;

            logger.info("Creating user with role: {}", role);

            // Register user (initially enabled - email verification handled separately)
            User user = userService.registerUser(
                    registrationDto.getUsername(),
                    registrationDto.getEmail(),
                    registrationDto.getPassword(),
                    role
            );

            logger.info("User registered successfully: {}", user.getUsername());

            // ✅ FIX: Create verification token after user is registered
            try {
                emailVerificationService.createVerificationToken(user, userService);
                model.addAttribute("success",
                        "Registration successful! Please check your email (" + user.getEmail() +
                                ") to verify your account before logging in. " +
                                "If you don't see the email, check your spam folder.");
                model.addAttribute("showResendLink", true);
                model.addAttribute("userEmail", user.getEmail());
            } catch (Exception emailException) {
                logger.warn("Failed to send verification email, but user was registered: {}", emailException.getMessage());
                model.addAttribute("success",
                        "Registration successful! You can now login. " +
                                "Note: Verification email could not be sent.");
            }

            return "auth/login";

        } catch (RuntimeException e) {
            logger.error("User registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        // ✅ FIX: Pass UserService to the verification method
        boolean verified = emailVerificationService.verifyEmail(token, userService);

        if (verified) {
            model.addAttribute("success",
                    "Email verified successfully! You can now login to your account.");
        } else {
            model.addAttribute("error",
                    "Invalid or expired verification token. Please request a new verification email.");
            model.addAttribute("showResendLink", true);
        }

        return "auth/login";
    }

    @GetMapping("/resend-verification")
    public String showResendVerificationForm(Model model) {
        return "auth/resend-verification";
    }

    @PostMapping("/resend-verification")
    public String resendVerification(@RequestParam("email") String email, Model model) {
        try {
            // ✅ FIX: Pass UserService to the resend method
            emailVerificationService.resendVerificationToken(email, userService);
            model.addAttribute("success",
                    "Verification email sent! Please check your inbox and spam folder.");
            return "auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Error sending verification email: " + e.getMessage());
            return "auth/resend-verification";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("User logged out: {}", auth.getName());
        }
        return "redirect:/login?logout";
    }
}