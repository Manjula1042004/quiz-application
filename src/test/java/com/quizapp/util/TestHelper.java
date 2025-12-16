package com.quizapp.util;

import com.quizapp.entity.DifficultyLevel;
import com.quizapp.entity.Question;
import com.quizapp.service.BulkImportService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestHelper {

    // This is an example of how to test private methods (not recommended)
    // But if you really need it, you can use reflection

    @Test
    void testParseQuestionFromCSVLine() throws Exception {
        // This is just an example - you might not need this
        BulkImportService service = new BulkImportService();

        // Use reflection to access private method
        Method method = BulkImportService.class.getDeclaredMethod("parseQuestionFromCSVLine", String.class);
        method.setAccessible(true);

        String csvLine = "What is capital of France?,Paris|London|Berlin|Madrid,0,EASY,Capital city question,10";
        Question question = (Question) method.invoke(service, csvLine);

        assertNotNull(question);
        assertEquals("What is capital of France?", question.getQuestionText());
        assertEquals(List.of("Paris", "London", "Berlin", "Madrid"), question.getOptions());
        assertEquals(0, question.getCorrectAnswerIndex());
        assertEquals(DifficultyLevel.EASY, question.getDifficultyLevel());
    }
}