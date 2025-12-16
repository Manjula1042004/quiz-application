package com.quizapp.dto;

import com.quizapp.entity.DifficultyLevel;

public class QuestionUpdateRequest {
    private DifficultyLevel difficultyLevel;
    private Integer points;

    // Constructors
    public QuestionUpdateRequest() {}

    public QuestionUpdateRequest(DifficultyLevel difficultyLevel, Integer points) {
        this.difficultyLevel = difficultyLevel;
        this.points = points;
    }

    // Getters and Setters
    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}