package com.quizapp.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuizAttemptApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizAttemptService quizAttemptService;

    @Mock
    private UserService userService;

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizAttemptApiController quizAttemptApiController;

    private ObjectMapper objectMapper;
    private User user;
    private Quiz quiz;
    private QuizAttempt quizAttempt;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizAttemptApiController).build();
        objectMapper = new ObjectMapper();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");

        quizAttempt = new QuizAttempt();
        quizAttempt.setId(1L);
        quizAttempt.setUser(user);
        quizAttempt.setQuiz(quiz);
        quizAttempt.setScore(85.0);
    }

    @Test
    void startQuizAttempt_ShouldStartSuccessfully() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));
        when(quizAttemptService.startQuizAttempt(user, quiz)).thenReturn(quizAttempt);

        mockMvc.perform(post("/api/attempts/start/1")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk());

        verify(quizAttemptService, times(1)).startQuizAttempt(user, quiz);
    }
}