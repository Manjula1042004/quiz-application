package com.quizapp.service;

import com.quizapp.entity.*;
import com.quizapp.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TagService tagService;

    public Question createQuestion(Question question) {
        // Ensure options are properly initialized
        if (question.getOptions() == null) {
            question.setOptions(new ArrayList<>());
        }
        // Ensure tags are properly initialized
        if (question.getTags() == null) {
            question.setTags(new ArrayList<>());
        }
        return questionRepository.save(question);
    }

    public Question createQuestionWithTags(Question question, List<String> tagNames) {
        if (tagNames != null && !tagNames.isEmpty()) {
            List<Tag> tags = tagService.createOrGetTags(tagNames);
            question.setTags(tags);
        } else {
            question.setTags(new ArrayList<>());
        }

        // Ensure options are properly initialized
        if (question.getOptions() == null) {
            question.setOptions(new ArrayList<>());
        }

        return questionRepository.save(question);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Transactional(readOnly = true)
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        question.setQuestionText(questionDetails.getQuestionText());
        question.setOptions(questionDetails.getOptions() != null ? questionDetails.getOptions() : new ArrayList<>());
        question.setCorrectAnswerIndex(questionDetails.getCorrectAnswerIndex());
        question.setDifficultyLevel(questionDetails.getDifficultyLevel());
        question.setExplanation(questionDetails.getExplanation());
        question.setPoints(questionDetails.getPoints());

        return questionRepository.save(question);
    }

    public Question updateQuestionTags(Long questionId, List<String> tagNames) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        List<Tag> tags = tagService.createOrGetTags(tagNames);
        question.setTags(tags);

        return questionRepository.save(question);
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        questionRepository.delete(question);
    }

    // NEW: Delete question from specific quiz
    public void deleteQuestionFromQuiz(Long quizId, Long questionId) {
        // Use the repository method we added
        questionRepository.deleteByQuizIdAndQuestionId(quizId, questionId);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByDifficulty(DifficultyLevel difficultyLevel) {
        return questionRepository.findByDifficultyLevel(difficultyLevel);
    }

    @Transactional(readOnly = true)
    public List<Question> getTemplateQuestions() {
        return questionRepository.findTemplateQuestions();
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionBankQuestions() {
        return questionRepository.findQuestionBankQuestions();
    }

    @Transactional(readOnly = true)
    public List<Question> searchQuestions(String searchTerm) {
        return questionRepository.searchByQuestionText(searchTerm);
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsByTag(String tagName) {
        return questionRepository.findByTagName(tagName);
    }

    public Question convertToTemplate(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        question.setIsTemplate(true);
        question.setQuiz(null); // Remove from any quiz
        return questionRepository.save(question);
    }

    public Question addToQuizFromTemplate(Long templateId, Quiz quiz) {
        Question template = questionRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template question not found"));

        // Create a copy of the template question
        Question newQuestion = new Question();
        newQuestion.setQuestionText(template.getQuestionText());
        newQuestion.setOptions(new ArrayList<>(template.getOptions()));
        newQuestion.setCorrectAnswerIndex(template.getCorrectAnswerIndex());
        newQuestion.setDifficultyLevel(template.getDifficultyLevel());
        newQuestion.setExplanation(template.getExplanation());
        newQuestion.setPoints(template.getPoints());
        newQuestion.setTags(new ArrayList<>(template.getTags()));
        newQuestion.setQuiz(quiz);
        newQuestion.setIsTemplate(false);

        return questionRepository.save(newQuestion);
    }
}