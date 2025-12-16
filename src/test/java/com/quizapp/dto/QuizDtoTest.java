package com.quizapp.dto;

import com.quizapp.entity.DifficultyLevel;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QuizDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultConstructor() {
        QuizDto quiz = new QuizDto();

        assertNull(quiz.getId());
        assertNull(quiz.getTitle());
        assertNull(quiz.getDescription());
        assertNull(quiz.getTimeLimit());
        assertNull(quiz.getCategoryId());
        assertFalse(quiz.getIsTemplate());
        assertEquals(DifficultyLevel.MEDIUM, quiz.getDifficultyLevel());
        assertTrue(quiz.getIsPublic());
        assertTrue(quiz.getEnabled());
        assertNotNull(quiz.getQuestions());
        assertTrue(quiz.getQuestions().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        QuizDto quiz = new QuizDto();
        QuestionDto question1 = new QuestionDto();
        question1.setQuestionText("What is 2+2?");

        List<QuestionDto> questions = Arrays.asList(question1);

        quiz.setId(1L);
        quiz.setTitle("Math Quiz");
        quiz.setDescription("Basic math questions");
        quiz.setTimeLimit(30);
        quiz.setCategoryId(1L);
        quiz.setIsTemplate(true);
        quiz.setDifficultyLevel(DifficultyLevel.HARD);
        quiz.setIsPublic(false);
        quiz.setEnabled(false);
        quiz.setQuestions(questions);

        assertEquals(1L, quiz.getId());
        assertEquals("Math Quiz", quiz.getTitle());
        assertEquals("Basic math questions", quiz.getDescription());
        assertEquals(30, quiz.getTimeLimit());
        assertEquals(1L, quiz.getCategoryId());
        assertTrue(quiz.getIsTemplate());
        assertEquals(DifficultyLevel.HARD, quiz.getDifficultyLevel());
        assertFalse(quiz.getIsPublic());
        assertFalse(quiz.getEnabled());
        assertEquals(1, quiz.getQuestions().size());
        assertEquals("What is 2+2?", quiz.getQuestions().get(0).getQuestionText());
    }

    @Test
    void testValidation_ValidData() {
        QuizDto quiz = new QuizDto();
        quiz.setTitle("Science Quiz");
        quiz.setTimeLimit(30);

        var violations = validator.validate(quiz);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankTitle() {
        QuizDto quiz = new QuizDto();
        quiz.setTitle("");
        quiz.setTimeLimit(30);

        var violations = validator.validate(quiz);
        assertFalse(violations.isEmpty());
        assertEquals("Title is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_NullTimeLimit() {
        QuizDto quiz = new QuizDto();
        quiz.setTitle("Math Quiz");
        // timeLimit is null

        var violations = validator.validate(quiz);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testQuestionsListInitialization() {
        QuizDto quiz1 = new QuizDto();
        assertNotNull(quiz1.getQuestions());
        assertTrue(quiz1.getQuestions().isEmpty());

        QuizDto quiz2 = new QuizDto();
        quiz2.setQuestions(Arrays.asList(new QuestionDto(), new QuestionDto()));
        assertEquals(2, quiz2.getQuestions().size());
    }

    @Test
    void testDifficultyLevelDefaultValue() {
        QuizDto quiz = new QuizDto();
        assertEquals(DifficultyLevel.MEDIUM, quiz.getDifficultyLevel());

        quiz.setDifficultyLevel(null);
        // After setting null, getter should return MEDIUM (based on setter logic)
        assertEquals(DifficultyLevel.MEDIUM, quiz.getDifficultyLevel());
    }

    @Test
    void testBooleanDefaults() {
        QuizDto quiz = new QuizDto();

        // Check default values
        assertFalse(quiz.getIsTemplate());
        assertTrue(quiz.getIsPublic());
        assertTrue(quiz.getEnabled());
    }
}