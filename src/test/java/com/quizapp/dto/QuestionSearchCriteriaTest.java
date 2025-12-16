package com.quizapp.dto;

import com.quizapp.entity.DifficultyLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class QuestionSearchCriteriaTest {

    @Test
    void testDefaultConstructor() {
        QuestionSearchCriteria criteria = new QuestionSearchCriteria();

        assertNull(criteria.getSearchTerm());
        assertNull(criteria.getDifficultyLevel());
        assertNull(criteria.getTags());
        assertNull(criteria.getIsTemplate());
    }

    @Test
    void testAllArgsConstructor() {
        List<String> tags = Arrays.asList("java", "spring");
        QuestionSearchCriteria criteria = new QuestionSearchCriteria(
                "search term",
                DifficultyLevel.HARD,
                tags,
                true
        );

        assertEquals("search term", criteria.getSearchTerm());
        assertEquals(DifficultyLevel.HARD, criteria.getDifficultyLevel());
        assertEquals(2, criteria.getTags().size());
        assertTrue(criteria.getTags().contains("java"));
        assertTrue(criteria.getTags().contains("spring"));
        assertTrue(criteria.getIsTemplate());
    }

    @Test
    void testSettersAndGetters() {
        QuestionSearchCriteria criteria = new QuestionSearchCriteria();
        List<String> tags = Arrays.asList("tag1", "tag2", "tag3");

        criteria.setSearchTerm("new search");
        criteria.setDifficultyLevel(DifficultyLevel.EASY);
        criteria.setTags(tags);
        criteria.setIsTemplate(false);

        assertEquals("new search", criteria.getSearchTerm());
        assertEquals(DifficultyLevel.EASY, criteria.getDifficultyLevel());
        assertEquals(3, criteria.getTags().size());
        assertTrue(criteria.getTags().contains("tag2"));
        assertFalse(criteria.getIsTemplate());
    }

    @Test
    void testEmptyTags() {
        QuestionSearchCriteria criteria = new QuestionSearchCriteria();
        criteria.setTags(Collections.emptyList());

        assertNotNull(criteria.getTags());
        assertTrue(criteria.getTags().isEmpty());

        // Test null tags
        criteria.setTags(null);
        assertNull(criteria.getTags());
    }

    @Test
    void testPartialCriteria() {
        // Search term only
        QuestionSearchCriteria criteria1 = new QuestionSearchCriteria();
        criteria1.setSearchTerm("java");

        assertEquals("java", criteria1.getSearchTerm());
        assertNull(criteria1.getDifficultyLevel());
        assertNull(criteria1.getTags());
        assertNull(criteria1.getIsTemplate());

        // Difficulty only
        QuestionSearchCriteria criteria2 = new QuestionSearchCriteria();
        criteria2.setDifficultyLevel(DifficultyLevel.MEDIUM);

        assertNull(criteria2.getSearchTerm());
        assertEquals(DifficultyLevel.MEDIUM, criteria2.getDifficultyLevel());
        assertNull(criteria2.getTags());
        assertNull(criteria2.getIsTemplate());

        // Template flag only
        QuestionSearchCriteria criteria3 = new QuestionSearchCriteria();
        criteria3.setIsTemplate(true);

        assertNull(criteria3.getSearchTerm());
        assertNull(criteria3.getDifficultyLevel());
        assertNull(criteria3.getTags());
        assertTrue(criteria3.getIsTemplate());
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> tags1 = Arrays.asList("java", "spring");
        List<String> tags2 = Arrays.asList("java", "spring");

        QuestionSearchCriteria criteria1 = new QuestionSearchCriteria(
                "search", DifficultyLevel.HARD, tags1, true
        );
        QuestionSearchCriteria criteria2 = new QuestionSearchCriteria(
                "search", DifficultyLevel.HARD, tags2, true
        );
        QuestionSearchCriteria criteria3 = new QuestionSearchCriteria(
                "different", DifficultyLevel.EASY, Collections.emptyList(), false
        );

        assertEquals(criteria1, criteria1);
        assertNotEquals(criteria1, null);
        assertNotEquals(criteria1, new Object());
        assertEquals(criteria1.hashCode(), criteria2.hashCode());
        assertNotEquals(criteria1, criteria3);
    }

    @Test
    void testToString() {
        QuestionSearchCriteria criteria = new QuestionSearchCriteria(
                "test", DifficultyLevel.MEDIUM, Arrays.asList("tag1"), false
        );
        String toString = criteria.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("test"));
        assertTrue(toString.contains("MEDIUM"));
        assertTrue(toString.contains("tag1"));
    }
}