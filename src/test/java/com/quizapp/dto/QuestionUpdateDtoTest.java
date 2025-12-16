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
class QuestionUpdateDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultConstructor() {
        QuestionUpdateDto dto = new QuestionUpdateDto();

        assertNull(dto.getId());
        assertNull(dto.getQuestionText());
        assertNotNull(dto.getOptions());
        assertTrue(dto.getOptions().isEmpty());
        assertNull(dto.getCorrectAnswerIndex());
        assertEquals(DifficultyLevel.MEDIUM, dto.getDifficultyLevel());
        assertNull(dto.getExplanation());
        assertEquals(1, dto.getPoints());
    }

    @Test
    void testSettersAndGetters() {
        QuestionUpdateDto dto = new QuestionUpdateDto();
        List<String> options = Arrays.asList("Option A", "Option B", "Option C");

        dto.setId(1L);
        dto.setQuestionText("Updated question text?");
        dto.setOptions(options);
        dto.setCorrectAnswerIndex(1);
        dto.setDifficultyLevel(DifficultyLevel.HARD);
        dto.setExplanation("Updated explanation");
        dto.setPoints(10);

        assertEquals(1L, dto.getId());
        assertEquals("Updated question text?", dto.getQuestionText());
        assertEquals(3, dto.getOptions().size());
        assertEquals("Option B", dto.getOptions().get(1));
        assertEquals(1, dto.getCorrectAnswerIndex());
        assertEquals(DifficultyLevel.HARD, dto.getDifficultyLevel());
        assertEquals("Updated explanation", dto.getExplanation());
        assertEquals(10, dto.getPoints());
    }

    @Test
    void testValidation_ValidData() {
        QuestionUpdateDto dto = new QuestionUpdateDto();
        dto.setQuestionText("Valid question?");
        dto.setOptions(Arrays.asList("Option 1", "Option 2"));
        dto.setCorrectAnswerIndex(0);

        var violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_BlankQuestionText() {
        QuestionUpdateDto dto = new QuestionUpdateDto();
        dto.setQuestionText("");
        dto.setOptions(Arrays.asList("Option 1", "Option 2"));
        dto.setCorrectAnswerIndex(0);

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Question text is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_InsufficientOptions() {
        QuestionUpdateDto dto = new QuestionUpdateDto();
        dto.setQuestionText("Valid question?");
        dto.setOptions(Arrays.asList("Only one option")); // Only 1 option
        dto.setCorrectAnswerIndex(0);

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("At least 2 options are required")));
    }

    @Test
    void testValidation_BlankOption() {
        QuestionUpdateDto dto = new QuestionUpdateDto();
        dto.setQuestionText("Valid question?");
        dto.setOptions(Arrays.asList("Option 1", "")); // Blank option
        dto.setCorrectAnswerIndex(0);

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testValidation_NullCorrectAnswerIndex() {
        QuestionUpdateDto dto = new QuestionUpdateDto();
        dto.setQuestionText("Valid question?");
        dto.setOptions(Arrays.asList("Option 1", "Option 2"));
        // correctAnswerIndex is null

        var violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testDefaultValues() {
        QuestionUpdateDto dto = new QuestionUpdateDto();

        assertEquals(DifficultyLevel.MEDIUM, dto.getDifficultyLevel());
        assertEquals(1, dto.getPoints());

        // Test that setting null doesn't break defaults
        dto.setDifficultyLevel(null);
        dto.setPoints(null);

        assertNull(dto.getDifficultyLevel());
        assertNull(dto.getPoints());
    }

    @Test
    void testEqualsAndHashCode() {
        QuestionUpdateDto dto1 = new QuestionUpdateDto();
        dto1.setId(1L);
        dto1.setQuestionText("Question 1");

        QuestionUpdateDto dto2 = new QuestionUpdateDto();
        dto2.setId(1L);
        dto2.setQuestionText("Question 1");

        QuestionUpdateDto dto3 = new QuestionUpdateDto();
        dto3.setId(2L);
        dto3.setQuestionText("Question 2");

        assertEquals(dto1, dto1);
        assertNotEquals(dto1, null);
        assertNotEquals(dto1, new Object());
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    void testToString() {
        QuestionUpdateDto dto = new QuestionUpdateDto();
        dto.setId(1L);
        dto.setQuestionText("Test question?");

        String toString = dto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test question?"));
    }
}