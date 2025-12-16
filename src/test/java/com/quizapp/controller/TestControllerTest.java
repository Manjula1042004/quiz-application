package com.quizapp.controller;

import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import com.quizapp.repository.UserRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.service.QuizService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private QuizService quizService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private TestController testController;

    private User user;
    private Quiz quiz;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(testController).build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setRole(Role.ADMIN);

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setIsPublic(true);
        quiz.setEnabled(true);
        quiz.setIsTemplate(false);
    }

    @Test
    void getAllUsers_ShouldReturnUsers() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/test/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

        verify(userService, times(1)).findAllUsers();
    }

    @Test
    void enableUser_ShouldEnableSuccessfully() throws Exception {
        doNothing().when(userService).enableUserByUsername("testuser");

        mockMvc.perform(get("/api/test/enable-user/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ User testuser enabled successfully"));

        verify(userService, times(1)).enableUserByUsername("testuser");
    }

    @Test
    void enableUser_ShouldHandleError() throws Exception {
        doThrow(new RuntimeException("User not found"))
                .when(userService).enableUserByUsername("testuser");

        mockMvc.perform(get("/api/test/enable-user/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("❌ Error: User not found"));
    }

    @Test
    void debugQuizzes_ShouldReturnDebugInfo() throws Exception {
        List<Quiz> allQuizzes = Arrays.asList(quiz);
        List<Quiz> publicQuizzes = Arrays.asList(quiz);

        when(quizService.getAllQuizzes()).thenReturn(allQuizzes);
        when(quizService.getPublicQuizzes()).thenReturn(publicQuizzes);

        mockMvc.perform(get("/api/debug/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allCount").value(1))
                .andExpect(jsonPath("$.publicCount").value(1))
                .andExpect(jsonPath("$.allQuizzes[0].title").value("Test Quiz"));

        verify(quizService, times(1)).getAllQuizzes();
        verify(quizService, times(1)).getPublicQuizzes();
    }

    @Test
    void createTestQuizzes_ShouldCreateWhenUsersExist() throws Exception {
        List<User> users = Arrays.asList(user);
        when(userRepository.findAll()).thenReturn(users);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);
        when(quizRepository.count()).thenReturn(1L);

        mockMvc.perform(get("/api/create-test-quizzes"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Created test quiz! Total quizzes now: 1"));

        verify(userRepository, times(1)).findAll();
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void createTestQuizzes_ShouldHandleNoUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/create-test-quizzes"))
                .andExpect(status().isOk())
                .andExpect(content().string("❌ No users found. Please register first."));
    }

    @Test
    void makeAllQuizzesPublic_ShouldUpdateQuizzes() throws Exception {
        List<Quiz> quizzes = Arrays.asList(quiz);
        when(quizRepository.findAll()).thenReturn(quizzes);
        when(quizRepository.save(any(Quiz.class))).thenReturn(quiz);

        mockMvc.perform(get("/api/fix/all-quizzes-public"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Fixed 1 quizzes to be public"));

        verify(quizRepository, times(1)).findAll();
    }
}