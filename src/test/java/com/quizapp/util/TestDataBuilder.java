package com.quizapp.util;

import com.quizapp.entity.*;
import com.quizapp.dto.UserRegistrationDto;
import com.quizapp.dto.AuthRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestDataBuilder {

    public static User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setRole(Role.PARTICIPANT);
        return user;
    }

    public static User createAdminUser() {
        User user = createTestUser();
        user.setUsername("admin");
        user.setRole(Role.ADMIN);
        return user;
    }

    public static Quiz createTestQuiz(User createdBy) {
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setTimeLimit(30);
        quiz.setCreatedBy(createdBy);
        quiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        quiz.setIsPublic(true);
        quiz.setEnabled(true);
        quiz.setIsTemplate(false);
        quiz.setCreatedAt(LocalDateTime.now());
        return quiz;
    }

    public static Question createTestQuestion(Quiz quiz) {
        Question question = new Question();
        question.setId(1L);
        question.setQuestionText("What is 2+2?");
        question.setOptions(Arrays.asList("3", "4", "5", "6"));
        question.setCorrectAnswerIndex(1);
        question.setDifficultyLevel(DifficultyLevel.EASY);
        question.setPoints(10);
        question.setQuiz(quiz);
        return question;
    }

    public static QuizAttempt createTestQuizAttempt(User user, Quiz quiz) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setScore(85.0);
        // Remove the setStartedAt method call as it doesn't exist in your entity
        return attempt;
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Mathematics");
        category.setDescription("Math related quizzes");
        category.setColor("#FF0000");
        return category;
    }

    public static UserRegistrationDto createUserRegistrationDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("newuser");
        dto.setEmail("newuser@example.com");
        dto.setPassword("Password123!");
        dto.setConfirmPassword("Password123!");
        dto.setRole("PARTICIPANT");
        return dto;
    }

    public static AuthRequest createAuthRequest() {
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("Password123!");
        return request;
    }
}