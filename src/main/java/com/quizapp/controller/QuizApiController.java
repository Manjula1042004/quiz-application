package com.quizapp.controller.api;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
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

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Quizzes", description = "Quiz management APIs for creating, reading, updating, and deleting quizzes")
public class QuizApiController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Get all quizzes", description = "Retrieves a list of all available quizzes in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all quizzes",
                    content = @Content(schema = @Schema(implementation = Quiz.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quiz by ID", description = "Retrieves a specific quiz by its ID including all questions and details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz found successfully",
                    content = @Content(schema = @Schema(implementation = Quiz.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "400", description = "Invalid quiz ID")
    })
    public ResponseEntity<?> getQuizById(
            @Parameter(description = "ID of the quiz to retrieve", required = true)
            @PathVariable Long id) {
        try {
            Quiz quiz = quizService.getQuizById(id)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));
            return ResponseEntity.ok(quiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Create a new quiz", description = "Creates a new quiz with the provided details. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz created successfully",
                    content = @Content(schema = @Schema(implementation = Quiz.class))),
            @ApiResponse(responseCode = "400", description = "Invalid quiz data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - ADMIN role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createQuiz(
            @Parameter(description = "Quiz object to create", required = true)
            @RequestBody Quiz quiz,
            @Parameter(description = "Authenticated user details", hidden = true)
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Quiz createdQuiz = quizService.createQuiz(quiz, user);
            return ResponseEntity.ok(createdQuiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a quiz", description = "Updates an existing quiz with new details. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz updated successfully",
                    content = @Content(schema = @Schema(implementation = Quiz.class))),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - ADMIN role required"),
            @ApiResponse(responseCode = "400", description = "Invalid quiz data")
    })
    public ResponseEntity<?> updateQuiz(
            @Parameter(description = "ID of the quiz to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated quiz details", required = true)
            @RequestBody Quiz quizDetails) {
        try {
            Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
            return ResponseEntity.ok(updatedQuiz);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a quiz", description = "Deletes a quiz and all its associated questions. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Quiz deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quiz not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - ADMIN role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteQuiz(
            @Parameter(description = "ID of the quiz to delete", required = true)
            @PathVariable Long id) {
        try {
            quizService.deleteQuiz(id);
            return ResponseEntity.ok("Quiz deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}