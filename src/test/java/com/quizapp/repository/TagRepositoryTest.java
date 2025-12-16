package com.quizapp.repository;

import com.quizapp.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TagRepository Tests")
class TagRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Tag javaTag;
    private Tag springTag;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        // Create tags
        javaTag = new Tag("Java");
        entityManager.persist(javaTag);

        springTag = new Tag("Spring");
        entityManager.persist(springTag);

        // Create test question with tags
        testQuestion = new Question();
        testQuestion.setQuestionText("What is Java?");
        testQuestion.setOptions(Arrays.asList("Language", "Coffee", "Island", "All"));
        testQuestion.setCorrectAnswerIndex(0);
        testQuestion.setDifficultyLevel(DifficultyLevel.EASY);
        testQuestion.setPoints(1);
        testQuestion.setTags(Arrays.asList(javaTag));
        entityManager.persist(testQuestion);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find tag by name")
    void findByName_TagExists_ReturnsTag() {
        // When
        Optional<Tag> found = tagRepository.findByName("Java");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Java", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when tag name not found")
    void findByName_TagNotFound_ReturnsEmpty() {
        // When
        Optional<Tag> found = tagRepository.findByName("Python");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find tags by name containing (case insensitive)")
    void findByNameContainingIgnoreCase_ReturnsMatchingTags() {
        // Given - create more tags
        Tag javaScriptTag = new Tag("JavaScript");
        entityManager.persist(javaScriptTag);
        entityManager.flush();

        // When
        List<Tag> tags = tagRepository.findByNameContainingIgnoreCase("java");

        // Then
        assertEquals(2, tags.size()); // Java and JavaScript
        assertTrue(tags.stream().anyMatch(t -> t.getName().equals("Java")));
        assertTrue(tags.stream().anyMatch(t -> t.getName().equals("JavaScript")));
    }

    @Test
    @DisplayName("Should find all tags ordered by name")
    void findAllOrderByName_ReturnsOrderedTags() {
        // Given - create more tags
        Tag aTag = new Tag("A-Tag");
        Tag zTag = new Tag("Z-Tag");
        entityManager.persist(aTag);
        entityManager.persist(zTag);
        entityManager.flush();

        // When
        List<Tag> tags = tagRepository.findAllOrderByName();

        // Then
        assertTrue(tags.size() >= 4);
        // Check ordering
        for (int i = 0; i < tags.size() - 1; i++) {
            assertTrue(tags.get(i).getName().compareToIgnoreCase(tags.get(i + 1).getName()) <= 0);
        }
    }

    @Test
    @DisplayName("Should find tags by question ID")
    void findByQuestionId_ReturnsTags() {
        // When
        List<Tag> tags = tagRepository.findByQuestionId(testQuestion.getId());

        // Then
        assertEquals(1, tags.size());
        assertEquals("Java", tags.get(0).getName());
    }

    @Test
    @DisplayName("Should save new tag")
    void save_NewTag_SuccessfullySaved() {
        // Given
        Tag newTag = new Tag();
        newTag.setName("Python");

        // When
        Tag savedTag = tagRepository.save(newTag);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertNotNull(savedTag.getId());
        Tag foundTag = entityManager.find(Tag.class, savedTag.getId());
        assertEquals("Python", foundTag.getName());
        assertNotNull(foundTag.getQuestions());
        assertTrue(foundTag.getQuestions().isEmpty());
    }

    @Test
    @DisplayName("Should update tag")
    void save_UpdateTag_SuccessfullyUpdated() {
        // Given
        javaTag.setName("Java Programming");

        // When
        Tag updatedTag = tagRepository.save(javaTag);
        entityManager.flush();
        entityManager.clear();

        // Then
        Tag foundTag = entityManager.find(Tag.class, javaTag.getId());
        assertEquals("Java Programming", foundTag.getName());
    }

    @Test
    @DisplayName("Should delete tag")
    void delete_TagExists_SuccessfullyDeleted() {
        // When
        tagRepository.delete(javaTag);
        entityManager.flush();
        entityManager.clear();

        // Then
        Tag foundTag = entityManager.find(Tag.class, javaTag.getId());
        assertNull(foundTag);
    }

    @Test
    @DisplayName("Should not allow duplicate tag names")
    void save_DuplicateTagName_ThrowsException() {
        // Given
        Tag duplicateTag = new Tag("Java");

        // When & Then
        assertThrows(Exception.class, () -> {
            tagRepository.save(duplicateTag);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should maintain relationship with questions")
    void tagQuestionRelationship_ShouldWork() {
        // Given - create a new question with the tag
        Question newQuestion = new Question();
        newQuestion.setQuestionText("New question about Java");
        newQuestion.setOptions(Arrays.asList("A", "B", "C"));
        newQuestion.setCorrectAnswerIndex(0);
        newQuestion.setDifficultyLevel(DifficultyLevel.MEDIUM);
        newQuestion.setTags(Arrays.asList(javaTag));
        entityManager.persist(newQuestion);
        entityManager.flush();
        entityManager.clear();

        // When
        Tag foundTag = entityManager.find(Tag.class, javaTag.getId());
        List<Question> tagQuestions = foundTag.getQuestions();

        // Then
        assertEquals(2, tagQuestions.size());
        assertTrue(tagQuestions.stream().anyMatch(q -> q.getQuestionText().contains("Java")));
    }
}