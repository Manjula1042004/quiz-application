package com.quizapp.repository;

import com.quizapp.entity.DifficultyLevel;
import com.quizapp.entity.Question;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("QuestionRepository Tests")
class QuestionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    private Quiz testQuiz;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        // Create test user
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.ADMIN);
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

        // Create test question
        testQuestion = new Question();
        testQuestion.setQuestionText("What is 2+2?");
        testQuestion.setOptions(Arrays.asList("3", "4", "5", "6"));
        testQuestion.setCorrectAnswerIndex(1);
        testQuestion.setDifficultyLevel(DifficultyLevel.EASY);
        testQuestion.setPoints(1);
        testQuestion.setQuiz(testQuiz);
        entityManager.persist(testQuestion);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find questions by quiz ID")
    void findByQuizId_ReturnsQuestions() {
        // When
        List<Question> questions = questionRepository.findByQuizId(testQuiz.getId());

        // Then
        assertEquals(1, questions.size());
        assertEquals("What is 2+2?", questions.get(0).getQuestionText());
    }

    @Test
    @DisplayName("Should find questions by difficulty level")
    void findByDifficultyLevel_ReturnsQuestions() {
        // When
        List<Question> questions = questionRepository.findByDifficultyLevel(DifficultyLevel.EASY);

        // Then
        assertEquals(1, questions.size());
        assertEquals(DifficultyLevel.EASY, questions.get(0).getDifficultyLevel());
    }

    @Test
    @DisplayName("Should find template questions")
    void findByIsTemplateTrue_ReturnsTemplateQuestions() {
        // Given - create a template question
        Question templateQuestion = new Question();
        templateQuestion.setQuestionText("Template Question");
        templateQuestion.setOptions(Arrays.asList("A", "B", "C", "D"));
        templateQuestion.setCorrectAnswerIndex(0);
        templateQuestion.setDifficultyLevel(DifficultyLevel.MEDIUM);
        templateQuestion.setPoints(2);
        templateQuestion.setIsTemplate(true);
        entityManager.persist(templateQuestion);
        entityManager.flush();

        // When
        List<Question> templateQuestions = questionRepository.findByIsTemplateTrue();

        // Then
        assertTrue(templateQuestions.size() >= 1);
        assertTrue(templateQuestions.stream().anyMatch(q -> q.getIsTemplate()));
    }

    @Test
    @DisplayName("Should find template questions from question bank")
    void findTemplateQuestions_ReturnsTemplateQuestions() {
        // Given - create a template question without quiz
        Question templateQuestion = new Question();
        templateQuestion.setQuestionText("Question Bank Template");
        templateQuestion.setOptions(Arrays.asList("X", "Y", "Z"));
        templateQuestion.setCorrectAnswerIndex(1);
        templateQuestion.setDifficultyLevel(DifficultyLevel.HARD);
        templateQuestion.setPoints(3);
        templateQuestion.setIsTemplate(true);
        entityManager.persist(templateQuestion);
        entityManager.flush();

        // When
        List<Question> templateQuestions = questionRepository.findTemplateQuestions();

        // Then
        assertEquals(1, templateQuestions.size());
        assertEquals("Question Bank Template", templateQuestions.get(0).getQuestionText());
        assertTrue(templateQuestions.get(0).getIsTemplate());
        assertNull(templateQuestions.get(0).getQuiz());
    }

    @Test
    @DisplayName("Should search questions by text")
    void searchByQuestionText_ReturnsMatchingQuestions() {
        // When
        List<Question> questions = questionRepository.searchByQuestionText("2+2");

        // Then
        assertEquals(1, questions.size());
        assertEquals("What is 2+2?", questions.get(0).getQuestionText());
    }

    @Test
    @DisplayName("Should count questions by quiz ID")
    void countByQuizId_ReturnsCorrectCount() {
        // When
        Long count = questionRepository.countByQuizId(testQuiz.getId());

        // Then
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Should find question bank questions")
    void findQuestionBankQuestions_ReturnsQuestionsWithoutQuiz() {
        // Given - create a question without quiz
        Question questionBankQuestion = new Question();
        questionBankQuestion.setQuestionText("Question Bank Item");
        questionBankQuestion.setOptions(Arrays.asList("True", "False"));
        questionBankQuestion.setCorrectAnswerIndex(0);
        questionBankQuestion.setDifficultyLevel(DifficultyLevel.MEDIUM);
        questionBankQuestion.setPoints(1);
        entityManager.persist(questionBankQuestion);
        entityManager.flush();

        // When
        List<Question> questionBankQuestions = questionRepository.findQuestionBankQuestions();

        // Then
        assertTrue(questionBankQuestions.size() >= 1);
        assertTrue(questionBankQuestions.stream().anyMatch(q -> q.getQuiz() == null));
    }

    @Test
    @DisplayName("Should save new question")
    void save_NewQuestion_SuccessfullySaved() {
        // Given
        Question newQuestion = new Question();
        newQuestion.setQuestionText("New Question?");
        newQuestion.setOptions(Arrays.asList("Option 1", "Option 2", "Option 3", "Option 4"));
        newQuestion.setCorrectAnswerIndex(2);
        newQuestion.setDifficultyLevel(DifficultyLevel.MEDIUM);
        newQuestion.setPoints(2);
        newQuestion.setQuiz(testQuiz);

        // When
        Question savedQuestion = questionRepository.save(newQuestion);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedQuestion.getId());
        Question foundQuestion = entityManager.find(Question.class, savedQuestion.getId());
        assertEquals("New Question?", foundQuestion.getQuestionText());
        assertEquals(4, foundQuestion.getOptions().size());
        assertEquals(2, foundQuestion.getCorrectAnswerIndex());
    }
}