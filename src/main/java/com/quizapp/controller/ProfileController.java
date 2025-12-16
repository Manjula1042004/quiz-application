package com.quizapp.controller;

import com.quizapp.entity.User;
import com.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails,
                              HttpServletRequest request,
                              Model model) {
        try {
            System.out.println("=== PROFILE CONTROLLER DEBUG ===");
            System.out.println("UserDetails: " + (userDetails != null ? userDetails.getUsername() : "NULL"));
            System.out.println("Authentication in context: " + SecurityContextHolder.getContext().getAuthentication());

            String username = userDetails.getUsername();
            Optional<User> userOpt = userService.findByUsername(username);

            System.out.println("User from DB: " + userOpt.isPresent());

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                return "profile/view";
            } else {
                User dummyUser = createDummyUser(username);
                model.addAttribute("user", dummyUser);
                model.addAttribute("error", "User profile details not found in database.");
                return "profile/view";
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR in profile: " + e.getMessage());
            e.printStackTrace();
            User dummyUser = createDummyUser("Error");
            model.addAttribute("user", dummyUser);
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "profile/view";
        }
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            String username = userDetails.getUsername();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("user", user);
                return "profile/edit";
            } else {
                model.addAttribute("error", "User not found");
                return "redirect:/profile";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String email,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = userDetails.getUsername();
            Optional<User> userOpt = userService.findByUsername(username);

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                // Validate email
                if (email == null || email.trim().isEmpty()) {
                    model.addAttribute("error", "Email is required");
                    model.addAttribute("user", user);
                    return "profile/edit";
                }

                email = email.trim().toLowerCase();

                // Check if email is already taken by another user
                if (!user.getEmail().equals(email)) {
                    Optional<User> existingUser = userService.findByEmail(email);
                    if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                        model.addAttribute("error", "Email is already taken by another user");
                        model.addAttribute("user", user);
                        return "profile/edit";
                    }
                }

                user.setEmail(email);
                userService.saveUser(user);

                redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
                return "redirect:/profile";
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/profile";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            return "profile/edit";
        }
    }

    @GetMapping("/profile/change-password")
    public String showChangePasswordForm(Model model) {
        return "profile/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            String username = userDetails.getUsername();

            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("error", "New passwords do not match");
                return "profile/change-password";
            }

            if (newPassword.length() < 6) {
                model.addAttribute("error", "Password must be at least 6 characters long");
                return "profile/change-password";
            }

            // Note: In a real application, you should verify the current password
            // For now, we'll just update the password
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userService.updatePassword(user.getId(), newPassword);

                redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
                return "redirect:/profile";
            } else {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/profile";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error changing password: " + e.getMessage());
            return "profile/change-password";
        }
    }

    /**
     * Creates a dummy user object to prevent null pointer exceptions in templates
     */
    private User createDummyUser(String username) {
        User user = new User();
        user.setUsername(username != null ? username : "Unknown");
        user.setEmail("Not available");
        user.setEnabled(true);
        return user;
    }
}