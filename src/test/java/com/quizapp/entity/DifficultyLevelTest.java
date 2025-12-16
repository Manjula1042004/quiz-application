package com.quizapp.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DifficultyLevel Enum Tests")
class DifficultyLevelTest {

    @Test
    @DisplayName("Should have all difficulty levels")
    void shouldHaveAllDifficultyLevels() {
        // When
        DifficultyLevel[] levels = DifficultyLevel.values();

        // Then
        assertEquals(3, levels.length);
        assertArrayEquals(new DifficultyLevel[]{
                DifficultyLevel.EASY,
                DifficultyLevel.MEDIUM,
                DifficultyLevel.HARD
        }, levels);
    }

    @ParameterizedTest
    @EnumSource(DifficultyLevel.class)
    @DisplayName("Should have valid enum values")
    void shouldHaveValidEnumValues(DifficultyLevel level) {
        // Then
        assertNotNull(level);
        assertNotNull(level.name());
        assertFalse(level.name().isEmpty());
    }

    @Test
    @DisplayName("Should convert string to enum")
    void valueOf_ValidString_ReturnsEnum() {
        // When & Then
        assertEquals(DifficultyLevel.EASY, DifficultyLevel.valueOf("EASY"));
        assertEquals(DifficultyLevel.MEDIUM, DifficultyLevel.valueOf("MEDIUM"));
        assertEquals(DifficultyLevel.HARD, DifficultyLevel.valueOf("HARD"));
    }

    @Test
    @DisplayName("Should have correct string representations")
    void toString_ReturnsCorrectString() {
        // When & Then
        assertEquals("EASY", DifficultyLevel.EASY.toString());
        assertEquals("MEDIUM", DifficultyLevel.MEDIUM.toString());
        assertEquals("HARD", DifficultyLevel.HARD.toString());
    }

    @Test
    @DisplayName("Should compare enum values")
    void compareEnumValues() {
        // When & Then
        assertTrue(DifficultyLevel.EASY != DifficultyLevel.MEDIUM);
        assertTrue(DifficultyLevel.MEDIUM != DifficultyLevel.HARD);
        assertTrue(DifficultyLevel.EASY != DifficultyLevel.HARD);
        assertEquals(DifficultyLevel.EASY, DifficultyLevel.EASY);
    }

    @Test
    @DisplayName("Should get enum by ordinal")
    void values_ReturnsByOrdinal() {
        // When & Then
        assertEquals(DifficultyLevel.EASY, DifficultyLevel.values()[0]);
        assertEquals(DifficultyLevel.MEDIUM, DifficultyLevel.values()[1]);
        assertEquals(DifficultyLevel.HARD, DifficultyLevel.values()[2]);
    }

    @Test
    @DisplayName("Should handle enum ordinal values")
    void ordinal_ReturnsCorrectIndex() {
        // When & Then
        assertEquals(0, DifficultyLevel.EASY.ordinal());
        assertEquals(1, DifficultyLevel.MEDIUM.ordinal());
        assertEquals(2, DifficultyLevel.HARD.ordinal());
    }

    @Test
    @DisplayName("Should iterate through all enum values")
    void values_Iteration() {
        // When
        int count = 0;
        for (DifficultyLevel level : DifficultyLevel.values()) {
            assertNotNull(level);
            count++;
        }

        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should use enum in switch statement")
    void enumInSwitchStatement() {
        // Given
        DifficultyLevel level = DifficultyLevel.MEDIUM;
        String result = "";

        // When
        switch (level) {
            case EASY:
                result = "Easy level";
                break;
            case MEDIUM:
                result = "Medium level";
                break;
            case HARD:
                result = "Hard level";
                break;
        }

        // Then
        assertEquals("Medium level", result);
    }

    @Test
    @DisplayName("Should check if enum contains value")
    void valueOf_ContainsValue() {
        // When & Then
        assertTrue(containsDifficultyLevel("EASY"));
        assertTrue(containsDifficultyLevel("MEDIUM"));
        assertTrue(containsDifficultyLevel("HARD"));
        assertFalse(containsDifficultyLevel("VERY_HARD"));
        assertFalse(containsDifficultyLevel("BEGINNER"));
    }

    private boolean containsDifficultyLevel(String name) {
        try {
            DifficultyLevel.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Test
    @DisplayName("Should handle case sensitivity")
    void valueOf_CaseSensitive() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            DifficultyLevel.valueOf("easy"); // lowercase should fail
        });

        assertThrows(IllegalArgumentException.class, () -> {
            DifficultyLevel.valueOf("Medium"); // mixed case should fail
        });
    }
}