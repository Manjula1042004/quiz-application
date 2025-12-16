package com.quizapp.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tag Entity Tests")
class TagEntityTest {

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag();
    }

    @Test
    @DisplayName("Should create tag with default constructor")
    void constructor_Default() {
        // When
        Tag newTag = new Tag();

        // Then
        assertNotNull(newTag);
        assertNull(newTag.getName());
        assertNotNull(newTag.getQuestions());
        assertTrue(newTag.getQuestions().isEmpty());
    }

    @Test
    @DisplayName("Should create tag with name parameter")
    void constructor_WithName() {
        // When
        Tag newTag = new Tag("Java");

        // Then
        assertEquals("Java", newTag.getName());
        assertNotNull(newTag.getQuestions());
        assertTrue(newTag.getQuestions().isEmpty());
    }

    @Test
    @DisplayName("Should set and get ID")
    void setIdAndGetId() {
        // When
        tag.setId(20L);

        // Then
        assertEquals(20L, tag.getId());
    }

    @Test
    @DisplayName("Should set and get name")
    void setNameAndGetName() {
        // When
        tag.setName("Spring Framework");

        // Then
        assertEquals("Spring Framework", tag.getName());
    }

    @Test
    @DisplayName("Should set and get questions")
    void setQuestionsAndGetQuestions() {
        // Given
        List<Question> questions = new ArrayList<>();
        Question question1 = new Question();
        question1.setQuestionText("What is Java?");
        questions.add(question1);

        Question question2 = new Question();
        question2.setQuestionText("What is Spring?");
        questions.add(question2);

        // When
        tag.setQuestions(questions);

        // Then
        assertEquals(2, tag.getQuestions().size());
        assertEquals("What is Java?", tag.getQuestions().get(0).getQuestionText());
        assertEquals("What is Spring?", tag.getQuestions().get(1).getQuestionText());
    }

    @Test
    @DisplayName("Should add question to tag")
    void addQuestion() {
        // Given
        Question question = new Question();
        question.setQuestionText("New question about Java");

        // When
        tag.getQuestions().add(question);
        question.getTags().add(tag);

        // Then
        assertEquals(1, tag.getQuestions().size());
        assertEquals("New question about Java", tag.getQuestions().get(0).getQuestionText());
        assertTrue(question.getTags().contains(tag));
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void handleNullValues() {
        // When
        tag.setName(null);
        tag.setQuestions(null);

        // Then
        assertNull(tag.getName());
        assertNull(tag.getQuestions());
    }

    @Test
    @DisplayName("Should count questions with tag")
    void countQuestions() {
        // When - No questions
        assertTrue(tag.getQuestions() == null || tag.getQuestions().isEmpty());

        // Given - Add questions
        List<Question> questions = new ArrayList<>();
        questions.add(new Question());
        questions.add(new Question());
        questions.add(new Question());
        tag.setQuestions(questions);

        // Then
        assertEquals(3, tag.getQuestions().size());
    }

    @Test
    @DisplayName("Should check if tag has questions")
    void hasQuestions() {
        // When - No questions
        assertTrue(tag.getQuestions() == null || tag.getQuestions().isEmpty());

        // Given - Add a question
        tag.setQuestions(new ArrayList<>());
        tag.getQuestions().add(new Question());

        // Then
        assertFalse(tag.getQuestions().isEmpty());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void toString_ReturnsTagInfo() {
        // Given
        tag.setId(15L);
        tag.setName("Database");

        // When
        String toString = tag.toString();

        // Then
        assertTrue(toString.contains("Database"));
    }

    @Test
    @DisplayName("Should create tag with programming language name")
    void createProgrammingLanguageTag() {
        // When
        tag.setName("Python");

        // Then
        assertEquals("Python", tag.getName());
    }

    @Test
    @DisplayName("Should create tag with framework name")
    void createFrameworkTag() {
        // When
        tag.setName("Spring Boot");

        // Then
        assertEquals("Spring Boot", tag.getName());
    }

    @Test
    @DisplayName("Should create tag with database name")
    void createDatabaseTag() {
        // When
        tag.setName("MySQL");

        // Then
        assertEquals("MySQL", tag.getName());
    }

    @Test
    @DisplayName("Should create tag with tool name")
    void createToolTag() {
        // When
        tag.setName("Git");

        // Then
        assertEquals("Git", tag.getName());
    }

    @Test
    @DisplayName("Should create tag with concept name")
    void createConceptTag() {
        // When
        tag.setName("Object-Oriented Programming");

        // Then
        assertEquals("Object-Oriented Programming", tag.getName());
    }

    @Test
    @DisplayName("Should handle tag name with spaces")
    void tagNameWithSpaces() {
        // When
        tag.setName("Web Development");

        // Then
        assertEquals("Web Development", tag.getName());
    }

    @Test
    @DisplayName("Should handle tag name with special characters")
    void tagNameWithSpecialCharacters() {
        // When
        tag.setName("C#");

        // Then
        assertEquals("C#", tag.getName());
    }

    @Test
    @DisplayName("Should handle tag name with numbers")
    void tagNameWithNumbers() {
        // When
        tag.setName("Java 8");

        // Then
        assertEquals("Java 8", tag.getName());
    }

    @Test
    @DisplayName("Should handle tag name with version")
    void tagNameWithVersion() {
        // When
        tag.setName("Spring 5.0");

        // Then
        assertEquals("Spring 5.0", tag.getName());
    }

    @Test
    @DisplayName("Should create tag and associate with multiple questions")
    void tagWithMultipleQuestions() {
        // Given
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Question q = new Question();
            q.setQuestionText("Question " + i + " about Java");
            questions.add(q);
        }

        // When
        tag.setName("Java");
        tag.setQuestions(questions);

        // Then
        assertEquals("Java", tag.getName());
        assertEquals(10, tag.getQuestions().size());
        for (int i = 0; i < 10; i++) {
            assertEquals("Question " + (i + 1) + " about Java",
                    tag.getQuestions().get(i).getQuestionText());
        }
    }

    @Test
    @DisplayName("Should remove question from tag")
    void removeQuestionFromTag() {
        // Given
        List<Question> questions = new ArrayList<>();
        Question q1 = new Question();
        q1.setQuestionText("Question 1");
        Question q2 = new Question();
        q2.setQuestionText("Question 2");
        questions.add(q1);
        questions.add(q2);

        tag.setQuestions(questions);

        // When
        tag.getQuestions().remove(q1);

        // Then
        assertEquals(1, tag.getQuestions().size());
        assertEquals("Question 2", tag.getQuestions().get(0).getQuestionText());
    }

    @Test
    @DisplayName("Should check if tag name is empty")
    void tagNameEmpty() {
        // When
        tag.setName("");

        // Then
        assertEquals("", tag.getName());
        assertTrue(tag.getName().isEmpty());
    }

    @Test
    @DisplayName("Should create tag with single character name")
    void tagNameSingleCharacter() {
        // When
        tag.setName("A");

        // Then
        assertEquals("A", tag.getName());
        assertEquals(1, tag.getName().length());
    }
}