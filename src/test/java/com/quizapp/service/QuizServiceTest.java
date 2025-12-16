package com.quizapp.service;

import com.quizapp.entity.*;
import com.quizapp.repository.QuizRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private QuizService quizService;

    private User user;
    private Quiz quiz;
    private Question question;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setTimeLimit(30);
        quiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        quiz.setIsPublic(true);
        quiz.setEnabled(true);
        quiz.setIsTemplate(false);
        quiz.setCreatedBy(user);

        question = new Question();
        question.setId(1L);
        question.setQuestionText("Test Question");
        question.setQuiz(quiz);
    }

    @Test
    void createQuiz_Success() {
        // Arrange
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        // Act
        Quiz created = quizService.createQuiz(quiz, user);

        // Assert
        assertNotNull(created);
        assertEquals(user, created.getCreatedBy());
        assertEquals("Test Quiz", created.getTitle());
        verify(quizRepository, times(1)).save(quiz);
    }

    @Test
    void getAllQuizzes() {
        // Arrange
        List<Quiz> quizzes = Arrays.asList(quiz);
        when(quizRepository.findAll()).thenReturn(quizzes);

        // Act
        List<Quiz> result = quizService.getAllQuizzes();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Quiz", result.get(0).getTitle());
    }

    @Test
    void getQuizzesByUser() {
        // Arrange
        List<Quiz> quizzes = Arrays.asList(quiz);
        when(quizRepository.findByCreatedByUsername("testuser")).thenReturn(quizzes);

        // Act
        List<Quiz> result = quizService.getQuizzesByUser("testuser");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Quiz", result.get(0).getTitle());
    }

    @Test
    void getQuizById_Found() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));

        // Act
        Optional<Quiz> result = quizService.getQuizById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Quiz", result.get().getTitle());
    }

    @Test
    void getQuizById_NotFound() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Quiz> result = quizService.getQuizById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void updateQuiz_Success() {
        // Arrange
        Quiz updatedDetails = new Quiz();
        updatedDetails.setTitle("Updated Quiz");
        updatedDetails.setDescription("Updated Description");
        updatedDetails.setTimeLimit(45);
        updatedDetails.setDifficultyLevel(DifficultyLevel.HARD);
        updatedDetails.setIsPublic(false);
        updatedDetails.setEnabled(false);

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        // Act
        Quiz updated = quizService.updateQuiz(1L, updatedDetails);

        // Assert
        assertNotNull(updated);
        assertEquals("Updated Quiz", quiz.getTitle());
        assertEquals("Updated Description", quiz.getDescription());
        assertEquals(45, quiz.getTimeLimit());
        assertEquals(DifficultyLevel.HARD, quiz.getDifficultyLevel());
        assertFalse(quiz.getIsPublic());
        assertFalse(quiz.getEnabled());
        verify(quizRepository, times(1)).save(quiz);
    }

    @Test
    void updateQuiz_NotFound() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            quizService.updateQuiz(1L, new Quiz());
        });
    }

    @Test
    void deleteQuiz_Success() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        doNothing().when(quizRepository).delete(quiz);

        // Act
        quizService.deleteQuiz(1L);

        // Assert
        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    void deleteQuiz_NotFound() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            quizService.deleteQuiz(1L);
        });
    }

    @Test
    void getQuizTemplates() {
        // Arrange
        quiz.setIsTemplate(true);
        List<Quiz> templates = Arrays.asList(quiz);
        when(quizRepository.findByIsTemplateTrue()).thenReturn(templates);

        // Act
        List<Quiz> result = quizService.getQuizTemplates();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsTemplate());
    }

    @Test
    void createQuizFromTemplate_Success() {
        // Arrange
        Quiz template = new Quiz();
        template.setId(2L);
        template.setTitle("Template Quiz");
        template.setDescription("Template Description");
        template.setTimeLimit(20);
        template.setDifficultyLevel(DifficultyLevel.EASY);
        template.setIsPublic(false);
        template.setEnabled(true);
        template.setIsTemplate(true);
        template.setQuestions(Arrays.asList(question));

        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(questionService.createQuestion(any(Question.class))).thenReturn(question);

        // Act
        Quiz created = quizService.createQuizFromTemplate(template, user, "New Quiz");

        // Assert
        assertNotNull(created);
        assertEquals("New Quiz", created.getTitle());
        assertEquals(user, created.getCreatedBy());
        assertFalse(created.getIsTemplate());
        verify(quizRepository, times(1)).save(any(Quiz.class));
        verify(questionService, times(1)).createQuestion(any(Question.class));
    }

    @Test
    void saveAsTemplate_Success() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(questionService.createQuestion(any(Question.class))).thenReturn(question);

        // Act
        quizService.saveAsTemplate(1L, "New Template");

        // Assert
        verify(quizRepository, times(2)).save(any(Quiz.class)); // Once for quiz, once for template
        verify(questionService, times(1)).createQuestion(any(Question.class));
    }

    @Test
    void addQuestionToQuiz_Success() {
        // Arrange
        when(questionService.createQuestion(any(Question.class))).thenReturn(question);

        // Act
        quizService.addQuestionToQuiz(quiz, question);

        // Assert
        verify(questionService, times(1)).createQuestion(any(Question.class));
    }

    @Test
    void getPublicQuizzes_AllPublic() {
        // Arrange
        List<Quiz> allQuizzes = Arrays.asList(quiz);
        when(quizRepository.findAll()).thenReturn(allQuizzes);

        // Act
        List<Quiz> result = quizService.getPublicQuizzes();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsPublic());
        assertTrue(result.get(0).getEnabled());
        assertFalse(result.get(0).getIsTemplate());
    }

    @Test
    void getPublicQuizzes_Filtered() {
        // Arrange
        Quiz privateQuiz = new Quiz();
        privateQuiz.setId(2L);
        privateQuiz.setTitle("Private Quiz");
        privateQuiz.setIsPublic(false);
        privateQuiz.setEnabled(true);
        privateQuiz.setIsTemplate(false);

        Quiz disabledQuiz = new Quiz();
        disabledQuiz.setId(3L);
        disabledQuiz.setTitle("Disabled Quiz");
        disabledQuiz.setIsPublic(true);
        disabledQuiz.setEnabled(false);
        disabledQuiz.setIsTemplate(false);

        Quiz templateQuiz = new Quiz();
        templateQuiz.setId(4L);
        templateQuiz.setTitle("Template Quiz");
        templateQuiz.setIsPublic(true);
        templateQuiz.setEnabled(true);
        templateQuiz.setIsTemplate(true);

        List<Quiz> allQuizzes = Arrays.asList(quiz, privateQuiz, disabledQuiz, templateQuiz);
        when(quizRepository.findAll()).thenReturn(allQuizzes);

        // Act
        List<Quiz> result = quizService.getPublicQuizzes();

        // Assert
        assertEquals(1, result.size()); // Only the public, enabled, non-template quiz
        assertEquals("Test Quiz", result.get(0).getTitle());
    }

    @Test
    void getPublicQuizzes_NullValues() {
        // Arrange
        quiz.setIsPublic(null); // Null values
        quiz.setEnabled(null);
        quiz.setIsTemplate(null);

        List<Quiz> allQuizzes = Arrays.asList(quiz);
        when(quizRepository.findAll()).thenReturn(allQuizzes);

        // Act
        List<Quiz> result = quizService.getPublicQuizzes();

        // Assert
        assertEquals(0, result.size()); // Null values should be filtered out
    }
}