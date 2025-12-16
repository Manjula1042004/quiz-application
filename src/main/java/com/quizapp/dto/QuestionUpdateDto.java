// File: src/main/java/com/quizapp/dto/QuestionUpdateDto.java
package com.quizapp.dto;

import com.quizapp.entity.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class QuestionUpdateDto {
    private Long id;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @Size(min = 2, message = "At least 2 options are required")
    private List<@NotBlank String> options = new ArrayList<>();

    @NotNull(message = "Correct answer index is required")
    private Integer correctAnswerIndex;

    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;
    private String explanation;
    private Integer points = 1;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Integer getCorrectAnswerIndex() { return correctAnswerIndex; }
    public void setCorrectAnswerIndex(Integer correctAnswerIndex) { this.correctAnswerIndex = correctAnswerIndex; }

    public DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
}