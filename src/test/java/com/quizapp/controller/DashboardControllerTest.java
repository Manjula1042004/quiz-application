package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import com.quizapp.service.QuizAttemptService;
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private QuizService quizService;

    @Mock
    private QuizAttemptService quizAttemptService;

    @InjectMocks
    private DashboardController dashboardController;

    private User adminUser;
    private User participantUser;
    private Quiz quiz;
    private QuizAttempt quizAttempt;
    private UserDetails adminUserDetails;
    private UserDetails participantUserDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();

        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        participantUser = new User();
        participantUser.setId(2L);
        participantUser.setUsername("participant");
        participantUser.setRole(Role.PARTICIPANT);

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setIsPublic(true);
        quiz.setEnabled(true);

        quizAttempt = new QuizAttempt();
        quizAttempt.setId(1L);
        quizAttempt.setUser(participantUser);
        quizAttempt.setQuiz(quiz);
        quizAttempt.setScore(85.0);

        adminUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("admin")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        participantUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("participant")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void dashboard_ShouldRedirectToLoginWhenNoUser() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void dashboard_ShouldShowAdminDashboard() throws Exception {
        when(userService.findByUsername("admin")).thenReturn(java.util.Optional.of(adminUser));
        when(quizService.getAllQuizzes()).thenReturn(Arrays.asList(quiz));
        when(userService.findAllParticipants()).thenReturn(Arrays.asList(participantUser));

        mockMvc.perform(get("/dashboard")
                        .principal(() -> "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("user", "quizzes", "quizCount", "participantCount"));

        verify(quizService, times(1)).getAllQuizzes();
    }

    @Test
    void dashboard_ShouldShowParticipantDashboard() throws Exception {
        when(userService.findByUsername("participant")).thenReturn(java.util.Optional.of(participantUser));
        when(quizAttemptService.getUserAttempts(2L)).thenReturn(Arrays.asList(quizAttempt));
        when(quizService.getPublicQuizzes()).thenReturn(Arrays.asList(quiz));

        mockMvc.perform(get("/dashboard")
                        .principal(() -> "participant"))
                .andExpect(status().isOk())
                .andExpect(view().name("participant/dashboard"))
                .andExpect(model().attributeExists("user", "attempts", "quizzes"));

        verify(quizService, times(1)).getPublicQuizzes();
    }

    @Test
    void adminDashboard_ShouldShowAdminDashboard() throws Exception {
        when(userService.findByUsername("admin")).thenReturn(java.util.Optional.of(adminUser));
        when(quizService.getAllQuizzes()).thenReturn(Arrays.asList(quiz));
        when(userService.findAllParticipants()).thenReturn(Arrays.asList(participantUser));

        mockMvc.perform(get("/admin/dashboard")
                        .principal(() -> "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));

        verify(userService, times(1)).findByUsername("admin");
    }

    @Test
    void adminDashboard_ShouldRedirectNonAdmin() throws Exception {
        when(userService.findByUsername("participant")).thenReturn(java.util.Optional.of(participantUser));

        mockMvc.perform(get("/admin/dashboard")
                        .principal(() -> "participant"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void participantView_ShouldShowParticipantViewForAdmin() throws Exception {
        when(userService.findByUsername("admin")).thenReturn(java.util.Optional.of(adminUser));
        when(quizAttemptService.getUserAttempts(1L)).thenReturn(Arrays.asList(quizAttempt));
        when(quizService.getAllQuizzes()).thenReturn(Arrays.asList(quiz));

        mockMvc.perform(get("/participant-view")
                        .principal(() -> "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("participant/dashboard"))
                .andExpect(model().attributeExists("isAdminView"));

        verify(quizService, times(1)).getAllQuizzes();
    }

    @Test
    void dashboard_ShouldHandleUserNotFound() throws Exception {
        when(userService.findByUsername("unknown")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/dashboard")
                        .principal(() -> "unknown"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void dashboard_ShouldHandleServiceException() throws Exception {
        when(userService.findByUsername("admin")).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/dashboard")
                        .principal(() -> "admin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}