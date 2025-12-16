package com.quizapp.controller;

import com.quizapp.service.PasswordResetService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private UserService userService;

    @InjectMocks
    private PasswordResetController passwordResetController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordResetController).build();
    }

    @Test
    void showForgotPasswordForm_ShouldDisplayForm() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"));
    }

    @Test
    void processForgotPassword_ShouldProcessSuccessfully() throws Exception {
        when(passwordResetService.createPasswordResetToken("test@example.com"))
                .thenReturn("Reset email sent successfully");

        mockMvc.perform(post("/forgot-password")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"))
                .andExpect(model().attributeExists("message"));

        verify(passwordResetService, times(1)).createPasswordResetToken("test@example.com");
    }

    @Test
    void processForgotPassword_ShouldHandleError() throws Exception {
        when(passwordResetService.createPasswordResetToken("test@example.com"))
                .thenThrow(new RuntimeException("Email not found"));

        mockMvc.perform(post("/forgot-password")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void showResetPasswordForm_ShouldShowFormForValidToken() throws Exception {
        when(passwordResetService.validatePasswordResetToken("valid-token"))
                .thenReturn("valid");

        mockMvc.perform(get("/reset-password")
                        .param("token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attributeExists("token"));
    }

    @Test
    void showResetPasswordForm_ShouldShowErrorForInvalidToken() throws Exception {
        when(passwordResetService.validatePasswordResetToken("invalid-token"))
                .thenReturn("Invalid token");

        mockMvc.perform(get("/reset-password")
                        .param("token", "invalid-token"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password-error"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void processResetPassword_ShouldResetSuccessfully() throws Exception {
        when(passwordResetService.resetPassword("valid-token", "newpassword123"))
                .thenReturn("Password reset successfully");

        mockMvc.perform(post("/reset-password")
                        .param("token", "valid-token")
                        .param("password", "newpassword123")
                        .param("confirmPassword", "newpassword123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password-success"))
                .andExpect(model().attributeExists("success"));

        verify(passwordResetService, times(1)).resetPassword("valid-token", "newpassword123");
    }

    @Test
    void processResetPassword_ShouldValidatePasswordMatch() throws Exception {
        mockMvc.perform(post("/reset-password")
                        .param("token", "valid-token")
                        .param("password", "newpassword123")
                        .param("confirmPassword", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attributeExists("error", "token"));
    }

    @Test
    void processResetPassword_ShouldValidatePasswordLength() throws Exception {
        mockMvc.perform(post("/reset-password")
                        .param("token", "valid-token")
                        .param("password", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password"))
                .andExpect(model().attributeExists("error", "token"));
    }

    @Test
    void processResetPassword_ShouldHandleServiceError() throws Exception {
        when(passwordResetService.resetPassword("invalid-token", "newpassword123"))
                .thenReturn("Invalid or expired token");

        mockMvc.perform(post("/reset-password")
                        .param("token", "invalid-token")
                        .param("password", "newpassword123")
                        .param("confirmPassword", "newpassword123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/reset-password-error"))
                .andExpect(model().attributeExists("error"));
    }
}