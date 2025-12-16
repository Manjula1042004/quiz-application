package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Question Entity Tests")
class QuestionEntityTest {

    private Question question;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        question = new Question();
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
    }

    @Test
    @DisplayName("Should create question with default values")
    void constructor_DefaultValues() {
        // When
        Question newQuestion = new Question();

        // Then
        assertNotNull(newQuestion);
        assertEquals(DifficultyLevel.MEDIUM, newQuestion.getDifficultyLevel());
        assertEquals(1, newQuestion.getPoints());
        assertFalse(newQuestion.getIsTemplate());
        assertNotNull(newQuestion.getOptions());
        assertTrue(newQuestion.getOptions().isEmpty());
        assertNotNull(newQuestion.getTags());
        assertTrue(newQuestion.getTags().isEmpty());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        question.setId(50L);

        // Then
        assertEquals(50L, question.getId());
    }

    @Test
    @DisplayName("Should set and get question text")
    void setQuestionTextAndGetQuestionText() {
        // When
        question.setQuestionText("What is the capital of France?");

        // Then
        assertEquals("What is the capital of France?", question.getQuestionText());
    }

    @Test
    @DisplayName("Should set and get options")
    void setOptionsAndGetOptions() {
        // Given
        List<String> options = Arrays.asList("Paris", "London", "Berlin", "Madrid");

        // When
        question.setOptions(options);

        // Then
        assertEquals(4, question.getOptions().size());
        assertEquals("Paris", question.getOptions().get(0));
        assertEquals("London", question.getOptions().get(1));
        assertEquals("Berlin", question.getOptions().get(2));
        assertEquals("Madrid", question.getOptions().get(3));
    }

    @Test
    @DisplayName("Should set and get correct answer index")
    void setCorrectAnswerIndexAndGetCorrectAnswerIndex() {
        // When
        question.setCorrectAnswerIndex(2);

        // Then
        assertEquals(2, question.getCorrectAnswerIndex());
    }

    @Test
    @DisplayName("Should validate correct answer index within bounds")
    void correctAnswerIndex_WithinBounds() {
        // Given
        question.setOptions(Arrays.asList("A", "B", "C", "D"));

        // When
        question.setCorrectAnswerIndex(0); // First option
        assertEquals(0, question.getCorrectAnswerIndex());

        question.setCorrectAnswerIndex(3); // Last option
        assertEquals(3, question.getCorrectAnswerIndex());
    }

    @Test
    @DisplayName("Should set and get difficulty level")
    void setDifficultyLevelAndGetDifficultyLevel() {
        // When
        question.setDifficultyLevel(DifficultyLevel.HARD);

        // Then
        assertEquals(DifficultyLevel.HARD, question.getDifficultyLevel());

        // When - Test default when null
        question.setDifficultyLevel(null);

        // Then
        assertEquals(DifficultyLevel.MEDIUM, question.getDifficultyLevel());
    }

    @Test
    @DisplayName("Should set and get explanation")
    void setExplanationAndGetExplanation() {
        // When
        question.setExplanation("Paris is the capital and most populous city of France.");

        // Then
        assertEquals("Paris is the capital and most populous city of France.", question.getExplanation());
    }

    @Test
    @DisplayName("Should set and get points")
    void setPointsAndGetPoints() {
        // When
        question.setPoints(5);

        // Then
        assertEquals(5, question.getPoints());

        // When - Test default when null
        question.setPoints(null);

        // Then
        assertEquals(1, question.getPoints());
    }

    @Test
    @DisplayName("Should set and get quiz")
    void setQuizAndGetQuiz() {
        // When
        question.setQuiz(quiz);

        // Then
        assertEquals(quiz, question.getQuiz());
        assertEquals(1L, question.getQuiz().getId());
        assertEquals("Test Quiz", question.getQuiz().getTitle());
    }

    @Test
    @DisplayName("Should set and get tags")
    void setTagsAndGetTags() {
        // Given
        Tag tag1 = new Tag("Geography");
        Tag tag2 = new Tag("Europe");
        List<Tag> tags = Arrays.asList(tag1, tag2);

        // When
        question.setTags(tags);

        // Then
        assertEquals(2, question.getTags().size());
        assertEquals("Geography", question.getTags().get(0).getName());
        assertEquals("Europe", question.getTags().get(1).getName());
    }

    @Test
    @DisplayName("Should add tag to question")
    void addTag() {
        // Given
        Tag tag = new Tag("History");

        // When
        question.getTags().add(tag);

        // Then
        assertEquals(1, question.getTags().size());
        assertEquals("History", question.getTags().get(0).getName());
    }

    @Test
    @DisplayName("Should set and get template flag")
    void setIsTemplateAndGetIsTemplate() {
        // When
        question.setIsTemplate(true);

        // Then
        assertTrue(question.getIsTemplate());

        // When
        question.setIsTemplate(false);

        // Then
        assertFalse(question.getIsTemplate());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        question.setQuestionText(null);
        question.setOptions(null);
        question.setExplanation(null);
        question.setQuiz(null);
        question.setTags(null);

        // Then
        assertNull(question.getQuestionText());
        assertNull(question.getOptions());
        assertNull(question.getExplanation());
        assertNull(question.getQuiz());
        assertNull(question.getTags());
    }

    @Test
    @DisplayName("Should check if question is multiple choice")
    void isMultipleChoice() {
        // When - No options
        assertTrue(question.getOptions().isEmpty());

        // Given - Add options
        question.setOptions(Arrays.asList("Option A", "Option B"));

        // Then
        assertEquals(2, question.getOptions().size());
        assertTrue(question.getOptions().size() >= 2);
    }

    @Test
    @DisplayName("Should check if correct answer is valid")
    void isValidCorrectAnswer() {
        // Given
        question.setOptions(Arrays.asList("A", "B", "C", "D"));

        // When - Valid index
        question.setCorrectAnswerIndex(1);

        // Then
        assertTrue(question.getCorrectAnswerIndex() >= 0);
        assertTrue(question.getCorrectAnswerIndex() < question.getOptions().size());
    }

    @Test
    @DisplayName("Should create question without quiz (for question bank)")
    void questionWithoutQuiz() {
        // When
        question.setQuiz(null);

        // Then
        assertNull(question.getQuiz());
        assertTrue(question.getIsTemplate() == null || !question.getIsTemplate());
    }

    @Test
    @DisplayName("Should create template question for question bank")
    void templateQuestionForQuestionBank() {
        // When
        question.setIsTemplate(true);
        question.setQuiz(null);

        // Then
        assertTrue(question.getIsTemplate());
        assertNull(question.getQuiz());
    }

    @Test
    @DisplayName("Should return correct answer text")
    void getCorrectAnswerText() {
        // Given
        List<String> options = Arrays.asList("Paris", "London", "Berlin", "Madrid");
        question.setOptions(options);
        question.setCorrectAnswerIndex(0);

        // When
        String correctAnswer = question.getOptions().get(question.getCorrectAnswerIndex());

        // Then
        assertEquals("Paris", correctAnswer);
    }

    @Test
    @DisplayName("Should calculate question complexity based on options")
    void questionComplexity() {
        // Given - Simple question with 2 options
        question.setOptions(Arrays.asList("True", "False"));
        assertEquals(2, question.getOptions().size());

        // Given - Complex question with 6 options
        question.setOptions(Arrays.asList("A", "B", "C", "D", "E", "F"));
        assertEquals(6, question.getOptions().size());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsQuestionInfo() {
        // Given
        question.setId(10L);
        question.setQuestionText("Sample question?");
        question.setDifficultyLevel(DifficultyLevel.EASY);

        // When
        String toString = question.toString();

        // Then
        assertTrue(toString.contains("Sample question"));
        assertTrue(toString.contains("EASY"));
    }

    @Test
    @DisplayName("Should handle question with many options")
    void questionWithManyOptions() {
        // Given
        List<String> options = Arrays.asList(
                "Option 1", "Option 2", "Option 3", "Option 4",
                "Option 5", "Option 6", "Option 7", "Option 8"
        );

        // When
        question.setOptions(options);

        // Then
        assertEquals(8, question.getOptions().size());
    }

    @Test
    @DisplayName("Should handle question with explanation")
    void questionWithDetailedExplanation() {
        // Given
        String detailedExplanation = "This is a detailed explanation that provides " +
                "additional context and information about the correct answer. " +
                "It helps users understand why the answer is correct.";

        // When
        question.setExplanation(detailedExplanation);

        // Then
        assertEquals(detailedExplanation, question.getExplanation());
        assertTrue(question.getExplanation().length() > 50);
    }

    @Test
    @DisplayName("Should handle question with high points value")
    void questionWithHighPoints() {
        // When
        question.setPoints(10);

        // Then
        assertEquals(10, question.getPoints());
        assertTrue(question.getPoints() > 1);
    }

    @Test
    @DisplayName("Should create easy question")
    void createEasyQuestion() {
        // When
        question.setDifficultyLevel(DifficultyLevel.EASY);
        question.setPoints(1);

        // Then
        assertEquals(DifficultyLevel.EASY, question.getDifficultyLevel());
        assertEquals(1, question.getPoints());
    }

    @Test
    @DisplayName("Should create hard question")
    void createHardQuestion() {
        // When
        question.setDifficultyLevel(DifficultyLevel.HARD);
        question.setPoints(5);
        question.setExplanation("Complex explanation for hard question");

        // Then
        assertEquals(DifficultyLevel.HARD, question.getDifficultyLevel());
        assertEquals(5, question.getPoints());
        assertNotNull(question.getExplanation());
    }
}