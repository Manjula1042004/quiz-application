package com.quizapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PasswordValidator Tests")
class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    @DisplayName("Should validate strong password")
    void validate_StrongPassword_ReturnsValid() {
        // Given
        String strongPassword = "StrongPass123!";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(strongPassword);

        // Then
        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
        assertTrue(result.getStrengthScore() >= 8);
        assertEquals("STRONG", result.getStrengthLevel());
    }

    @Test
    @DisplayName("Should reject null password")
    void validate_NullPassword_ReturnsInvalid() {
        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(null);

        // Then
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("cannot be empty"));
    }

    @Test
    @DisplayName("Should reject empty password")
    void validate_EmptyPassword_ReturnsInvalid() {
        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate("");

        // Then
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("cannot be empty"));
    }

    @Test
    @DisplayName("Should reject whitespace-only password")
    void validate_WhitespacePassword_ReturnsInvalid() {
        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate("   ");

        // Then
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertTrue(result.getErrors().get(0).contains("cannot be empty"));
    }

    @Test
    @DisplayName("Should reject password that's too short")
    void validate_ShortPassword_ReturnsInvalid() {
        // Given
        String shortPassword = "Short1!";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(shortPassword);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("at least 8 characters")));
    }

    @Test
    @DisplayName("Should reject password without uppercase")
    void validate_NoUppercasePassword_ReturnsInvalid() {
        // Given
        String noUppercase = "lowercase123!";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(noUppercase);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("uppercase letter")));
    }

    @Test
    @DisplayName("Should reject password without lowercase")
    void validate_NoLowercasePassword_ReturnsInvalid() {
        // Given
        String noLowercase = "UPPERCASE123!";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(noLowercase);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("lowercase letter")));
    }

    @Test
    @DisplayName("Should reject password without digit")
    void validate_NoDigitPassword_ReturnsInvalid() {
        // Given
        String noDigit = "NoDigitPass!";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(noDigit);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("digit")));
    }

    @Test
    @DisplayName("Should reject password without special character")
    void validate_NoSpecialCharPassword_ReturnsInvalid() {
        // Given
        String noSpecialChar = "NoSpecial123";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(noSpecialChar);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("special character")));
    }

    @Test
    @DisplayName("Should reject password with whitespace")
    void validate_PasswordWithWhitespace_ReturnsInvalid() {
        // Given
        String withWhitespace = "Password 123!";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(withWhitespace);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("whitespace")));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "password",
            "123456",
            "qwerty",
            "admin",
            "12345678",
            "123456789",
            "Password123!", // Contains "password"
            "ADMIN123!" // Contains "admin"
    })
    @DisplayName("Should reject common passwords")
    void validate_CommonPasswords_ReturnsInvalid(String commonPassword) {
        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(commonPassword);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("common") || error.contains("guessable")));
    }

    @Test
    @DisplayName("Should calculate correct strength score for weak password")
    void calculateStrengthScore_WeakPassword_ReturnsLowScore() {
        // Given
        String weakPassword = "weakpass";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(weakPassword);

        // Then
        assertTrue(result.getStrengthScore() <= 4);
        assertEquals("WEAK", result.getStrengthLevel());
    }

    @Test
    @DisplayName("Should calculate correct strength score for medium password")
    void calculateStrengthScore_MediumPassword_ReturnsMediumScore() {
        // Given
        String mediumPassword = "MediumPass12";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(mediumPassword);

        // Then
        assertTrue(result.getStrengthScore() >= 5 && result.getStrengthScore() < 8);
        assertEquals("MEDIUM", result.getStrengthLevel());
    }

    @Test
    @DisplayName("Should calculate correct strength score for strong password")
    void calculateStrengthScore_StrongPassword_ReturnsHighScore() {
        // Given
        String strongPassword = "VeryStrongPass123!@#";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(strongPassword);

        // Then
        assertTrue(result.getStrengthScore() >= 8);
        assertEquals("STRONG", result.getStrengthLevel());
    }

    @Test
    @DisplayName("Should cap strength score at 10")
    void calculateStrengthScore_VeryStrongPassword_CappedAt10() {
        // Given - A very strong password
        String veryStrongPassword = "SuperStrongPassword123!@#$%^&*()";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(veryStrongPassword);

        // Then
        assertTrue(result.getStrengthScore() <= 10);
        assertEquals("STRONG", result.getStrengthLevel());
    }

    @Test
    @DisplayName("Should accumulate multiple errors")
    void validate_MultipleIssues_ReturnsAllErrors() {
        // Given - Password with multiple issues
        String badPassword = "short";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(badPassword);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() > 1);
    }

    @Test
    @DisplayName("Should handle password at maximum length")
    void validate_MaxLengthPassword_ReturnsValid() {
        // Given - Create a 128 character password
        StringBuilder maxLengthPassword = new StringBuilder("A1!");
        while (maxLengthPassword.length() < 128) {
            maxLengthPassword.append("a");
        }

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(maxLengthPassword.toString());

        // Then
        // It should be valid (meets all criteria)
        assertTrue(result.isValid() || result.getErrors().isEmpty());
    }

    @Test
    @DisplayName("Should reject password exceeding maximum length")
    void validate_ExceedsMaxLengthPassword_ReturnsInvalid() {
        // Given - Create a 129 character password
        StringBuilder tooLongPassword = new StringBuilder("A1!");
        while (tooLongPassword.length() <= 128) {
            tooLongPassword.append("a");
        }

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(tooLongPassword.toString());

        // Then
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("cannot exceed 128 characters")));
    }

    @Test
    @DisplayName("Should include all character types for maximum strength")
    void validate_PasswordWithAllCharTypes_HasHighStrength() {
        // Given
        String completePassword = "Aa1!Bb2@Cc3#";

        // When
        PasswordValidator.PasswordValidationResult result = passwordValidator.validate(completePassword);

        // Then
        assertEquals(0, result.getErrors().size());
        assertTrue(result.getStrengthScore() >= 8);
        assertEquals("STRONG", result.getStrengthLevel());
    }

    @Test
    @DisplayName("PasswordValidationResult getters and setters should work")
    void passwordValidationResult_GettersSetters_WorkCorrectly() {
        // Given
        PasswordValidator.PasswordValidationResult result = new PasswordValidator.PasswordValidationResult();

        // When
        result.setValid(true);
        result.setStrengthScore(9);
        result.setStrengthLevel("STRONG");
        result.getErrors().add("Test error");

        // Then
        assertTrue(result.isValid());
        assertEquals(9, result.getStrengthScore());
        assertEquals("STRONG", result.getStrengthLevel());
        assertEquals(1, result.getErrors().size());
        assertEquals("Test error", result.getErrors().get(0));
    }
}