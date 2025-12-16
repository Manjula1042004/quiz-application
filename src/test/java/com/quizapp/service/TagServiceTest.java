package com.quizapp.service;

import com.quizapp.entity.Tag;
import com.quizapp.repository.TagRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("Mathematics");
    }

    @Test
    void createOrGetTag_NewTag() {
        // Arrange
        when(tagRepository.findByName("Mathematics")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // Act
        Tag result = tagService.createOrGetTag("Mathematics");

        // Assert
        assertNotNull(result);
        assertEquals("Mathematics", result.getName());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void createOrGetTag_ExistingTag() {
        // Arrange
        when(tagRepository.findByName("Mathematics")).thenReturn(Optional.of(tag));

        // Act
        Tag result = tagService.createOrGetTag("Mathematics");

        // Assert
        assertNotNull(result);
        assertEquals("Mathematics", result.getName());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void createOrGetTags() {
        // Arrange
        List<String> tagNames = Arrays.asList("Mathematics", "Science", "History");
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // Act
        List<Tag> result = tagService.createOrGetTags(tagNames);

        // Assert
        assertEquals(3, result.size());
        verify(tagRepository, times(3)).save(any(Tag.class));
    }

    @Test
    void createOrGetTags_WithEmptyNames() {
        // Arrange
        List<String> tagNames = Arrays.asList("Mathematics", "", "  ", "Science");

        // Act
        List<Tag> result = tagService.createOrGetTags(tagNames);

        // Assert
        assertEquals(2, result.size()); // Only non-empty names
    }

    @Test
    void getAllTags() {
        // Arrange
        List<Tag> tags = Arrays.asList(tag);
        when(tagRepository.findAllOrderByName()).thenReturn(tags);

        // Act
        List<Tag> result = tagService.getAllTags();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Mathematics", result.get(0).getName());
    }

    @Test
    void searchTags() {
        // Arrange
        List<Tag> tags = Arrays.asList(tag);
        when(tagRepository.findByNameContainingIgnoreCase("math")).thenReturn(tags);

        // Act
        List<Tag> result = tagService.searchTags("math");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().toLowerCase().contains("math"));
    }

    @Test
    void deleteTag_Success() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        doNothing().when(tagRepository).delete(tag);

        // Act
        tagService.deleteTag(1L);

        // Assert
        verify(tagRepository, times(1)).delete(tag);
    }

    @Test
    void deleteTag_NotFound() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            tagService.deleteTag(1L);
        });
    }

    @Test
    void getTagById_Found() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        // Act
        Optional<Tag> result = tagService.getTagById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Mathematics", result.get().getName());
    }

    @Test
    void getTagById_NotFound() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Tag> result = tagService.getTagById(1L);

        // Assert
        assertFalse(result.isPresent());
    }
}