package com.quizapp.validation;

import com.quizapp.dto.UserRegistrationDto;
import com.quizapp.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CustomValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userRegistrationDto_ValidData_ShouldPassValidation() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("validuser");
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123!");
        dto.setConfirmPassword("ValidPass123!");

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void userRegistrationDto_InvalidEmail_ShouldFailValidation() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("validuser");
        dto.setEmail("invalid-email");
        dto.setPassword("ValidPass123!");
        dto.setConfirmPassword("ValidPass123!");

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void userRegistrationDto_ShortUsername_ShouldFailValidation() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("ab"); // Too short
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123!");
        dto.setConfirmPassword("ValidPass123!");

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void userRegistrationDto_LongUsername_ShouldFailValidation() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("a".repeat(51)); // Too long
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123!");
        dto.setConfirmPassword("ValidPass123!");

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void userRegistrationDto_ShortPassword_ShouldFailValidation() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("validuser");
        dto.setEmail("valid@example.com");
        dto.setPassword("short"); // Too short
        dto.setConfirmPassword("short");

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void userRegistrationDto_NullUsername_ShouldFailValidation() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(null); // Null username
        dto.setEmail("valid@example.com");
        dto.setPassword("ValidPass123!");
        dto.setConfirmPassword("ValidPass123!");

        Set<ConstraintViolation<UserRegistrationDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void userEntity_ValidData_ShouldPassValidation() {
        User user = new User();
        user.setUsername("validuser");
        user.setEmail("valid@example.com");
        user.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void userEntity_InvalidEmail_ShouldFailValidation() {
        User user = new User();
        user.setUsername("validuser");
        user.setEmail("invalid-email");
        user.setPassword("password123");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void customPasswordValidation_WeakPassword_ShouldFail() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("weak"); // Weak password
        dto.setConfirmPassword("weak");

        var validationResult = dto.validatePasswordStrength();

        assertFalse(validationResult.isValid());
        assertFalse(validationResult.getErrors().isEmpty());
    }

    @Test
    void customPasswordValidation_StrongPassword_ShouldPass() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("StrongPass123!");
        dto.setConfirmPassword("StrongPass123!");

        var validationResult = dto.validatePasswordStrength();

        assertTrue(validationResult.isValid());
        assertTrue(validationResult.getErrors().isEmpty());
        assertEquals("STRONG", validationResult.getStrengthLevel());
    }
}