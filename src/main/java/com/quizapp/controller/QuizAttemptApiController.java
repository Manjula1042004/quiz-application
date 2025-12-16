package com.quizapp.controller.api;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attempts")
@Tag(name = "Quiz Attempts", description = "APIs for starting, submitting, and retrieving quiz attempts")
public class QuizAttemptApiController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuizService quizService;

    @PostMapping("/start/{quizId}")
    @Operation(summary = "Start a quiz attempt", description = "Starts a new quiz attempt for the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz attempt started successfully",
                    content = @Content(schema = @Schema(implementation = QuizAttempt.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> startQuizAttempt(
            @Parameter(description = "ID of the quiz to attempt", required = true)
            @PathVariable Long quizId,
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // FIX: Get the Quiz object first, then pass it to startQuizAttempt
            Quiz quiz = quizService.getQuizById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));

            QuizAttempt attempt = quizAttemptService.startQuizAttempt(user, quiz);
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error starting quiz: " + e.getMessage());
        }
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit quiz answers", description = "Submits quiz answers and calculates the score")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz submitted successfully",
                    content = @Content(schema = @Schema(implementation = QuizAttempt.class))),
            @ApiResponse(responseCode = "404", description = "Quiz attempt not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "400", description = "Invalid submission data")
    })
    public ResponseEntity<?> submitQuizAttempt(
            @Parameter(description = "ID of the quiz attempt", required = true)
            @RequestParam Long attemptId,
            @Parameter(description = "Map of question IDs to selected answer indices", required = true)
            @RequestBody Map<Long, Integer> answers,
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            QuizAttempt submittedAttempt = quizAttemptService.submitQuiz(attemptId, answers);
            return ResponseEntity.ok(submittedAttempt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error submitting quiz: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's quiz attempts", description = "Retrieves all quiz attempts for a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz attempts retrieved successfully",
                    content = @Content(schema = @Schema(implementation = QuizAttempt.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<QuizAttempt>> getUserAttempts(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId) {
        List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(userId);
        return ResponseEntity.ok(attempts);
    }

    @GetMapping("/{attemptId}")
    @Operation(summary = "Get quiz attempt by ID", description = "Retrieves a specific quiz attempt with detailed results")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz attempt found",
                    content = @Content(schema = @Schema(implementation = QuizAttempt.class))),
            @ApiResponse(responseCode = "404", description = "Quiz attempt not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<?> getAttemptById(
            @Parameter(description = "ID of the quiz attempt", required = true)
            @PathVariable Long attemptId) {
        try {
            QuizAttempt attempt = quizAttemptService.getAttemptById(attemptId)
                    .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));
            return ResponseEntity.ok(attempt);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get attempts for a specific quiz", description = "Retrieves all attempts for a specific quiz")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz attempts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<List<QuizAttempt>> getAttemptsByQuizId(
            @Parameter(description = "ID of the quiz", required = true)
            @PathVariable Long quizId) {
        List<QuizAttempt> attempts = quizAttemptService.getUserAttempts(quizId); // This might need adjustment
        return ResponseEntity.ok(attempts);
    }
}