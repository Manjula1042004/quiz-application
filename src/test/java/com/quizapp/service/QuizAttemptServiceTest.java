package com.quizapp.service;

import com.quizapp.entity.*;
import com.quizapp.repository.QuizAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuizService quizService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private QuizAttemptService quizAttemptService;

    private User user;
    private Quiz quiz;
    private Question question;
    private QuizAttempt quizAttempt;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setTimeLimit(30);

        question = new Question();
        question.setId(1L);
        question.setQuestionText("What is 2+2?");
        question.setOptions(Arrays.asList("3", "4", "5", "6"));
        question.setCorrectAnswerIndex(1);
        question.setPoints(10);

        quiz.setQuestions(Arrays.asList(question));

        quizAttempt = new QuizAttempt();
        quizAttempt.setId(1L);
        quizAttempt.setUser(user);
        quizAttempt.setQuiz(quiz);
        quizAttempt.setAttemptedAt(LocalDateTime.now());
    }

    @Test
    void startQuizAttempt_Success() {
        // Arrange
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        // Act
        QuizAttempt attempt = quizAttemptService.startQuizAttempt(user, quiz);

        // Assert
        assertNotNull(attempt);
        assertEquals(user, attempt.getUser());
        assertEquals(quiz, attempt.getQuiz());
        assertNotNull(attempt.getAttemptedAt());
        assertNotNull(attempt.getExpiresAt()); // Should be set with time limit
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
    }

    @Test
    void startQuizAttempt_NoTimeLimit() {
        // Arrange
        quiz.setTimeLimit(null);
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        // Act
        QuizAttempt attempt = quizAttemptService.startQuizAttempt(user, quiz);

        // Assert
        assertNotNull(attempt);
        assertNull(attempt.getExpiresAt()); // No expiry without time limit
    }

    @Test
    void submitQuiz_Success() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 1); // Correct answer

        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);
        doNothing().when(emailService).sendQuizResultEmail(any(QuizAttempt.class));

        // Act
        QuizAttempt submitted = quizAttemptService.submitQuiz(1L, answers);

        // Assert
        assertNotNull(submitted);
        assertNotNull(submitted.getCompletedAt());
        assertEquals(100.0, submitted.getScore()); // 1 correct out of 1
        assertEquals(10, submitted.getEarnedPoints()); // All points earned
        assertEquals(10, submitted.getTotalPoints()); // Total points
        verify(quizAttemptRepository, times(1)).save(quizAttempt);
        verify(emailService, times(1)).sendQuizResultEmail(quizAttempt);
    }

    @Test
    void submitQuiz_AttemptNotFound() {
        // Arrange
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            quizAttemptService.submitQuiz(1L, new HashMap<>());
        });

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void submitQuiz_ExpiredAttempt() {
        // Arrange
        quizAttempt.setExpiresAt(LocalDateTime.now().minusMinutes(1)); // Expired 1 minute ago
        Map<Long, Integer> answers = new HashMap<>();

        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        // Act
        QuizAttempt submitted = quizAttemptService.submitQuiz(1L, answers);

        // Assert
        assertNotNull(submitted);
        assertTrue(submitted.isExpired());
    }

    @Test
    void submitQuiz_InvalidAnswerRemoved() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 5); // Invalid index (only 4 options)
        answers.put(999L, 0); // Non-existent question

        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        // Act
        QuizAttempt submitted = quizAttemptService.submitQuiz(1L, answers);

        // Assert
        assertNotNull(submitted);
        assertTrue(submitted.getAnswers().isEmpty()); // All invalid answers removed
        assertEquals(0.0, submitted.getScore()); // No valid answers
    }

    @Test
    void getUserAttempts() {
        // Arrange
        List<QuizAttempt> attempts = Arrays.asList(quizAttempt);
        when(quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(1L)).thenReturn(attempts);

        // Act
        List<QuizAttempt> result = quizAttemptService.getUserAttempts(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(quizAttempt, result.get(0));
    }

    @Test
    void getAttemptById_Found() {
        // Arrange
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));

        // Act
        Optional<QuizAttempt> result = quizAttemptService.getAttemptById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(quizAttempt, result.get());
    }

    @Test
    void getAttemptById_NotFound() {
        // Arrange
        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<QuizAttempt> result = quizAttemptService.getAttemptById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    // REMOVE tests for private calculateScore method
    // Instead, test the score calculation through submitQuiz method

    @Test
    void submitQuiz_ScoreCalculation_AllCorrect() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 1); // Correct answer

        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        // Act
        QuizAttempt submitted = quizAttemptService.submitQuiz(1L, answers);

        // Assert
        assertEquals(100.0, submitted.getScore()); // All correct
        assertEquals(10, submitted.getEarnedPoints()); // All points earned
    }

    @Test
    void submitQuiz_ScoreCalculation_PartialCorrect() {
        // Arrange
        // Add second question
        Question question2 = new Question();
        question2.setId(2L);
        question2.setQuestionText("What is 3+3?");
        question2.setOptions(Arrays.asList("5", "6", "7", "8"));
        question2.setCorrectAnswerIndex(1);
        question2.setPoints(5);

        quiz.getQuestions().add(question2);

        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 1); // Correct (10 points)
        answers.put(2L, 0); // Incorrect (0 points)

        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        // Act
        QuizAttempt submitted = quizAttemptService.submitQuiz(1L, answers);

        // Assert
        assertEquals(66.67, submitted.getScore(), 0.01); // 10/15 points = 66.67%
        assertEquals(10, submitted.getEarnedPoints()); // Only first question correct
        assertEquals(15, submitted.getTotalPoints()); // Total points
    }

    @Test
    void submitQuiz_ScoreCalculation_NoAnswers() {
        // Arrange
        Map<Long, Integer> answers = new HashMap<>(); // Empty answers

        when(quizAttemptRepository.findById(1L)).thenReturn(Optional.of(quizAttempt));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(quizAttempt);

        // Act
        QuizAttempt submitted = quizAttemptService.submitQuiz(1L, answers);

        // Assert
        assertEquals(0.0, submitted.getScore()); // No answers
        assertEquals(0, submitted.getEarnedPoints()); // No points earned
    }
}