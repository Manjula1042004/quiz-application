package com.quizapp.controller;

import com.quizapp.entity.*;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AttemptControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizAttemptService quizAttemptService;

    @Mock
    private QuizService quizService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AttemptController attemptController;

    private User user;
    private Quiz quiz;
    private QuizAttempt quizAttempt;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attemptController).build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");

        Question question = new Question();
        question.setId(1L);
        question.setQuestionText("Test Question");
        question.setOptions(Arrays.asList("Option 1", "Option 2", "Option 3"));
        question.setCorrectAnswerIndex(0);
        quiz.setQuestions(Arrays.asList(question));

        quizAttempt = new QuizAttempt();
        quizAttempt.setId(1L);
        quizAttempt.setUser(user);
        quizAttempt.setQuiz(quiz);
        quizAttempt.setScore(85.0);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void startQuizAttempt_ShouldRedirectToTakeQuiz() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));
        when(quizAttemptService.startQuizAttempt(user, quiz)).thenReturn(quizAttempt);

        mockMvc.perform(get("/attempt/start/1")
                        .principal(() -> "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attempt/take/1"));

        verify(userService, times(1)).findByUsername("testuser");
        verify(quizService, times(1)).getQuizById(1L);
        verify(quizAttemptService, times(1)).startQuizAttempt(user, quiz);
    }

    @Test
    void startQuizAttempt_ShouldHandleQuizNotFound() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(quizService.getQuizById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/attempt/start/1")
                        .principal(() -> "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/list"));
    }

    @Test
    void takeQuiz_ShouldShowQuizPage() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(quizAttemptService.getAttemptById(1L)).thenReturn(Optional.of(quizAttempt));

        mockMvc.perform(get("/attempt/take/1")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/take"));

        verify(userService, times(1)).findByUsername("testuser");
        verify(quizAttemptService, times(1)).getAttemptById(1L);
    }

    @Test
    void takeQuiz_ShouldDenyAccessWhenNotOwner() throws Exception {
        User differentUser = new User();
        differentUser.setId(2L);

        QuizAttempt differentAttempt = new QuizAttempt();
        differentAttempt.setId(1L);
        differentAttempt.setUser(differentUser);
        differentAttempt.setQuiz(quiz);

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(quizAttemptService.getAttemptById(1L)).thenReturn(Optional.of(differentAttempt));

        mockMvc.perform(get("/attempt/take/1")
                        .principal(() -> "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void submitQuizAttempt_ShouldSubmitSuccessfully() throws Exception {
        Map<Long, Integer> answers = new HashMap<>();
        answers.put(1L, 0);

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(quizAttemptService.submitQuiz(eq(1L), anyMap())).thenReturn(quizAttempt);

        mockMvc.perform(post("/attempt/submit")
                        .param("attemptId", "1")
                        .param("quizId", "1")
                        .param("answers[1]", "0")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/results"))
                .andExpect(model().attributeExists("attempt", "quiz", "score", "totalQuestions", "success"));

        verify(quizAttemptService, times(1)).submitQuiz(eq(1L), anyMap());
    }

    @Test
    void viewQuizResults_ShouldShowResults() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(quizAttemptService.getAttemptById(1L)).thenReturn(Optional.of(quizAttempt));

        mockMvc.perform(get("/attempt/results/1")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/results"))
                .andExpect(model().attributeExists("attempt", "quiz", "score", "totalQuestions"));

        verify(quizAttemptService, times(1)).getAttemptById(1L);
    }
}