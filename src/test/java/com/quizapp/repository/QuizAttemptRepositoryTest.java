package com.quizapp.repository;

import com.quizapp.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("QuizAttemptRepository Tests")
class QuizAttemptRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    private User testUser;
    private Quiz testQuiz;
    private QuizAttempt testAttempt;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("testparticipant");
        testUser.setEmail("participant@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.PARTICIPANT);
        testUser.setEnabled(true);
        entityManager.persist(testUser);

        // Create test quiz
        testQuiz = new Quiz();
        testQuiz.setTitle("Test Quiz");
        testQuiz.setDescription("Test Description");
        testQuiz.setTimeLimit(30);
        testQuiz.setCreatedBy(testUser);
        testQuiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        testQuiz.setIsPublic(true);
        testQuiz.setEnabled(true);
        entityManager.persist(testQuiz);

        // Create test questions
        Question question1 = new Question();
        question1.setQuestionText("Question 1");
        question1.setOptions(Arrays.asList("A", "B", "C", "D"));
        question1.setCorrectAnswerIndex(1);
        question1.setDifficultyLevel(DifficultyLevel.EASY);
        question1.setPoints(1);
        question1.setQuiz(testQuiz);
        entityManager.persist(question1);

        Question question2 = new Question();
        question2.setQuestionText("Question 2");
        question2.setOptions(Arrays.asList("True", "False"));
        question2.setCorrectAnswerIndex(0);
        question2.setDifficultyLevel(DifficultyLevel.MEDIUM);
        question2.setPoints(2);
        question2.setQuiz(testQuiz);
        entityManager.persist(question2);

        // Create test quiz attempt
        testAttempt = new QuizAttempt();
        testAttempt.setUser(testUser);
        testAttempt.setQuiz(testQuiz);

        Map<Long, Integer> answers = new HashMap<>();
        answers.put(question1.getId(), 1); // Correct answer
        answers.put(question2.getId(), 1); // Wrong answer
        testAttempt.setAnswers(answers);

        testAttempt.setScore(50.0);
        testAttempt.setAttemptedAt(LocalDateTime.now().minusHours(1));
        testAttempt.setCompletedAt(LocalDateTime.now());
        testAttempt.setEarnedPoints(1); // 1 correct out of 2 questions
        testAttempt.setTotalPoints(2);

        entityManager.persist(testAttempt);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find quiz attempts by user ID")
    void findByUserId_ReturnsAttempts() {
        // When
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(testUser.getId());

        // Then
        assertEquals(1, attempts.size());
        assertEquals(testUser.getId(), attempts.get(0).getUser().getId());
        assertEquals(50.0, attempts.get(0).getScore());
    }

    @Test
    @DisplayName("Should find quiz attempts by quiz ID")
    void findByQuizId_ReturnsAttempts() {
        // When
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizId(testQuiz.getId());

        // Then
        assertEquals(1, attempts.size());
        assertEquals(testQuiz.getId(), attempts.get(0).getQuiz().getId());
    }

    @Test
    @DisplayName("Should find quiz attempts by user ID ordered by attempted date")
    void findByUserIdOrderByAttemptedAtDesc_ReturnsOrderedAttempts() {
        // Given - create another attempt
        QuizAttempt anotherAttempt = new QuizAttempt();
        anotherAttempt.setUser(testUser);
        anotherAttempt.setQuiz(testQuiz);
        anotherAttempt.setScore(75.0);
        anotherAttempt.setAttemptedAt(LocalDateTime.now());
        anotherAttempt.setCompletedAt(LocalDateTime.now());
        entityManager.persist(anotherAttempt);
        entityManager.flush();

        // When
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(testUser.getId());

        // Then
        assertEquals(2, attempts.size());
        assertTrue(attempts.get(0).getAttemptedAt().isAfter(attempts.get(1).getAttemptedAt()));
    }

    @Test
    @DisplayName("Should find recent attempts")
    void findRecentAttempts_ReturnsRecentAttempts() {
        // When
        List<QuizAttempt> attempts = quizAttemptRepository.findRecentAttempts(PageRequest.of(0, 10));

        // Then
        assertFalse(attempts.isEmpty());
        assertEquals(testAttempt.getId(), attempts.get(0).getId());
    }

    @Test
    @DisplayName("Should count attempts by user ID")
    void countByUserId_ReturnsCorrectCount() {
        // When
        Long count = quizAttemptRepository.countByUserId(testUser.getId());

        // Then
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Should count attempts by quiz ID")
    void countByQuizId_ReturnsCorrectCount() {
        // When
        Long count = quizAttemptRepository.countByQuizId(testQuiz.getId());

        // Then
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Should calculate average score by quiz ID")
    void findAverageScoreByQuizId_ReturnsAverage() {
        // Given - create another attempt with different score
        QuizAttempt anotherAttempt = new QuizAttempt();
        anotherAttempt.setUser(testUser);
        anotherAttempt.setQuiz(testQuiz);
        anotherAttempt.setScore(100.0);
        anotherAttempt.setAttemptedAt(LocalDateTime.now());
        anotherAttempt.setCompletedAt(LocalDateTime.now());
        entityManager.persist(anotherAttempt);
        entityManager.flush();

        // When
        Double averageScore = quizAttemptRepository.findAverageScoreByQuizId(testQuiz.getId());

        // Then
        assertNotNull(averageScore);
        assertEquals(75.0, averageScore); // (50 + 100) / 2 = 75
    }

    @Test
    @DisplayName("Should find attempts by user ID and quiz ID")
    void findByUserIdAndQuizIdOrderByAttemptedAtDesc_ReturnsAttempts() {
        // When
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdAndQuizIdOrderByAttemptedAtDesc(
                testUser.getId(), testQuiz.getId());

        // Then
        assertEquals(1, attempts.size());
        assertEquals(testUser.getId(), attempts.get(0).getUser().getId());
        assertEquals(testQuiz.getId(), attempts.get(0).getQuiz().getId());
    }

    @Test
    @DisplayName("Should save new quiz attempt")
    void save_NewQuizAttempt_SuccessfullySaved() {
        // Given
        QuizAttempt newAttempt = new QuizAttempt();
        newAttempt.setUser(testUser);
        newAttempt.setQuiz(testQuiz);
        newAttempt.setScore(85.5);
        newAttempt.setAttemptedAt(LocalDateTime.now());
        newAttempt.setCompletedAt(LocalDateTime.now());

        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 0);
        newAttempt.setAnswers(answers);

        newAttempt.setEarnedPoints(3);
        newAttempt.setTotalPoints(4);

        // When
        QuizAttempt savedAttempt = quizAttemptRepository.save(newAttempt);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedAttempt.getId());
        QuizAttempt foundAttempt = entityManager.find(QuizAttempt.class, savedAttempt.getId());
        assertEquals(85.5, foundAttempt.getScore());
        assertEquals(3, foundAttempt.getEarnedPoints());
        assertEquals(4, foundAttempt.getTotalPoints());
    }

    @Test
    @DisplayName("Should check if attempt is expired")
    void isExpired_ExpiredAttempt_ReturnsTrue() {
        // Given
        QuizAttempt expiredAttempt = new QuizAttempt();
        expiredAttempt.setUser(testUser);
        expiredAttempt.setQuiz(testQuiz);
        expiredAttempt.setExpiresAt(LocalDateTime.now().minusMinutes(5)); // Expired 5 minutes ago
        entityManager.persist(expiredAttempt);
        entityManager.flush();

        // When & Then
        assertTrue(expiredAttempt.isExpired());
    }

    @Test
    @DisplayName("Should check if attempt is not expired")
    void isExpired_NotExpiredAttempt_ReturnsFalse() {
        // Given
        QuizAttempt validAttempt = new QuizAttempt();
        validAttempt.setUser(testUser);
        validAttempt.setQuiz(testQuiz);
        validAttempt.setExpiresAt(LocalDateTime.now().plusHours(1)); // Valid for 1 more hour
        entityManager.persist(validAttempt);
        entityManager.flush();

        // When & Then
        assertFalse(validAttempt.isExpired());
    }
}