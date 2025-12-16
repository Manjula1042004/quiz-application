package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("QuizAttempt Entity Tests")
class QuizAttemptEntityTest {

    private QuizAttempt quizAttempt;
    private User user;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quizAttempt = new QuizAttempt();
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        quiz = new Quiz();
        quiz.setId(100L);
        quiz.setTitle("Java Quiz");
    }

    @Test
    @DisplayName("Should create quiz attempt with default values")
    void constructor_DefaultValues() {
        // When
        QuizAttempt newAttempt = new QuizAttempt();

        // Then
        assertNotNull(newAttempt);
        assertNotNull(newAttempt.getAttemptedAt());
        assertNotNull(newAttempt.getAnswers());
        assertTrue(newAttempt.getAnswers().isEmpty());
        assertNull(newAttempt.getCompletedAt());
        assertNull(newAttempt.getScore());
        assertNull(newAttempt.getExpiresAt());
        assertNull(newAttempt.getTotalPoints());
        assertNull(newAttempt.getEarnedPoints());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        quizAttempt.setId(500L);

        // Then
        assertEquals(500L, quizAttempt.getId());
    }

    @Test
    @DisplayName("Should set and get user")
    void setUserAndGetUser() {
        // When
        quizAttempt.setUser(user);

        // Then
        assertEquals(user, quizAttempt.getUser());
        assertEquals(1L, quizAttempt.getUser().getId());
        assertEquals("testuser", quizAttempt.getUser().getUsername());
    }

    @Test
    @DisplayName("Should set and get quiz")
    void setQuizAndGetQuiz() {
        // When
        quizAttempt.setQuiz(quiz);

        // Then
        assertEquals(quiz, quizAttempt.getQuiz());
        assertEquals(100L, quizAttempt.getQuiz().getId());
        assertEquals("Java Quiz", quizAttempt.getQuiz().getTitle());
    }

    @Test
    @DisplayName("Should set and get answers")
    void setAnswersAndGetAnswers() {
        // Given
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 0); // Question ID 1, selected option 0
        answers.put(2L, 1); // Question ID 2, selected option 1
        answers.put(3L, 2); // Question ID 3, selected option 2

        // When
        quizAttempt.setAnswers(answers);

        // Then
        assertEquals(3, quizAttempt.getAnswers().size());
        assertEquals(0, quizAttempt.getAnswers().get(1L));
        assertEquals(1, quizAttempt.getAnswers().get(2L));
        assertEquals(2, quizAttempt.getAnswers().get(3L));
    }

    @Test
    @DisplayName("Should add answer to quiz attempt")
    void addAnswer() {
        // When
        quizAttempt.getAnswers().put(1L, 0);
        quizAttempt.getAnswers().put(2L, 1);

        // Then
        assertEquals(2, quizAttempt.getAnswers().size());
        assertEquals(0, quizAttempt.getAnswers().get(1L));
        assertEquals(1, quizAttempt.getAnswers().get(2L));
    }

    @Test
    @DisplayName("Should set and get score")
    void setScoreAndGetScore() {
        // When
        quizAttempt.setScore(85.5);

        // Then
        assertEquals(85.5, quizAttempt.getScore());
    }

    @Test
    @DisplayName("Should set and get attempted at timestamp")
    void setAttemptedAtAndGetAttemptedAt() {
        // Given
        LocalDateTime attemptedTime = LocalDateTime.now().minusMinutes(30);

        // When
        quizAttempt.setAttemptedAt(attemptedTime);

        // Then
        assertEquals(attemptedTime, quizAttempt.getAttemptedAt());
    }

    @Test
    @DisplayName("Should set and get completed at timestamp")
    void setCompletedAtAndGetCompletedAt() {
        // Given
        LocalDateTime completedTime = LocalDateTime.now();

        // When
        quizAttempt.setCompletedAt(completedTime);

        // Then
        assertEquals(completedTime, quizAttempt.getCompletedAt());
    }

    @Test
    @DisplayName("Should set and get expires at timestamp")
    void setExpiresAtAndGetExpiresAt() {
        // Given
        LocalDateTime expiresTime = LocalDateTime.now().plusHours(1);

        // When
        quizAttempt.setExpiresAt(expiresTime);

        // Then
        assertEquals(expiresTime, quizAttempt.getExpiresAt());
    }

    @Test
    @DisplayName("Should set and get total points")
    void setTotalPointsAndGetTotalPoints() {
        // When
        quizAttempt.setTotalPoints(50);

        // Then
        assertEquals(50, quizAttempt.getTotalPoints());
    }

    @Test
    @DisplayName("Should set and get earned points")
    void setEarnedPointsAndGetEarnedPoints() {
        // When
        quizAttempt.setEarnedPoints(45);

        // Then
        assertEquals(45, quizAttempt.getEarnedPoints());
    }

    @Test
    @DisplayName("Should calculate percentage score from points")
    void calculatePercentageScore() {
        // Given
        quizAttempt.setTotalPoints(50);
        quizAttempt.setEarnedPoints(40);

        // When - Calculate manually
        double percentage = (quizAttempt.getEarnedPoints().doubleValue() /
                quizAttempt.getTotalPoints().doubleValue()) * 100;

        // Then
        assertEquals(80.0, percentage);
    }

    @Test
    @DisplayName("Should check if attempt is completed")
    void isCompleted() {
        // When - Not completed
        quizAttempt.setCompletedAt(null);
        assertNull(quizAttempt.getCompletedAt());

        // When - Completed
        quizAttempt.setCompletedAt(LocalDateTime.now());
        assertNotNull(quizAttempt.getCompletedAt());
    }

    @Test
    @DisplayName("Should check if attempt is expired")
    void isExpired_ExpiredAttempt_ReturnsTrue() {
        // Given
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(10);
        quizAttempt.setExpiresAt(pastTime);

        // When
        boolean expired = quizAttempt.isExpired();

        // Then
        assertTrue(expired);
    }

    @Test
    @DisplayName("Should check if attempt is not expired")
    void isExpired_NotExpiredAttempt_ReturnsFalse() {
        // Given
        LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
        quizAttempt.setExpiresAt(futureTime);

        // When
        boolean expired = quizAttempt.isExpired();

        // Then
        assertFalse(expired);
    }

    @Test
    @DisplayName("Should check if attempt is not expired when expires at is null")
    void isExpired_NullExpiresAt_ReturnsFalse() {
        // Given
        quizAttempt.setExpiresAt(null);

        // When
        boolean expired = quizAttempt.isExpired();

        // Then
        assertFalse(expired);
    }

    @Test
    @DisplayName("Should calculate time taken for quiz")
    void calculateTimeTaken() {
        // Given
        LocalDateTime started = LocalDateTime.now().minusMinutes(30);
        LocalDateTime completed = LocalDateTime.now();
        quizAttempt.setAttemptedAt(started);
        quizAttempt.setCompletedAt(completed);

        // When - Calculate manually (in minutes)
        long minutes = java.time.Duration.between(started, completed).toMinutes();

        // Then
        assertEquals(30, minutes);
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        quizAttempt.setUser(null);
        quizAttempt.setQuiz(null);
        quizAttempt.setAnswers(null);
        quizAttempt.setScore(null);
        quizAttempt.setAttemptedAt(null);
        quizAttempt.setCompletedAt(null);
        quizAttempt.setExpiresAt(null);
        quizAttempt.setTotalPoints(null);
        quizAttempt.setEarnedPoints(null);

        // Then
        assertNull(quizAttempt.getUser());
        assertNull(quizAttempt.getQuiz());
        assertNull(quizAttempt.getAnswers());
        assertNull(quizAttempt.getScore());
        assertNull(quizAttempt.getAttemptedAt());
        assertNull(quizAttempt.getCompletedAt());
        assertNull(quizAttempt.getExpiresAt());
        assertNull(quizAttempt.getTotalPoints());
        assertNull(quizAttempt.getEarnedPoints());
    }

    @Test
    @DisplayName("Should check if attempt is in progress")
    void isInProgress() {
        // When - Started but not completed
        quizAttempt.setAttemptedAt(LocalDateTime.now().minusMinutes(10));
        quizAttempt.setCompletedAt(null);

        // Then
        assertNotNull(quizAttempt.getAttemptedAt());
        assertNull(quizAttempt.getCompletedAt());
        assertTrue(quizAttempt.getAttemptedAt().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should calculate score percentage from earned and total points")
    void calculateScoreFromPoints() {
        // Given
        quizAttempt.setEarnedPoints(30);
        quizAttempt.setTotalPoints(40);

        // When
        double calculatedScore = (quizAttempt.getEarnedPoints().doubleValue() /
                quizAttempt.getTotalPoints().doubleValue()) * 100;
        quizAttempt.setScore(calculatedScore);

        // Then
        assertEquals(75.0, quizAttempt.getScore());
    }

    @Test
    @DisplayName("Should handle perfect score")
    void perfectScore() {
        // Given
        quizAttempt.setEarnedPoints(50);
        quizAttempt.setTotalPoints(50);

        // When
        double calculatedScore = (quizAttempt.getEarnedPoints().doubleValue() /
                quizAttempt.getTotalPoints().doubleValue()) * 100;
        quizAttempt.setScore(calculatedScore);

        // Then
        assertEquals(100.0, quizAttempt.getScore());
    }

    @Test
    @DisplayName("Should handle zero score")
    void zeroScore() {
        // Given
        quizAttempt.setEarnedPoints(0);
        quizAttempt.setTotalPoints(50);

        // When
        double calculatedScore = (quizAttempt.getEarnedPoints().doubleValue() /
                quizAttempt.getTotalPoints().doubleValue()) * 100;
        quizAttempt.setScore(calculatedScore);

        // Then
        assertEquals(0.0, quizAttempt.getScore());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsQuizAttemptInfo() {
        // Given
        quizAttempt.setId(200L);
        quizAttempt.setScore(85.5);
        quizAttempt.setAttemptedAt(LocalDateTime.of(2024, 1, 1, 10, 30));

        // When
        String toString = quizAttempt.toString();

        // Then
        assertTrue(toString.contains("85.5"));
    }

    @Test
    @DisplayName("Should handle quiz attempt with many answers")
    void quizAttemptWithManyAnswers() {
        // Given
        Map<Long, Integer> answers = new HashMap<>();
        for (long i = 1; i <= 50; i++) {
            answers.put(i, (int) (i % 4)); // Answers 0-3
        }

        // When
        quizAttempt.setAnswers(answers);

        // Then
        assertEquals(50, quizAttempt.getAnswers().size());
        assertEquals(0, quizAttempt.getAnswers().get(1L));
        assertEquals(1, quizAttempt.getAnswers().get(2L));
        assertEquals(2, quizAttempt.getAnswers().get(3L));
        assertEquals(3, quizAttempt.getAnswers().get(4L));
    }

    @Test
    @DisplayName("Should create completed quiz attempt")
    void createCompletedQuizAttempt() {
        // Given
        LocalDateTime started = LocalDateTime.now().minusMinutes(45);
        LocalDateTime completed = LocalDateTime.now();

        // When
        quizAttempt.setAttemptedAt(started);
        quizAttempt.setCompletedAt(completed);
        quizAttempt.setScore(92.5);
        quizAttempt.setEarnedPoints(37);
        quizAttempt.setTotalPoints(40);

        // Then
        assertNotNull(quizAttempt.getAttemptedAt());
        assertNotNull(quizAttempt.getCompletedAt());
        assertEquals(92.5, quizAttempt.getScore());
        assertEquals(37, quizAttempt.getEarnedPoints());
        assertEquals(40, quizAttempt.getTotalPoints());
        assertTrue(quizAttempt.getCompletedAt().isAfter(quizAttempt.getAttemptedAt()));
    }

    @Test
    @DisplayName("Should create timed quiz attempt with expiry")
    void createTimedQuizAttempt() {
        // Given
        LocalDateTime started = LocalDateTime.now();
        LocalDateTime expires = started.plusMinutes(60); // 60 minute time limit

        // When
        quizAttempt.setAttemptedAt(started);
        quizAttempt.setExpiresAt(expires);

        // Then
        assertNotNull(quizAttempt.getAttemptedAt());
        assertNotNull(quizAttempt.getExpiresAt());
        assertTrue(quizAttempt.getExpiresAt().isAfter(quizAttempt.getAttemptedAt()));
        assertFalse(quizAttempt.isExpired());
    }
}