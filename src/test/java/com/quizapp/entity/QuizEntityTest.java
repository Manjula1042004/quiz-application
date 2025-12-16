package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quiz Entity Tests")
class QuizEntityTest {

    private Quiz quiz;
    private User creator;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");
    }

    @Test
    @DisplayName("Should create quiz with default values")
    void constructor_DefaultValues() {
        // When
        Quiz newQuiz = new Quiz();

        // Then
        assertNotNull(newQuiz);
        assertEquals(DifficultyLevel.MEDIUM, newQuiz.getDifficultyLevel());
        assertTrue(newQuiz.getIsPublic());
        assertTrue(newQuiz.getEnabled());
        assertFalse(newQuiz.getIsTemplate());
        assertNotNull(newQuiz.getCreatedAt());
        assertNotNull(newQuiz.getQuestions());
        assertTrue(newQuiz.getQuestions().isEmpty());
        assertNotNull(newQuiz.getQuizAttempts());
        assertTrue(newQuiz.getQuizAttempts().isEmpty());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        quiz.setId(100L);

        // Then
        assertEquals(100L, quiz.getId());
    }

    @Test
    @DisplayName("Should set and get title")
    void setTitleAndGetTitle() {
        // When
        quiz.setTitle("Java Programming Quiz");

        // Then
        assertEquals("Java Programming Quiz", quiz.getTitle());
    }

    @Test
    @DisplayName("Should set and get description")
    void setDescriptionAndGetDescription() {
        // When
        quiz.setDescription("Test your Java programming knowledge");

        // Then
        assertEquals("Test your Java programming knowledge", quiz.getDescription());
    }

    @Test
    @DisplayName("Should set and get time limit")
    void setTimeLimitAndGetTimeLimit() {
        // When
        quiz.setTimeLimit(45);

        // Then
        assertEquals(45, quiz.getTimeLimit());
    }

    @Test
    @DisplayName("Should set and get created at timestamp")
    void setCreatedAtAndGetCreatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        quiz.setCreatedAt(now);

        // Then
        assertEquals(now, quiz.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get creator")
    void setCreatedByAndGetCreatedBy() {
        // When
        quiz.setCreatedBy(creator);

        // Then
        assertEquals(creator, quiz.getCreatedBy());
        assertEquals(1L, quiz.getCreatedBy().getId());
        assertEquals("creator", quiz.getCreatedBy().getUsername());
    }

    @Test
    @DisplayName("Should set and get category")
    void setCategoryAndGetCategory() {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Programming");

        // When
        quiz.setCategory(category);

        // Then
        assertEquals(category, quiz.getCategory());
        assertEquals("Programming", quiz.getCategory().getName());
    }

    @Test
    @DisplayName("Should set and get template flag")
    void setIsTemplateAndGetIsTemplate() {
        // When
        quiz.setIsTemplate(true);

        // Then
        assertTrue(quiz.getIsTemplate());

        // When
        quiz.setIsTemplate(false);

        // Then
        assertFalse(quiz.getIsTemplate());
    }

    @Test
    @DisplayName("Should set and get difficulty level")
    void setDifficultyLevelAndGetDifficultyLevel() {
        // When
        quiz.setDifficultyLevel(DifficultyLevel.HARD);

        // Then
        assertEquals(DifficultyLevel.HARD, quiz.getDifficultyLevel());

        // When - Test default when null
        quiz.setDifficultyLevel(null);

        // Then
        assertEquals(DifficultyLevel.MEDIUM, quiz.getDifficultyLevel());
    }

    @Test
    @DisplayName("Should set and get public flag")
    void setIsPublicAndGetIsPublic() {
        // When
        quiz.setIsPublic(false);

        // Then
        assertFalse(quiz.getIsPublic());

        // When - Test default when null
        quiz.setIsPublic(null);

        // Then
        assertTrue(quiz.getIsPublic());
    }

    @Test
    @DisplayName("Should set and get enabled flag")
    void setEnabledAndGetEnabled() {
        // When
        quiz.setEnabled(false);

        // Then
        assertFalse(quiz.getEnabled());

        // When - Test default when null
        quiz.setEnabled(null);

        // Then
        assertTrue(quiz.getEnabled());
    }

    @Test
    @DisplayName("Should set and get questions")
    void setQuestionsAndGetQuestions() {
        // Given
        ArrayList<Question> questions = new ArrayList<>();
        Question question1 = new Question();
        question1.setQuestionText("What is Java?");
        questions.add(question1);

        Question question2 = new Question();
        question2.setQuestionText("What is Spring?");
        questions.add(question2);

        // When
        quiz.setQuestions(questions);

        // Then
        assertEquals(2, quiz.getQuestions().size());
        assertEquals("What is Java?", quiz.getQuestions().get(0).getQuestionText());
        assertEquals("What is Spring?", quiz.getQuestions().get(1).getQuestionText());
    }

    @Test
    @DisplayName("Should add question to quiz")
    void addQuestion() {
        // Given
        Question question = new Question();
        question.setQuestionText("New question");

        // When
        quiz.getQuestions().add(question);
        question.setQuiz(quiz);

        // Then
        assertEquals(1, quiz.getQuestions().size());
        assertEquals("New question", quiz.getQuestions().get(0).getQuestionText());
        assertEquals(quiz, question.getQuiz());
    }

    @Test
    @DisplayName("Should set and get quiz attempts")
    void setQuizAttemptsAndGetQuizAttempts() {
        // Given
        ArrayList<QuizAttempt> attempts = new ArrayList<>();
        QuizAttempt attempt = new QuizAttempt();
        attempts.add(attempt);

        // When
        quiz.setQuizAttempts(attempts);

        // Then
        assertEquals(1, quiz.getQuizAttempts().size());
        assertEquals(attempt, quiz.getQuizAttempts().get(0));
    }

    @Test
    @DisplayName("Should add quiz attempt")
    void addQuizAttempt() {
        // Given
        QuizAttempt attempt = new QuizAttempt();

        // When
        quiz.getQuizAttempts().add(attempt);
        attempt.setQuiz(quiz);

        // Then
        assertEquals(1, quiz.getQuizAttempts().size());
        assertEquals(quiz, attempt.getQuiz());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        quiz.setTitle(null);
        quiz.setDescription(null);
        quiz.setCreatedBy(null);
        quiz.setCategory(null);
        quiz.setQuestions(null);
        quiz.setQuizAttempts(null);

        // Then
        assertNull(quiz.getTitle());
        assertNull(quiz.getDescription());
        assertNull(quiz.getCreatedBy());
        assertNull(quiz.getCategory());
        assertNull(quiz.getQuestions());
        assertNull(quiz.getQuizAttempts());
    }

    @Test
    @DisplayName("Should calculate total points from questions")
    void calculateTotalPoints() {
        // Given
        Question question1 = new Question();
        question1.setPoints(2);

        Question question2 = new Question();
        question2.setPoints(3);

        Question question3 = new Question();
        question3.setPoints(1);

        quiz.setQuestions(Arrays.asList(question1, question2, question3));

        // When - Calculate manually
        int totalPoints = quiz.getQuestions().stream()
                .mapToInt(Question::getPoints)
                .sum();

        // Then
        assertEquals(6, totalPoints);
    }

    @Test
    @DisplayName("Should check if quiz has questions")
    void hasQuestions() {
        // When - No questions
        assertTrue(quiz.getQuestions().isEmpty());

        // Given - Add a question
        Question question = new Question();
        quiz.getQuestions().add(question);

        // Then
        assertFalse(quiz.getQuestions().isEmpty());
        assertEquals(1, quiz.getQuestions().size());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsQuizInfo() {
        // Given
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDifficultyLevel(DifficultyLevel.EASY);

        // When
        String toString = quiz.toString();

        // Then
        assertTrue(toString.contains("Test Quiz"));
        assertTrue(toString.contains("EASY"));
    }

    @Test
    @DisplayName("Should create quiz with custom difficulty")
    void createQuizWithCustomDifficulty() {
        // When
        quiz.setDifficultyLevel(DifficultyLevel.HARD);

        // Then
        assertEquals(DifficultyLevel.HARD, quiz.getDifficultyLevel());
    }

    @Test
    @DisplayName("Should create private quiz")
    void createPrivateQuiz() {
        // When
        quiz.setIsPublic(false);

        // Then
        assertFalse(quiz.getIsPublic());
    }

    @Test
    @DisplayName("Should create disabled quiz")
    void createDisabledQuiz() {
        // When
        quiz.setEnabled(false);

        // Then
        assertFalse(quiz.getEnabled());
    }

    @Test
    @DisplayName("Should create template quiz")
    void createTemplateQuiz() {
        // When
        quiz.setIsTemplate(true);

        // Then
        assertTrue(quiz.getIsTemplate());
    }
}