package com.quizapp.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.dto.AuthRequest;
import com.quizapp.dto.UserRegistrationDto;
import com.quizapp.entity.User;
import com.quizapp.entity.Role;
import com.quizapp.security.JwtUtil;
import com.quizapp.service.CustomUserDetailsService;
import com.quizapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthApiController authApiController;

    private ObjectMapper objectMapper;
    private UserRegistrationDto registrationDto;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authApiController).build();
        objectMapper = new ObjectMapper();

        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testuser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("Password123!");
        registrationDto.setConfirmPassword("Password123!");
        registrationDto.setRole("PARTICIPANT");

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("Password123!");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Role.PARTICIPANT);
    }

    @Test
    void register_ShouldRegisterSuccessfully() throws Exception {
        when(userService.registerUser(anyString(), anyString(), anyString(), any()))
                .thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk());

        verify(userService, times(1)).registerUser(anyString(), anyString(), anyString(), any());
    }
}