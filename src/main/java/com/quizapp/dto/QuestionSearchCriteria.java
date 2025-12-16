package com.quizapp.dto;

import com.quizapp.entity.DifficultyLevel;

import java.util.List;

public class QuestionSearchCriteria {
    private String searchTerm;
    private DifficultyLevel difficultyLevel;
    private List<String> tags;
    private Boolean isTemplate;

    // Constructors
    public QuestionSearchCriteria() {}

    public QuestionSearchCriteria(String searchTerm, DifficultyLevel difficultyLevel, List<String> tags, Boolean isTemplate) {
        this.searchTerm = searchTerm;
        this.difficultyLevel = difficultyLevel;
        this.tags = tags;
        this.isTemplate = isTemplate;
    }

    // Getters and Setters
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public DifficultyLevel getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
}