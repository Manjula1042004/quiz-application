// File: src/main/java/com/quizapp/controller/PasswordResetController.java
package com.quizapp.controller;

import com.quizapp.service.PasswordResetService;
import com.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        try {
            String result = passwordResetService.createPasswordResetToken(email);
            model.addAttribute("message", result);
        } catch (Exception e) {
            model.addAttribute("error", "Error processing request: " + e.getMessage());
        }
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        String validationResult = passwordResetService.validatePasswordResetToken(token);

        if (!validationResult.equals("valid")) {
            model.addAttribute("error", validationResult);
            return "auth/reset-password-error";
        }

        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model) {
        try {
            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Passwords do not match");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }

            if (password.length() < 6) {
                model.addAttribute("error", "Password must be at least 6 characters long");
                model.addAttribute("token", token);
                return "auth/reset-password";
            }

            String result = passwordResetService.resetPassword(token, password);
            if (result.equals("Password reset successfully")) {
                model.addAttribute("success", result);
                return "auth/reset-password-success";
            } else {
                model.addAttribute("error", result);
                return "auth/reset-password-error";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error resetting password: " + e.getMessage());
            return "auth/reset-password-error";
        }
    }
}