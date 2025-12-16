package com.quizapp.service;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DebugServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private DebugService debugService;

    @Test
    void debugQuizData_QuizFound() {
        // Arrange
        Quiz quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");

        Question question1 = new Question();
        question1.setId(1L);
        question1.setQuestionText("Question 1");

        Question question2 = new Question();
        question2.setId(2L);
        question2.setQuestionText("Question 2");

        quiz.setQuestions(Arrays.asList(question1, question2));

        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(questionRepository.findByQuizId(1L)).thenReturn(Arrays.asList(question1, question2));

        // Act
        debugService.debugQuizData(1L);

        // Assert
        verify(quizRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).findByQuizId(1L);
    }

    @Test
    void debugQuizData_QuizNotFound() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        debugService.debugQuizData(1L);

        // Assert
        verify(quizRepository, times(1)).findById(1L);
        verify(questionRepository, never()).findByQuizId(any());
    }
}