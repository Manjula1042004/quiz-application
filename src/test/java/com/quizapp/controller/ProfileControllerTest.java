package com.quizapp.controller;

import com.quizapp.entity.User;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProfileController profileController;

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setEnabled(true);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void showProfile_ShouldDisplayProfile() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/profile")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/view"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).findByUsername("testuser");
    }

    @Test
    void showProfile_ShouldHandleUserNotFound() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

        mockMvc.perform(get("/profile")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/view"))
                .andExpect(model().attributeExists("user", "error"));
    }

    @Test
    void showEditProfileForm_ShouldDisplayEditForm() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/profile/edit")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void updateProfile_ShouldUpdateSuccessfully() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userService.saveUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/profile/update")
                        .param("email", "new@example.com")
                        .principal(() -> "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("success"));

        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void updateProfile_ShouldValidateEmail() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/profile/update")
                        .param("email", "")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void updateProfile_ShouldRejectDuplicateEmail() throws Exception {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("existing@example.com");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.findByEmail("existing@example.com")).thenReturn(Optional.of(otherUser));

        mockMvc.perform(post("/profile/update")
                        .param("email", "existing@example.com")
                        .principal(() -> "testuser"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/edit"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void showChangePasswordForm_ShouldDisplayForm() throws Exception {
        mockMvc.perform(get("/profile/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/change-password"));
    }

    @Test
    void changePassword_ShouldChangeSuccessfully() throws Exception {
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(userService).updatePassword(eq(1L), anyString());

        mockMvc.perform(post("/profile/change-password")
                        .param("currentPassword", "oldpass")
                        .param("newPassword", "newpass123")
                        .param("confirmPassword", "newpass123")
                        .principal(() -> "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("success"));

        verify(userService, times(1)).updatePassword(eq(1L), anyString());
    }

    @Test
    void changePassword_ShouldValidatePasswordMatch() throws Exception {
        mockMvc.perform(post("/profile/change-password")
                        .param("currentPassword", "oldpass")
                        .param("newPassword", "newpass123")
                        .param("confirmPassword", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/change-password"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void changePassword_ShouldValidatePasswordLength() throws Exception {
        mockMvc.perform(post("/profile/change-password")
                        .param("currentPassword", "oldpass")
                        .param("newPassword", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/change-password"))
                .andExpect(model().attributeExists("error"));
    }
}