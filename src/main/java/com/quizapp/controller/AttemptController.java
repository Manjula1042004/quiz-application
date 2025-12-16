// File: src/main/java/com/quizapp/controller/AttemptController.java
package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/attempt")
public class AttemptController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @GetMapping("/start/{quizId}")
    public String startQuizAttempt(@PathVariable Long quizId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   Model model) {
        try {
            System.out.println("Starting quiz attempt for quiz ID: " + quizId);

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
            if (quizOpt.isEmpty()) {
                model.addAttribute("error", "Quiz not found");
                return "redirect:/quiz/list";
            }

            Quiz quiz = quizOpt.get();
            System.out.println("Found quiz: " + quiz.getTitle() + " with " +
                    (quiz.getQuestions() != null ? quiz.getQuestions().size() : 0) + " questions");

            // Check if user already has an active attempt for this quiz
            QuizAttempt attempt = quizAttemptService.startQuizAttempt(user, quiz);

            System.out.println("Started quiz attempt: " + attempt.getId() + " for user: " + user.getUsername());

            // Directly redirect to take quiz page with attempt ID
            return "redirect:/attempt/take/" + attempt.getId();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error starting quiz: " + e.getMessage());
            return "redirect:/quiz/list";
        }
    }

    // NEW METHOD: Direct quiz taking page
    @GetMapping("/take/{attemptId}")
    public String takeQuiz(@PathVariable Long attemptId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        try {
            System.out.println("Loading quiz take page for attempt ID: " + attemptId);

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            QuizAttempt attempt = quizAttemptService.getAttemptById(attemptId)
                    .orElseThrow(() -> new RuntimeException("Attempt not found"));

            // Verify the attempt belongs to the current user
            if (!attempt.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "Access denied");
                return "redirect:/dashboard";
            }

            Quiz quiz = attempt.getQuiz();

            // Initialize questions to avoid LazyInitializationException
            if (quiz.getQuestions() != null) {
                quiz.getQuestions().size(); // Force initialization
                System.out.println("Quiz has " + quiz.getQuestions().size() + " questions");

                // Initialize options for each question
                quiz.getQuestions().forEach(question -> {
                    if (question.getOptions() != null) {
                        question.getOptions().size(); // Force initialization
                    }
                });
            }

            model.addAttribute("attempt", attempt);
            model.addAttribute("quiz", quiz);
            model.addAttribute("user", user);

            System.out.println("Successfully loaded quiz take page for: " + quiz.getTitle());
            return "quiz/take";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading quiz: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PostMapping("/submit")
    public String submitQuizAttempt(@RequestParam("attemptId") Long attemptId,
                                    @RequestParam("quizId") Long quizId,
                                    @RequestParam Map<String, String> allParams,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        try {
            System.out.println("=== QUIZ SUBMISSION STARTED ===");
            System.out.println("Attempt ID: " + attemptId);
            System.out.println("Quiz ID: " + quizId);

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Extract answers from parameters
            Map<Long, Integer> answers = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("answers[")) {
                    String key = entry.getKey().replace("answers[", "").replace("]", "");
                    try {
                        Long questionId = Long.parseLong(key);
                        Integer selectedOption = Integer.parseInt(entry.getValue());
                        answers.put(questionId, selectedOption);
                        System.out.println("Answer for question " + questionId + ": " + selectedOption);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid answer format: " + entry.getKey() + "=" + entry.getValue());
                    }
                }
            }

            System.out.println("Total answers collected: " + answers.size());

            // Submit the quiz attempt
            QuizAttempt submittedAttempt = quizAttemptService.submitQuiz(attemptId, answers);

            System.out.println("Quiz submitted successfully! Score: " + submittedAttempt.getScore());

            // Add results to model
            model.addAttribute("attempt", submittedAttempt);
            model.addAttribute("quiz", submittedAttempt.getQuiz());
            model.addAttribute("score", submittedAttempt.getScore());
            model.addAttribute("totalQuestions", submittedAttempt.getQuiz().getQuestions().size());
            model.addAttribute("success", "Quiz submitted successfully! Your score: " +
                    String.format("%.2f", submittedAttempt.getScore()) + "%");

            return "quiz/results";

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR in quiz submission: " + e.getMessage());
            model.addAttribute("error", "Error submitting quiz: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/results/{attemptId}")
    public String viewQuizResults(@PathVariable Long attemptId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<QuizAttempt> attemptOpt = quizAttemptService.getAttemptById(attemptId);
            if (attemptOpt.isEmpty()) {
                throw new RuntimeException("Attempt not found");
            }

            QuizAttempt attempt = attemptOpt.get();

            // Verify the attempt belongs to the current user
            if (!attempt.getUser().getId().equals(user.getId())) {
                model.addAttribute("error", "Access denied");
                return "redirect:/dashboard";
            }

            model.addAttribute("attempt", attempt);
            model.addAttribute("quiz", attempt.getQuiz());
            model.addAttribute("score", attempt.getScore());
            model.addAttribute("totalQuestions", attempt.getQuiz().getQuestions().size());

            return "quiz/results";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error viewing results: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }
}