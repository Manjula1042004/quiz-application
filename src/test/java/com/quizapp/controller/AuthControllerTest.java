package com.quizapp.controller;

import com.quizapp.dto.UserRegistrationDto;
import com.quizapp.entity.User;
import com.quizapp.security.PasswordValidator;
import com.quizapp.service.EmailVerificationService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthController authController;

    private UserRegistrationDto registrationDto;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("Password123!");
        registrationDto.setConfirmPassword("Password123!");
        registrationDto.setRole("PARTICIPANT");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setEnabled(true);
    }

    @Test
    void home_ShouldReturnIndex() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void showRegistrationForm_ShouldDisplayForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void showLoginForm_ShouldDisplayLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"));
    }

    @Test
    void showLoginForm_ShouldShowErrorMessage() throws Exception {
        mockMvc.perform(get("/login")
                        .param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void showLoginForm_ShouldShowSuccessMessage() throws Exception {
        mockMvc.perform(get("/login")
                        .param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))  // FIXED: Changed .expect() to .andExpect()
                .andExpect(model().attributeExists("success"));
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() throws Exception {
        // Mock password validation
        PasswordValidator.PasswordValidationResult validationResult =
                mock(PasswordValidator.PasswordValidationResult.class);
        when(validationResult.isValid()).thenReturn(true);

        when(userService.registerUser(anyString(), anyString(), anyString(), any()))
                .thenReturn(user);
        doNothing().when(emailVerificationService).createVerificationToken(any(User.class), any(UserService.class));

        mockMvc.perform(post("/register")
                        .flashAttr("user", registrationDto))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("success"));

        verify(userService, times(1)).registerUser(anyString(), anyString(), anyString(), any());
    }



    // ... rest of your test methods ...
}