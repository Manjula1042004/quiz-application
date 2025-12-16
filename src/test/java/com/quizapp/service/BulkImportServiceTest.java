package com.quizapp.service;

import com.quizapp.entity.DifficultyLevel;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulkImportServiceTest {

    @Mock
    private QuestionService questionService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private BulkImportService bulkImportService;

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
    }

    @Test
    void importQuestionsFromCSV_Success() throws Exception {
        // Arrange
        String csvContent = "Question Text,Option1|Option2|Option3|Option4,1,MEDIUM,Explanation,10\n" +
                "What is 2+2?,3|4|5|6,1,EASY,Basic math,5";

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);
        when(questionService.createQuestion(any(Question.class))).thenReturn(new Question());

        // Act
        BulkImportService.BulkImportResult result = bulkImportService.importQuestionsFromCSV(file, quiz);

        // Assert
        assertEquals(2, result.getSuccessCount());
        assertTrue(result.getErrors().isEmpty());
        verify(questionService, times(2)).createQuestion(any(Question.class));
    }

    @Test
    void importQuestionsFromCSV_WithErrors() throws Exception {
        // Arrange
        String csvContent = "Question Text,Option1,1,MEDIUM,Explanation,10\n" + // Only 1 option
                "What is 2+2?,3|4|5|6,5,EASY,Basic math,5"; // Invalid correct index

        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        // Act
        BulkImportService.BulkImportResult result = bulkImportService.importQuestionsFromCSV(file, quiz);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertEquals(2, result.getErrors().size());
        verify(questionService, never()).createQuestion(any(Question.class));
    }

    @Test
    void importQuestionsFromCSV_EmptyFile() throws Exception {
        // Arrange
        String csvContent = "";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes());
        when(file.getInputStream()).thenReturn(inputStream);

        // Act
        BulkImportService.BulkImportResult result = bulkImportService.importQuestionsFromCSV(file, quiz);

        // Assert
        assertEquals(0, result.getSuccessCount());
        assertTrue(result.getErrors().isEmpty());
    }



    // REMOVE tests for private parseQuestionFromCSVLine method
    // We can't test private methods directly

    @Test
    void bulkImportResult_TestGettersAndSetters() {
        // Arrange
        BulkImportService.BulkImportResult result = new BulkImportService.BulkImportResult();

        // Act
        result.incrementSuccessCount();
        result.addError("Test error");

        // Assert
        assertEquals(1, result.getSuccessCount());
        assertEquals(1, result.getErrors().size());
        assertEquals("Test error", result.getErrors().get(0));
        assertTrue(result.hasErrors());
    }
}