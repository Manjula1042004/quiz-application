package com.quizapp.service;

import com.quizapp.entity.Category;
import com.quizapp.repository.CategoryRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Mathematics");
        category.setDescription("Math related quizzes");
        category.setColor("#FF0000");
    }

    @Test
    void createCategory_Success() {
        // Arrange
        when(categoryRepository.existsByName("Mathematics")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category created = categoryService.createCategory(category);

        // Assert
        assertNotNull(created);
        assertEquals("Mathematics", created.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void createCategory_DuplicateName() {
        // Arrange
        when(categoryRepository.existsByName("Mathematics")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(category);
        });
    }

    @Test
    void updateCategory_Success() {
        // Arrange
        Category updatedDetails = new Category();
        updatedDetails.setName("Updated Math");
        updatedDetails.setDescription("Updated description");
        updatedDetails.setColor("#00FF00");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Updated Math")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        Category updated = categoryService.updateCategory(1L, updatedDetails);

        // Assert
        assertNotNull(updated);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void updateCategory_NotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, category);
        });
    }

    @Test
    void updateCategory_DuplicateName() {
        // Arrange
        Category updatedDetails = new Category();
        updatedDetails.setName("Existing Category");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByName("Existing Category")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, updatedDetails);
        });
    }

    @Test
    void deleteCategory_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.countQuizzesByCategoryId(1L)).thenReturn(0L);
        doNothing().when(categoryRepository).delete(category);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void deleteCategory_WithQuizzes() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.countQuizzesByCategoryId(1L)).thenReturn(5L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(1L);
        });
    }

    @Test
    void getAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(category);
        when(categoryRepository.findAllOrderByName()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Mathematics", result.get(0).getName());
    }

    @Test
    void getCategoryById_Found() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.getCategoryById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Mathematics", result.get().getName());
    }

    @Test
    void getCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.getCategoryById(1L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getCategoryByName_Found() {
        // Arrange
        when(categoryRepository.findByName("Mathematics")).thenReturn(Optional.of(category));

        // Act
        Optional<Category> result = categoryService.getCategoryByName("Mathematics");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Mathematics", result.get().getName());
    }
}