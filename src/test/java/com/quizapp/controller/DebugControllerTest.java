package com.quizapp.controller;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.service.DebugService;
import com.quizapp.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DebugControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizService quizService;

    @Mock
    private DebugService debugService;

    @InjectMocks
    private DebugController debugController;

    private Quiz quiz;
    private Question question;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(debugController).build();

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Debug Quiz");

        question = new Question();
        question.setId(1L);
        question.setQuestionText("What is 2+2?");
        question.setOptions(Arrays.asList("3", "4", "5", "6"));

        quiz.setQuestions(Arrays.asList(question));
    }

    @Test
    void debugQuiz_ShouldShowDebugInfo() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));

        mockMvc.perform(get("/debug/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/take"))
                .andExpect(model().attributeExists("quiz", "debug"));

        verify(quizService, times(1)).getQuizById(1L);
    }

    @Test
    void debugQuiz_ShouldHandleQuizNotFound() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/debug/quiz/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"))
                .andExpect(flash().attributeExists("error"));
    }
}