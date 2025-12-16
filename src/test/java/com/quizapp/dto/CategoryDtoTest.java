package com.quizapp.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDefaultConstructor() {
        CategoryDto category = new CategoryDto();

        assertNull(category.getId());
        assertNull(category.getName());
        assertNull(category.getDescription());
        assertNull(category.getColor());
    }

    @Test
    void testSettersAndGetters() {
        CategoryDto category = new CategoryDto();

        category.setId(1L);
        category.setName("Science");
        category.setDescription("Science related quizzes");
        category.setColor("#FF0000");

        assertEquals(1L, category.getId());
        assertEquals("Science", category.getName());
        assertEquals("Science related quizzes", category.getDescription());
        assertEquals("#FF0000", category.getColor());
    }

    @Test
    void testValidation_ValidData() {
        CategoryDto category = new CategoryDto();
        category.setName("Mathematics");

        var violations = validator.validate(category);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidation_InvalidName() {
        CategoryDto category = new CategoryDto();
        category.setName(""); // Blank name

        var violations = validator.validate(category);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Category name is required", violations.iterator().next().getMessage());
    }

    @Test
    void testValidation_NullName() {
        CategoryDto category = new CategoryDto();
        // Name is null

        var violations = validator.validate(category);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void testEqualsAndHashCode() {
        CategoryDto category1 = new CategoryDto();
        category1.setId(1L);
        category1.setName("Science");

        CategoryDto category2 = new CategoryDto();
        category2.setId(1L);
        category2.setName("Science");

        assertEquals(category1, category1); // Same object
        assertNotEquals(category1, null); // Not null
        assertNotEquals(category1, new Object()); // Different class

        // ID-based equality
        CategoryDto category3 = new CategoryDto();
        category3.setId(2L);
        category3.setName("Science");

        assertNotEquals(category1, category3);
        assertEquals(category1.hashCode(), category2.hashCode());
    }

    @Test
    void testToString() {
        CategoryDto category = new CategoryDto();
        category.setId(1L);
        category.setName("Science");

        String toString = category.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Science"));
    }
}