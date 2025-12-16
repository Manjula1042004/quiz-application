package com.quizapp.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringToListConverter Tests")
class StringToListConverterTest {

    private StringToListConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringToListConverter();
    }

    @Test
    @DisplayName("Should convert comma separated string to list")
    void convert_WithValidCommaSeparatedString_ReturnsList() {
        // Given
        String input = "item1,item2,item3";

        // When
        List<String> result = converter.convert(input);

        // Then
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("item1", "item2", "item3"), result);
    }

    @Test
    @DisplayName("Should trim whitespace from each item")
    void convert_WithSpacesAroundCommas_ReturnsTrimmedList() {
        // Given
        String input = "  item1 , item2  ,  item3 ";

        // When
        List<String> result = converter.convert(input);

        // Then
        assertEquals(3, result.size());
        assertEquals("item1", result.get(0));
        assertEquals("item2", result.get(1));
        assertEquals("item3", result.get(2));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("Should return empty list for null, empty or blank strings")
    void convert_WithNullOrEmptyOrBlankString_ReturnsEmptyList(String input) {
        // When
        List<String> result = converter.convert(input);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter out empty items after splitting")
    void convert_WithMultipleConsecutiveCommas_ReturnsListWithoutEmptyItems() {
        // Given
        String input = "item1,,item2,,,item3";

        // When
        List<String> result = converter.convert(input);

        // Then
        assertEquals(3, result.size());
        assertEquals(Arrays.asList("item1", "item2", "item3"), result);
    }

    @Test
    @DisplayName("Should handle single item without commas")
    void convert_WithSingleItem_ReturnsSingleItemList() {
        // Given
        String input = "singleItem";

        // When
        List<String> result = converter.convert(input);

        // Then
        assertEquals(1, result.size());
        assertEquals("singleItem", result.get(0));
    }

    @Test
    @DisplayName("Should maintain order of items")
    void convert_ShouldMaintainItemOrder() {
        // Given
        String input = "first,second,third,fourth";

        // When
        List<String> result = converter.convert(input);

        // Then
        assertEquals(4, result.size());
        assertEquals("first", result.get(0));
        assertEquals("second", result.get(1));
        assertEquals("third", result.get(2));
        assertEquals("fourth", result.get(3));
    }

    @Test
    @DisplayName("Should handle mixed content with spaces and no spaces")
    void convert_WithMixedContent_ReturnsCorrectList() {
        // Given
        String input = "noSpace, withSpace ,,multiple  spaces,trailingSpace ";

        // When
        List<String> result = converter.convert(input);

        // Then
        assertEquals(4, result.size());
        assertEquals("noSpace", result.get(0));
        assertEquals("withSpace", result.get(1));
        assertEquals("multiple  spaces", result.get(2));
        assertEquals("trailingSpace", result.get(3));
    }
}