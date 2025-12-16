package com.quizapp.controller.api;

import com.quizapp.dto.AuthRequest;
import com.quizapp.dto.AuthResponse;
import com.quizapp.dto.UserRegistrationDto;
import com.quizapp.entity.Role;
import com.quizapp.entity.User;
import com.quizapp.security.JwtUtil;
import com.quizapp.service.CustomUserDetailsService;
import com.quizapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication APIs for user registration, login, and token management")
public class AuthApiController {
    private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with either ADMIN or PARTICIPANT role")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        logger.info("API Registration attempt for user: {}", registrationDto.getUsername());

        try {
            if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match");
            }

            Role role = registrationDto.getRole() != null && registrationDto.getRole().equals("ADMIN")
                    ? Role.ADMIN : Role.PARTICIPANT;

            User user = userService.registerUser(
                    registrationDto.getUsername(),
                    registrationDto.getEmail(),
                    registrationDto.getPassword(),
                    role
            );

            logger.info("API User registered successfully: {}", user.getUsername());
            return ResponseEntity.ok("User registered successfully");

        } catch (RuntimeException e) {
            logger.error("API User registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token", description = "Authenticates user credentials and returns JWT token for API access")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "423", description = "Account locked"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        logger.info("API Login attempt for user: {}", authRequest.getUsername());

        try {
            if (userService.isAccountLocked(authRequest.getUsername())) {
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body("Account is locked due to too many failed login attempts");
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authRequest.getUsername());
            User user = userService.findByUsername(authRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            final String jwt = jwtUtil.generateToken(user);
            userService.handleLoginSuccess(authRequest.getUsername());

            AuthResponse response = new AuthResponse(
                    jwt,
                    user.getUsername(),
                    user.getRole().name()
            );
            response.setEmail(user.getEmail());
            response.setUserId(user.getId());

            logger.info("API User logged in successfully: {}", user.getUsername());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            userService.handleLoginFailure(authRequest.getUsername());
            logger.warn("API Invalid credentials for user: {}", authRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            logger.error("API Login error for user {}: {}", authRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    @PostMapping("/validate-token")
    @Operation(summary = "Validate JWT token", description = "Validates the provided JWT token and returns user information if valid")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token is valid",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
            @ApiResponse(responseCode = "400", description = "Invalid authorization header")
    })
    public ResponseEntity<?> validateToken(
            @Parameter(description = "Bearer token in format: 'Bearer {token}'", required = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            boolean isValid = jwtUtil.validateToken(token);

            if (isValid) {
                String username = jwtUtil.extractUsername(token);
                User user = userService.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                AuthResponse response = new AuthResponse(
                        token,
                        user.getUsername(),
                        user.getRole().name()
                );
                response.setEmail(user.getEmail());
                response.setUserId(user.getId());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed");
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user information", description = "Returns information about the currently authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User information retrieved",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "400", description = "Invalid authorization header")
    })
    public ResponseEntity<?> getCurrentUser(
            @Parameter(description = "Bearer token in format: 'Bearer {token}'", required = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Invalid authorization header");
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            AuthResponse response = new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getRole().name()
            );
            response.setEmail(user.getEmail());
            response.setUserId(user.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get user information");
        }
    }
}