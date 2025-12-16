package com.quizapp.repository;

import com.quizapp.entity.DifficultyLevel;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("QuizRepository Tests")
class QuizRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        // First save user to get an ID
        testUser = new User();
        testUser.setUsername("quizcreator");
        testUser.setEmail("creator@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.ADMIN);
        testUser.setEnabled(true);
        testUser = userRepository.save(testUser); // Use repository instead of entityManager

        // Then create quiz with the saved user
        testQuiz = new Quiz();
        testQuiz.setTitle("Java Basics Quiz");
        testQuiz.setDescription("Test your Java knowledge");
        testQuiz.setTimeLimit(30);
        testQuiz.setCreatedBy(testUser); // Use the persisted user
        testQuiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        testQuiz.setIsPublic(true);
        testQuiz.setEnabled(true);
        testQuiz.setCreatedAt(LocalDateTime.now());
        testQuiz = quizRepository.save(testQuiz); // Use repository instead of entityManager
    }

    @Test
    @DisplayName("Should find all quizzes")
    void findAll_ReturnsAllQuizzes() {
        // When
        List<Quiz> quizzes = quizRepository.findAll();

        // Then
        assertEquals(1, quizzes.size());
        assertEquals("Java Basics Quiz", quizzes.get(0).getTitle());
    }

    @Test
    @DisplayName("Should find quiz by ID")
    void findById_QuizExists_ReturnsQuiz() {
        // When
        Quiz foundQuiz = quizRepository.findById(testQuiz.getId()).orElse(null);

        // Then
        assertNotNull(foundQuiz);
        assertEquals("Java Basics Quiz", foundQuiz.getTitle());
        assertEquals(DifficultyLevel.MEDIUM, foundQuiz.getDifficultyLevel());
    }

    // ... rest of your tests
}