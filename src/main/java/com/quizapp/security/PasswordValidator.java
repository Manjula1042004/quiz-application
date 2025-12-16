package com.quizapp.security;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;

    // Regex patterns for password validation
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    private static final Pattern NO_WHITESPACE_PATTERN = Pattern.compile("^\\S*$");
    private static final Pattern NO_COMMON_PATTERN = Pattern.compile("^(?!(?:password|123456|qwerty|admin|12345678|123456789)).*$", Pattern.CASE_INSENSITIVE);

    public PasswordValidationResult validate(String password) {
        PasswordValidationResult result = new PasswordValidationResult();

        if (password == null || password.trim().isEmpty()) {
            result.setValid(false);
            result.getErrors().add("Password cannot be empty");
            return result;
        }

        // Length validation
        if (password.length() < MIN_LENGTH) {
            result.setValid(false);
            result.getErrors().add("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password.length() > MAX_LENGTH) {
            result.setValid(false);
            result.getErrors().add("Password cannot exceed " + MAX_LENGTH + " characters");
        }

        // Character type validation
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            result.setValid(false);
            result.getErrors().add("Password must contain at least one uppercase letter");
        }

        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            result.setValid(false);
            result.getErrors().add("Password must contain at least one lowercase letter");
        }

        if (!DIGIT_PATTERN.matcher(password).matches()) {
            result.setValid(false);
            result.getErrors().add("Password must contain at least one digit");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            result.setValid(false);
            result.getErrors().add("Password must contain at least one special character");
        }

        // Whitespace validation
        if (!NO_WHITESPACE_PATTERN.matcher(password).matches()) {
            result.setValid(false);
            result.getErrors().add("Password cannot contain whitespace");
        }

        // Common password validation
        if (!NO_COMMON_PATTERN.matcher(password).matches()) {
            result.setValid(false);
            result.getErrors().add("Password is too common or easily guessable");
        }

        // Calculate strength score
        int strengthScore = calculateStrengthScore(password);
        result.setStrengthScore(strengthScore);
        result.setStrengthLevel(getStrengthLevel(strengthScore));

        // If no errors, mark as valid
        if (result.getErrors().isEmpty()) {
            result.setValid(true);
        }

        return result;
    }

    private int calculateStrengthScore(String password) {
        int score = 0;

        // Length score
        if (password.length() >= 12) score += 3;
        else if (password.length() >= 10) score += 2;
        else if (password.length() >= 8) score += 1;

        // Character variety score
        int varietyCount = 0;
        if (UPPERCASE_PATTERN.matcher(password).matches()) varietyCount++;
        if (LOWERCASE_PATTERN.matcher(password).matches()) varietyCount++;
        if (DIGIT_PATTERN.matcher(password).matches()) varietyCount++;
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) varietyCount++;

        score += varietyCount;

        // Entropy bonus
        if (password.length() >= 10 && varietyCount >= 4) {
            score += 2;
        }

        return Math.min(score, 10); // Cap at 10
    }

    private String getStrengthLevel(int score) {
        if (score >= 8) return "STRONG";
        if (score >= 5) return "MEDIUM";
        return "WEAK";
    }

    public static class PasswordValidationResult {
        private boolean valid;
        private int strengthScore;
        private String strengthLevel;
        private java.util.List<String> errors = new java.util.ArrayList<>();

        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public int getStrengthScore() { return strengthScore; }
        public void setStrengthScore(int strengthScore) { this.strengthScore = strengthScore; }
        public String getStrengthLevel() { return strengthLevel; }
        public void setStrengthLevel(String strengthLevel) { this.strengthLevel = strengthLevel; }
        public java.util.List<String> getErrors() { return errors; }
        public void setErrors(java.util.List<String> errors) { this.errors = errors; }
    }
}