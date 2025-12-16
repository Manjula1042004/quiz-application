package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Entity Tests")
class CategoryEntityTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    @DisplayName("Should create category with default constructor")
    void constructor_Default() {
        // When
        Category newCategory = new Category();

        // Then
        assertNotNull(newCategory);
        assertNull(newCategory.getName());
        assertNull(newCategory.getDescription());
        assertNull(newCategory.getColor());
        assertNotNull(newCategory.getQuizzes());
        assertTrue(newCategory.getQuizzes().isEmpty());
    }

    @Test
    @DisplayName("Should create category with parameters")
    void constructor_WithParameters() {
        // When
        Category newCategory = new Category("Programming",
                "Programming related quizzes", "#FF5733");

        // Then
        assertEquals("Programming", newCategory.getName());
        assertEquals("Programming related quizzes", newCategory.getDescription());
        assertEquals("#FF5733", newCategory.getColor());
        assertNotNull(newCategory.getQuizzes());
        assertTrue(newCategory.getQuizzes().isEmpty());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        category.setId(10L);

        // Then
        assertEquals(10L, category.getId());
    }

    @Test
    @DisplayName("Should set and get name")
    void setNameAndGetName() {
        // When
        category.setName("Mathematics");

        // Then
        assertEquals("Mathematics", category.getName());
    }

    @Test
    @DisplayName("Should set and get description")
    void setDescriptionAndGetDescription() {
        // When
        category.setDescription("Math quizzes and puzzles");

        // Then
        assertEquals("Math quizzes and puzzles", category.getDescription());
    }

    @Test
    @DisplayName("Should set and get color")
    void setColorAndGetColor() {
        // When
        category.setColor("#33FF57");

        // Then
        assertEquals("#33FF57", category.getColor());
    }

    @Test
    @DisplayName("Should set and get created at timestamp")
    void setCreatedAtAndGetCreatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        category.setCreatedAt(now);

        // Then
        assertEquals(now, category.getCreatedAt());
    }

    @Test
    @DisplayName("Should set and get updated at timestamp")
    void setUpdatedAtAndGetUpdatedAt() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        category.setUpdatedAt(now);

        // Then
        assertEquals(now, category.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set and get quizzes")
    void setQuizzesAndGetQuizzes() {
        // Given
        List<Quiz> quizzes = new ArrayList<>();
        Quiz quiz1 = new Quiz();
        quiz1.setTitle("Java Basics");
        quizzes.add(quiz1);

        Quiz quiz2 = new Quiz();
        quiz2.setTitle("Advanced Java");
        quizzes.add(quiz2);

        // When
        category.setQuizzes(quizzes);

        // Then
        assertEquals(2, category.getQuizzes().size());
        assertEquals("Java Basics", category.getQuizzes().get(0).getTitle());
        assertEquals("Advanced Java", category.getQuizzes().get(1).getTitle());
    }

    @Test
    @DisplayName("Should add quiz to category")
    void addQuiz() {
        // Given
        Quiz quiz = new Quiz();
        quiz.setTitle("New Quiz");

        // When
        category.getQuizzes().add(quiz);
        quiz.setCategory(category);

        // Then
        assertEquals(1, category.getQuizzes().size());
        assertEquals("New Quiz", category.getQuizzes().get(0).getTitle());
        assertEquals(category, quiz.getCategory());
    }

    @Test
    @DisplayName("Should set timestamps on persist")
    void onPrePersist_SetsTimestamps() {
        // When
        category.onCreate();

        // Then
        assertNotNull(category.getCreatedAt());
        assertNotNull(category.getUpdatedAt());
        assertEquals(category.getCreatedAt(), category.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update timestamp on update")
    void onPreUpdate_UpdatesTimestamp() throws InterruptedException {
        // Given
        category.onCreate();
        LocalDateTime originalUpdatedAt = category.getUpdatedAt();

        // Wait a bit
        Thread.sleep(10);

        // When
        category.onUpdate();

        // Then
        assertTrue(category.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        category.setName(null);
        category.setDescription(null);
        category.setColor(null);
        category.setCreatedAt(null);
        category.setUpdatedAt(null);
        category.setQuizzes(null);

        // Then
        assertNull(category.getName());
        assertNull(category.getDescription());
        assertNull(category.getColor());
        assertNull(category.getCreatedAt());
        assertNull(category.getUpdatedAt());
        assertNull(category.getQuizzes());
    }

    @Test
    @DisplayName("Should count quizzes in category")
    void countQuizzes() {
        // When - No quizzes
        assertTrue(category.getQuizzes() == null || category.getQuizzes().isEmpty());

        // Given - Add quizzes
        List<Quiz> quizzes = new ArrayList<>();
        quizzes.add(new Quiz());
        quizzes.add(new Quiz());
        quizzes.add(new Quiz());
        category.setQuizzes(quizzes);

        // Then
        assertEquals(3, category.getQuizzes().size());
    }

    @Test
    @DisplayName("Should check if category has quizzes")
    void hasQuizzes() {
        // When - No quizzes
        assertTrue(category.getQuizzes() == null || category.getQuizzes().isEmpty());

        // Given - Add a quiz
        category.setQuizzes(new ArrayList<>());
        category.getQuizzes().add(new Quiz());

        // Then
        assertFalse(category.getQuizzes().isEmpty());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsCategoryInfo() {
        // Given
        category.setId(5L);
        category.setName("Science");
        category.setColor("#3357FF");

        // When
        String toString = category.toString();

        // Then
        assertTrue(toString.contains("Science"));
        assertTrue(toString.contains("#3357FF"));
    }

    @Test
    @DisplayName("Should create category with hex color code")
    void createCategoryWithHexColor() {
        // When
        category.setName("Art");
        category.setColor("#FF33A1");

        // Then
        assertEquals("Art", category.getName());
        assertEquals("#FF33A1", category.getColor());
        assertTrue(category.getColor().startsWith("#"));
        assertEquals(7, category.getColor().length()); // # + 6 hex digits
    }

    @Test
    @DisplayName("Should create category with RGB color")
    void createCategoryWithRGBColor() {
        // When
        category.setName("Design");
        category.setColor("rgb(255, 100, 50)");

        // Then
        assertEquals("Design", category.getName());
        assertEquals("rgb(255, 100, 50)", category.getColor());
    }

    @Test
    @DisplayName("Should create category with long description")
    void createCategoryWithLongDescription() {
        // Given
        String longDescription = "This category includes quizzes about various " +
                "programming languages, frameworks, tools, and best practices " +
                "used in software development.";

        // When
        category.setName("Programming");
        category.setDescription(longDescription);

        // Then
        assertEquals("Programming", category.getName());
        assertEquals(longDescription, category.getDescription());
        assertTrue(category.getDescription().length() > 50);
    }

    @Test
    @DisplayName("Should create category with no description")
    void createCategoryWithoutDescription() {
        // When
        category.setName("General Knowledge");
        category.setDescription(null);

        // Then
        assertEquals("General Knowledge", category.getName());
        assertNull(category.getDescription());
    }

    @Test
    @DisplayName("Should create category with no color")
    void createCategoryWithoutColor() {
        // When
        category.setName("History");
        category.setColor(null);

        // Then
        assertEquals("History", category.getName());
        assertNull(category.getColor());
    }

    @Test
    @DisplayName("Should handle category name with special characters")
    void categoryNameWithSpecialCharacters() {
        // When
        category.setName("Science & Technology");

        // Then
        assertEquals("Science & Technology", category.getName());
    }

    @Test
    @DisplayName("Should handle category name with numbers")
    void categoryNameWithNumbers() {
        // When
        category.setName("Web 2.0");

        // Then
        assertEquals("Web 2.0", category.getName());
    }
}