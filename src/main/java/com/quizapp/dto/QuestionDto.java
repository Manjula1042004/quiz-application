package com.quizapp.dto;

import com.quizapp.entity.DifficultyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class QuestionDto {
    private Long id;

    @NotBlank(message = "Question text is required")
    private String questionText;

    private List<String> options = new ArrayList<>();

    @NotNull(message = "Correct answer index is required")
    private Integer correctAnswerIndex;

    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    private String explanation;

    private Integer points = 1;

    private List<String> tags = new ArrayList<>();

    private Boolean isTemplate = false;

    public QuestionDto() {}

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
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel != null ? difficultyLevel : DifficultyLevel.MEDIUM;
    }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points != null ? points : 1; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getIsTemplate() { return isTemplate; }
    public void setIsTemplate(Boolean isTemplate) { this.isTemplate = isTemplate; }
}