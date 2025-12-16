package com.quizapp.controller.api;

import com.quizapp.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@Tag(name = "Email", description = "Email testing and management APIs")
public class EmailApiController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/test")
    @Operation(summary = "Send test email", description = "Sends a test email to verify email functionality")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Test email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email address"),
            @ApiResponse(responseCode = "500", description = "Email sending failed")
    })
    public ResponseEntity<?> sendTestEmail(
            @Parameter(description = "Email address to send test to", required = true)
            @RequestParam String email,
            @Parameter(description = "Type of test email", required = true)
            @RequestParam String testType) {
        try {
            emailService.sendTestEmail(email, testType);
            return ResponseEntity.ok("Test email sent successfully to: " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send test email: " + e.getMessage());
        }
    }
}