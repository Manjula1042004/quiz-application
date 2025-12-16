package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import com.quizapp.util.DashboardUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizAttemptService quizAttemptService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
                            Authentication authentication,
                            Model model) {
        try {
            String username = getUsername(userDetails, authentication);

            if (username == null) {
                logger.warn("No username found, redirecting to login");
                return "redirect:/login";
            }

            logger.info("Loading dashboard for user: {}", username);

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // ✅ FIX: Ensure user object is added to model
            model.addAttribute("user", user);

            if (user.getRole().name().equals("ADMIN")) {
                return handleAdminDashboard(user, model);
            } else {
                return handleParticipantDashboard(user, model);
            }
        } catch (Exception e) {
            logger.error("Error loading dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "redirect:/login";
        }
    }

    private String getUsername(UserDetails userDetails, Authentication authentication) {
        if (userDetails != null) {
            return userDetails.getUsername();
        } else if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            String username = userDetails != null ? userDetails.getUsername() : null;

            if (username == null) {
                logger.warn("No username found in admin dashboard, redirecting to login");
                return "redirect:/login";
            }

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            if (!user.getRole().name().equals("ADMIN")) {
                logger.warn("Non-admin user {} attempted to access admin dashboard", username);
                return "redirect:/dashboard";
            }

            // ✅ FIX: Ensure user object is added to model
            model.addAttribute("user", user);
            return handleAdminDashboard(user, model);
        } catch (Exception e) {
            logger.error("Error loading admin dashboard: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading admin dashboard");
            return "redirect:/login";
        }
    }

    @GetMapping("/participant-view")
    public String participantView(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        try {
            String username = userDetails != null ? userDetails.getUsername() : null;

            if (username == null) {
                return "redirect:/login";
            }

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // For admins, show participant view with all quizzes
            List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(user.getId());
            List<Quiz> availableQuizzes = quizService.getAllQuizzes();

            // Calculate stats
            long completedCount = DashboardUtil.getCompletedAttemptsCount(attempts);
            long inProgressCount = DashboardUtil.getInProgressAttemptsCount(attempts);
            double averageScore = DashboardUtil.calculateAverageScore(attempts);

            // ✅ FIX: Ensure user object is added to model
            model.addAttribute("user", user);
            model.addAttribute("attempts", attempts != null ? attempts : new ArrayList<>());
            model.addAttribute("quizzes", availableQuizzes != null ? availableQuizzes : new ArrayList<>());
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("inProgressCount", inProgressCount);
            model.addAttribute("averageScore", averageScore);
            model.addAttribute("isAdminView", true);

            return "participant/dashboard";
        } catch (Exception e) {
            logger.error("Error loading participant view: {}", e.getMessage(), e);
            model.addAttribute("error", "Error loading participant view");
            return "redirect:/admin/dashboard";
        }
    }

    private String handleAdminDashboard(User user, Model model) {
        try {
            // ✅ FIX: Get ALL quizzes for admin (not just user's quizzes)
            List<Quiz> allQuizzes = quizService.getAllQuizzes();

            // ✅ FIX: Initialize questions for each quiz to avoid LazyInitializationException
            for (Quiz quiz : allQuizzes) {
                if (quiz.getQuestions() != null) {
                    quiz.getQuestions().size(); // Force initialization
                }
            }

            int totalQuestions = allQuizzes.stream()
                    .mapToInt(quiz -> quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                    .sum();

            long participantCount = userService.findAllParticipants().size();

            // ✅ FIX: Ensure ALL required attributes are added to model
            model.addAttribute("user", user); // This was missing!
            model.addAttribute("quizzes", allQuizzes != null ? allQuizzes : new ArrayList<>());
            model.addAttribute("totalQuestions", totalQuestions);
            model.addAttribute("participantCount", participantCount);
            model.addAttribute("quizCount", allQuizzes.size());

            logger.info("Admin dashboard loaded for user: {} with {} quizzes and {} total questions",
                    user.getUsername(), allQuizzes.size(), totalQuestions);

            return "admin/dashboard";

        } catch (Exception e) {
            logger.error("Error loading admin dashboard: {}", e.getMessage(), e);
            // ✅ FIX: Add user to model even when there's an error
            model.addAttribute("user", user);
            model.addAttribute("error", "Failed to load admin dashboard: " + e.getMessage());
            return "admin/dashboard"; // Don't redirect, show error on dashboard
        }
    }



    // In DashboardController.java, update the handleParticipantDashboard method:
    private String handleParticipantDashboard(User user, Model model) {
        try {
            List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(user.getId());

            // ✅ FIX: Use getPublicQuizzes() which filters properly
            List<Quiz> availableQuizzes = quizService.getPublicQuizzes();

            // ✅ DEBUG LOGGING
            System.out.println("🔍 DEBUG handleParticipantDashboard:");
            System.out.println("User: " + user.getUsername());
            System.out.println("Available quizzes count: " + availableQuizzes.size());

            for (Quiz quiz : availableQuizzes) {
                System.out.println(" - Quiz: " + quiz.getTitle() +
                        " (ID: " + quiz.getId() +
                        ", Public: " + quiz.getIsPublic() +
                        ", Enabled: " + quiz.getEnabled() + ")");
            }

            // Calculate stats
            long completedCount = DashboardUtil.getCompletedAttemptsCount(attempts);
            long inProgressCount = DashboardUtil.getInProgressAttemptsCount(attempts);
            double averageScore = DashboardUtil.calculateAverageScore(attempts);

            // ✅ FIX: Ensure all attributes are added to model
            model.addAttribute("user", user);
            model.addAttribute("attempts", attempts != null ? attempts : new ArrayList<>());
            model.addAttribute("quizzes", availableQuizzes != null ? availableQuizzes : new ArrayList<>());
            model.addAttribute("completedCount", completedCount);
            model.addAttribute("inProgressCount", inProgressCount);
            model.addAttribute("averageScore", averageScore);

            logger.info("Participant dashboard loaded for user: {} with {} attempts and {} available quizzes",
                    user.getUsername(), attempts.size(), availableQuizzes.size());

            return "participant/dashboard";
        } catch (Exception e) {
            logger.error("Error loading participant dashboard: {}", e.getMessage(), e);
            // ✅ FIX: Add user to model even when there's an error
            model.addAttribute("user", user);
            model.addAttribute("error", "Failed to load participant dashboard: " + e.getMessage());
            return "participant/dashboard"; // Don't redirect, show error on dashboard
        }
    }
}