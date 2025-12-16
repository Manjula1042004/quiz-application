package com.quizapp.service;

import com.quizapp.entity.DifficultyLevel;
import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BulkImportService {

    @Autowired
    private QuestionService questionService;

    public BulkImportResult importQuestionsFromCSV(MultipartFile file, Quiz quiz) {
        BulkImportResult result = new BulkImportResult();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header row
                }

                try {
                    Question question = parseQuestionFromCSVLine(line);
                    if (question != null) {
                        question.setQuiz(quiz);
                        questionService.createQuestion(question);
                        result.incrementSuccessCount();
                    }
                } catch (Exception e) {
                    result.addError("Line: " + line + " - Error: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            result.addError("File processing error: " + e.getMessage());
        }

        return result;
    }

    private Question parseQuestionFromCSVLine(String line) {
        // Simple CSV parsing - split by comma but handle quoted fields
        List<String> fields = parseCSVLine(line);

        if (fields.size() < 4) {
            throw new RuntimeException("Invalid CSV format. Expected: question,options,correct_index,difficulty,explanation,points");
        }

        Question question = new Question();

        // Required fields
        question.setQuestionText(fields.get(0));

        // Options (pipe-separated)
        String optionsStr = fields.get(1);
        String[] options = optionsStr.split("\\|");
        if (options.length < 2) {
            throw new RuntimeException("At least 2 options required");
        }
        question.setOptions(Arrays.asList(options));

        // Correct answer index
        try {
            int correctIndex = Integer.parseInt(fields.get(2));
            if (correctIndex < 0 || correctIndex >= options.length) {
                throw new RuntimeException("Correct answer index out of range");
            }
            question.setCorrectAnswerIndex(correctIndex);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid correct answer index");
        }

        // Optional fields
        if (fields.size() > 3 && !fields.get(3).isEmpty()) {
            try {
                question.setDifficultyLevel(DifficultyLevel.valueOf(fields.get(3).toUpperCase()));
            } catch (IllegalArgumentException e) {
                question.setDifficultyLevel(DifficultyLevel.MEDIUM);
            }
        }

        if (fields.size() > 4 && !fields.get(4).isEmpty()) {
            question.setExplanation(fields.get(4));
        }

        if (fields.size() > 5 && !fields.get(5).isEmpty()) {
            try {
                question.setPoints(Integer.parseInt(fields.get(5)));
            } catch (NumberFormatException e) {
                question.setPoints(1);
            }
        }

        return question;
    }

    private List<String> parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }

        fields.add(field.toString().trim());
        return fields;
    }

    public static class BulkImportResult {
        private int successCount = 0;
        private List<String> errors = new ArrayList<>();

        public void incrementSuccessCount() { successCount++; }
        public void addError(String error) { errors.add(error); }

        // Getters
        public int getSuccessCount() { return successCount; }
        public List<String> getErrors() { return errors; }
        public boolean hasErrors() { return !errors.isEmpty(); }
    }
}