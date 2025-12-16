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
class QuestionDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultConstructor() {
        QuestionDto question = new QuestionDto();

        assertNull(question.getId());
        assertNull(question.getQuestionText());
        assertNotNull(question.getOptions());
        assertTrue(question.getOptions().isEmpty());
        assertNull(question.getCorrectAnswerIndex());
        assertEquals(DifficultyLevel.MEDIUM, question.getDifficultyLevel());
        assertNull(question.getExplanation());
        assertEquals(1, question.getPoints());
        assertNotNull(question.getTags());
        assertTrue(question.getTags().isEmpty());
        assertFalse(question.getIsTemplate());
    }

    @Test
    void testSettersAndGetters() {
        QuestionDto question = new QuestionDto();

        question.setId(1L);
        question.setQuestionText("What is the capital of France?");
        question.setOptions(Arrays.asList("London", "Berlin", "Paris", "Madrid"));
        question.setCorrectAnswerIndex(2);
        question.setDifficultyLevel(DifficultyLevel.EASY);
        question.setExplanation("Paris is the capital of France");
        question.setPoints(5);
        question.setTags(Arrays.asList("geography", "capital"));
        question.setIsTemplate(true);

        assertEquals(1L, question.getId());
        assertEquals("What is the capital of France?", question.getQuestionText());
        assertEquals(4, question.getOptions().size());
        assertEquals("Paris", question.getOptions().get(2));
        assertEquals(2, question.getCorrectAnswerIndex());
        assertEquals(DifficultyLevel.EASY, question.getDifficultyLevel());
        assertEquals("Paris is the capital of France", question.getExplanation());
        assertEquals(5, question.getPoints());
        assertEquals(2, question.getTags().size());
        assertTrue(question.getTags().contains("geography"));
        assertTrue(question.getIsTemplate());
    }

    @Test
    void testValidation_ValidData() {
        QuestionDto question = new QuestionDto();
        question.setQuestionText("Valid question?");
        question.setOptions(Arrays.asList("Option 1", "Option 2"));
        question.setCorrectAnswerIndex(0);

        var violations = validator.validate(question);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankQuestionText() {
        QuestionDto question = new QuestionDto();
        question.setQuestionText("");
        question.setOptions(Arrays.asList("Option 1", "Option 2"));
        question.setCorrectAnswerIndex(0);

        var violations = validator.validate(question);
        assertFalse(violations.isEmpty());
        assertEquals("Question text is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_NullCorrectAnswerIndex() {
        QuestionDto question = new QuestionDto();
        question.setQuestionText("Valid question?");
        question.setOptions(Arrays.asList("Option 1", "Option 2"));
        // correctAnswerIndex is null

        var violations = validator.validate(question);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testDifficultyLevelDefault() {
        QuestionDto question1 = new QuestionDto();
        assertEquals(DifficultyLevel.MEDIUM, question1.getDifficultyLevel());

        QuestionDto question2 = new QuestionDto();
        question2.setDifficultyLevel(null);
        assertEquals(DifficultyLevel.MEDIUM, question2.getDifficultyLevel());
    }

    @Test
    void testPointsDefault() {
        QuestionDto question1 = new QuestionDto();
        assertEquals(1, question1.getPoints());

        QuestionDto question2 = new QuestionDto();
        question2.setPoints(null);
        assertEquals(1, question2.getPoints());
    }

    @Test
    void testCollectionsInitialization() {
        QuestionDto question = new QuestionDto();

        // Options should be initialized
        assertNotNull(question.getOptions());
        assertTrue(question.getOptions().isEmpty());

        // Tags should be initialized
        assertNotNull(question.getTags());
        assertTrue(question.getTags().isEmpty());

        // Test setting collections
        List<String> options = Arrays.asList("A", "B", "C");
        List<String> tags = Arrays.asList("tag1", "tag2");

        question.setOptions(options);
        question.setTags(tags);

        assertEquals(3, question.getOptions().size());
        assertEquals(2, question.getTags().size());
    }

    @Test
    void testEqualsAndHashCode() {
        QuestionDto question1 = new QuestionDto();
        question1.setId(1L);
        question1.setQuestionText("Question 1");

        QuestionDto question2 = new QuestionDto();
        question2.setId(1L);
        question2.setQuestionText("Question 1");

        QuestionDto question3 = new QuestionDto();
        question3.setId(2L);
        question3.setQuestionText("Question 2");

        assertEquals(question1, question1);
        assertNotEquals(question1, null);
        assertNotEquals(question1, new Object());
        assertEquals(question1.hashCode(), question2.hashCode());
        assertNotEquals(question1, question3);
    }
}