package com.quizapp.dto;

import com.quizapp.entity.DifficultyLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QuestionUpdateRequestTest {

    @Test
    void testDefaultConstructor() {
        QuestionUpdateRequest request = new QuestionUpdateRequest();

        assertNull(request.getDifficultyLevel());
        assertNull(request.getPoints());
    }

    @Test
    void testAllArgsConstructor() {
        QuestionUpdateRequest request = new QuestionUpdateRequest(
                DifficultyLevel.HARD,
                10
        );

        assertEquals(DifficultyLevel.HARD, request.getDifficultyLevel());
        assertEquals(10, request.getPoints());
    }

    @Test
    void testSettersAndGetters() {
        QuestionUpdateRequest request = new QuestionUpdateRequest();

        request.setDifficultyLevel(DifficultyLevel.EASY);
        request.setPoints(5);

        assertEquals(DifficultyLevel.EASY, request.getDifficultyLevel());
        assertEquals(5, request.getPoints());
    }

    @Test
    void testPartialUpdate() {
        // Test setting only difficulty
        QuestionUpdateRequest request1 = new QuestionUpdateRequest();
        request1.setDifficultyLevel(DifficultyLevel.MEDIUM);

        assertEquals(DifficultyLevel.MEDIUM, request1.getDifficultyLevel());
        assertNull(request1.getPoints());

        // Test setting only points
        QuestionUpdateRequest request2 = new QuestionUpdateRequest();
        request2.setPoints(15);

        assertNull(request2.getDifficultyLevel());
        assertEquals(15, request2.getPoints());
    }

    @Test
    void testEqualsAndHashCode() {
        QuestionUpdateRequest request1 = new QuestionUpdateRequest(DifficultyLevel.HARD, 10);
        QuestionUpdateRequest request2 = new QuestionUpdateRequest(DifficultyLevel.HARD, 10);
        QuestionUpdateRequest request3 = new QuestionUpdateRequest(DifficultyLevel.EASY, 5);

        assertEquals(request1, request1);
        assertNotEquals(request1, null);
        assertNotEquals(request1, new Object());
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
    }

    @Test
    void testToString() {
        QuestionUpdateRequest request = new QuestionUpdateRequest(DifficultyLevel.MEDIUM, 8);
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("MEDIUM"));
        assertTrue(toString.contains("8"));
    }

    @Test
    void testNullValues() {
        QuestionUpdateRequest request = new QuestionUpdateRequest(null, null);

        assertNull(request.getDifficultyLevel());
        assertNull(request.getPoints());

        // Update with null values
        request.setDifficultyLevel(null);
        request.setPoints(null);

        assertNull(request.getDifficultyLevel());
        assertNull(request.getPoints());
    }
}