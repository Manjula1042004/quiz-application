package com.quizapp.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.FormatterRegistry;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebConfig Tests")
class WebConfigTest {

    @Mock
    private StringToListConverter stringToListConverter;

    @Mock
    private FormatterRegistry formatterRegistry;

    @InjectMocks
    private WebConfig webConfig;

    @Test
    @DisplayName("Should add StringToListConverter to formatter registry")
    void addFormatters_ShouldRegisterConverter() {
        // When
        webConfig.addFormatters(formatterRegistry);

        // Then
        verify(formatterRegistry, times(1)).addConverter(stringToListConverter);
        verifyNoMoreInteractions(formatterRegistry);
    }
}