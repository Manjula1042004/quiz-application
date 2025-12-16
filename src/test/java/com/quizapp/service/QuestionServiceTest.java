package com.quizapp.service;

import com.quizapp.entity.DifficultyLevel;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.Tag;
import com.quizapp.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TagService tagService;

    @InjectMocks
    private QuestionService questionService;

    private Question question;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");

        question = new Question();
        question.setId(1L);
        question.setQuestionText("What is 2+2?");
        question.setOptions(Arrays.asList("3", "4", "5", "6"));
        question.setCorrectAnswerIndex(1);
        question.setDifficultyLevel(DifficultyLevel.EASY);
        question.setExplanation("Basic math");
        question.setPoints(10);
        question.setQuiz(quiz);
    }

    @Test
    void createQuestion_Success() {
        // Arrange
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question created = questionService.createQuestion(question);

        // Assert
        assertNotNull(created);
        assertEquals("What is 2+2?", created.getQuestionText());
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void createQuestionWithTags_Success() {
        // Arrange
        List<String> tagNames = Arrays.asList("math", "basic");
        List<Tag> tags = Arrays.asList(new Tag("math"), new Tag("basic"));

        when(tagService.createOrGetTags(tagNames)).thenReturn(tags);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question created = questionService.createQuestionWithTags(question, tagNames);

        // Assert
        assertNotNull(created);
        assertEquals(2, created.getTags().size());
        verify(tagService, times(1)).createOrGetTags(tagNames);
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void createQuestionWithTags_EmptyTags() {
        // Arrange
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question created = questionService.createQuestionWithTags(question, new ArrayList<>());

        // Assert
        assertNotNull(created);
        assertTrue(created.getTags().isEmpty());
        verify(tagService, never()).createOrGetTags(any());
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void getQuestionsByQuizId() {
        // Arrange
        List<Question> questions = Arrays.asList(question);
        when(questionRepository.findByQuizId(1L)).thenReturn(questions);

        // Act
        List<Question> result = questionService.getQuestionsByQuizId(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("What is 2+2?", result.get(0).getQuestionText());
    }

    @Test
    void getQuestionById_Found() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        // Act
        Optional<Question> result = questionService.getQuestionById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("What is 2+2?", result.get().getQuestionText());
    }

    @Test
    void getQuestionById_NotFound() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Question> result = questionService.getQuestionById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void updateQuestion_Success() {
        // Arrange
        Question updatedDetails = new Question();
        updatedDetails.setQuestionText("Updated question");
        updatedDetails.setOptions(Arrays.asList("A", "B", "C"));
        updatedDetails.setCorrectAnswerIndex(0);
        updatedDetails.setDifficultyLevel(DifficultyLevel.HARD);
        updatedDetails.setExplanation("Updated explanation");
        updatedDetails.setPoints(5);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question updated = questionService.updateQuestion(1L, updatedDetails);

        // Assert
        assertNotNull(updated);
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void updateQuestion_NotFound() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            questionService.updateQuestion(1L, new Question());
        });
    }

    @Test
    void updateQuestionTags_Success() {
        // Arrange
        List<String> tagNames = Arrays.asList("updated", "tags");
        List<Tag> tags = Arrays.asList(new Tag("updated"), new Tag("tags"));

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(tagService.createOrGetTags(tagNames)).thenReturn(tags);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question updated = questionService.updateQuestionTags(1L, tagNames);

        // Assert
        assertNotNull(updated);
        verify(tagService, times(1)).createOrGetTags(tagNames);
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void deleteQuestion_Success() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        doNothing().when(questionRepository).delete(question);

        // Act
        questionService.deleteQuestion(1L);

        // Assert
        verify(questionRepository, times(1)).delete(question);
    }

    @Test
    void deleteQuestion_NotFound() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            questionService.deleteQuestion(1L);
        });
    }

    @Test
    void getQuestionsByDifficulty() {
        // Arrange
        List<Question> questions = Arrays.asList(question);
        when(questionRepository.findByDifficultyLevel(DifficultyLevel.EASY)).thenReturn(questions);

        // Act
        List<Question> result = questionService.getQuestionsByDifficulty(DifficultyLevel.EASY);

        // Assert
        assertEquals(1, result.size());
        assertEquals(DifficultyLevel.EASY, result.get(0).getDifficultyLevel());
    }

    @Test
    void getTemplateQuestions() {
        // Arrange
        List<Question> questions = Arrays.asList(question);
        when(questionRepository.findTemplateQuestions()).thenReturn(questions);

        // Act
        List<Question> result = questionService.getTemplateQuestions();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getQuestionBankQuestions() {
        // Arrange
        List<Question> questions = Arrays.asList(question);
        when(questionRepository.findQuestionBankQuestions()).thenReturn(questions);

        // Act
        List<Question> result = questionService.getQuestionBankQuestions();

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void searchQuestions() {
        // Arrange
        List<Question> questions = Arrays.asList(question);
        when(questionRepository.searchByQuestionText("2+2")).thenReturn(questions);

        // Act
        List<Question> result = questionService.searchQuestions("2+2");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getQuestionText().contains("2+2"));
    }

    @Test
    void getQuestionsByTag() {
        // Arrange
        List<Question> questions = Arrays.asList(question);
        when(questionRepository.findByTagName("math")).thenReturn(questions);

        // Act
        List<Question> result = questionService.getQuestionsByTag("math");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void convertToTemplate() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question template = questionService.convertToTemplate(1L);

        // Assert
        assertTrue(template.getIsTemplate());
        assertNull(template.getQuiz());
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    void addToQuizFromTemplate() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // Act
        Question newQuestion = questionService.addToQuizFromTemplate(1L, quiz);

        // Assert
        assertNotNull(newQuestion);
        assertEquals(quiz, newQuestion.getQuiz());
        assertFalse(newQuestion.getIsTemplate());
        verify(questionRepository, times(1)).save(any(Question.class));
    }
}