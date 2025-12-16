package com.quizapp.repository;

import com.quizapp.entity.Category;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import com.quizapp.entity.DifficultyLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CategoryRepository Tests")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizRepository quizRepository;

    private Category programmingCategory;
    private Category mathCategory;

    @BeforeEach
    void setUp() {
        // Create categories
        programmingCategory = new Category();
        programmingCategory.setName("Programming");
        programmingCategory.setDescription("Programming related quizzes");
        programmingCategory.setColor("#FF5733");
        entityManager.persist(programmingCategory);

        mathCategory = new Category();
        mathCategory.setName("Mathematics");
        mathCategory.setDescription("Math quizzes");
        mathCategory.setColor("#33FF57");
        entityManager.persist(mathCategory);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find category by name")
    void findByName_CategoryExists_ReturnsCategory() {
        // When
        Optional<Category> found = categoryRepository.findByName("Programming");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Programming", found.get().getName());
        assertEquals("#FF5733", found.get().getColor());
    }

    @Test
    @DisplayName("Should return empty when category name not found")
    void findByName_CategoryNotFound_ReturnsEmpty() {
        // When
        Optional<Category> found = categoryRepository.findByName("Nonexistent");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should check if category exists by name")
    void existsByName_CategoryExists_ReturnsTrue() {
        // When
        boolean exists = categoryRepository.existsByName("Programming");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should check if category doesn't exist by name")
    void existsByName_CategoryNotExists_ReturnsFalse() {
        // When
        boolean exists = categoryRepository.existsByName("Science");

        // Then
        assertFalse(exists);
    }

    @Test
    @DisplayName("Should find all categories ordered by name")
    void findAllOrderByName_ReturnsOrderedCategories() {
        // When
        List<Category> categories = categoryRepository.findAllOrderByName();

        // Then
        assertEquals(2, categories.size());
        assertEquals("Mathematics", categories.get(0).getName()); // M comes before P
        assertEquals("Programming", categories.get(1).getName());
    }

    @Test
    @DisplayName("Should count quizzes by category ID")
    void countQuizzesByCategoryId_ReturnsCorrectCount() {
        // Given - create a quiz with category
        User testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.ADMIN);
        testUser.setEnabled(true);
        entityManager.persist(testUser);

        Quiz quiz = new Quiz();
        quiz.setTitle("Java Quiz");
        quiz.setDescription("Java programming quiz");
        quiz.setTimeLimit(30);
        quiz.setCreatedBy(testUser);
        quiz.setCategory(programmingCategory);
        quiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        quiz.setIsPublic(true);
        quiz.setEnabled(true);
        entityManager.persist(quiz);

        entityManager.flush();

        // When
        Long count = categoryRepository.countQuizzesByCategoryId(programmingCategory.getId());

        // Then
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Should count zero quizzes for category with no quizzes")
    void countQuizzesByCategoryId_NoQuizzes_ReturnsZero() {
        // When
        Long count = categoryRepository.countQuizzesByCategoryId(mathCategory.getId());

        // Then
        assertEquals(0L, count);
    }

    @Test
    @DisplayName("Should save new category")
    void save_NewCategory_SuccessfullySaved() {
        // Given
        Category newCategory = new Category();
        newCategory.setName("Science");
        newCategory.setDescription("Science quizzes");
        newCategory.setColor("#3357FF");

        // When
        Category savedCategory = categoryRepository.save(newCategory);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedCategory.getId());
        Category foundCategory = entityManager.find(Category.class, savedCategory.getId());
        assertEquals("Science", foundCategory.getName());
        assertNotNull(foundCategory.getCreatedAt());
        assertNotNull(foundCategory.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update category")
    void save_UpdateCategory_SuccessfullyUpdated() {
        // Given
        programmingCategory.setName("Computer Programming");
        programmingCategory.setDescription("Updated description");
        programmingCategory.setColor("#FF3333");

        // When
        Category updatedCategory = categoryRepository.save(programmingCategory);
        entityManager.flush();
        entityManager.clear();

        // Then
        Category foundCategory = entityManager.find(Category.class, programmingCategory.getId());
        assertEquals("Computer Programming", foundCategory.getName());
        assertEquals("Updated description", foundCategory.getDescription());
        assertEquals("#FF3333", foundCategory.getColor());
    }

    @Test
    @DisplayName("Should delete category")
    void delete_CategoryExists_SuccessfullyDeleted() {
        // When
        categoryRepository.delete(programmingCategory);
        entityManager.flush();
        entityManager.clear();

        // Then
        Category foundCategory = entityManager.find(Category.class, programmingCategory.getId());
        assertNull(foundCategory);
    }

    @Test
    @DisplayName("Should have automatic timestamps on create")
    void onCreate_ShouldSetTimestamps() {
        // Given
        Category newCategory = new Category();
        newCategory.setName("History");
        newCategory.setDescription("History quizzes");
        newCategory.setColor("#FFFF33");

        // When
        Category savedCategory = categoryRepository.save(newCategory);
        entityManager.flush();
        entityManager.clear();

        // Then
        Category foundCategory = entityManager.find(Category.class, savedCategory.getId());
        assertNotNull(foundCategory.getCreatedAt());
        assertNotNull(foundCategory.getUpdatedAt());
        assertEquals(foundCategory.getCreatedAt(), foundCategory.getUpdatedAt());
    }
}