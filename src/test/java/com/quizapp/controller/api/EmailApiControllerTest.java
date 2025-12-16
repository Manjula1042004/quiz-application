package com.quizapp.controller.api;

import com.quizapp.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmailApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailApiController emailApiController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailApiController).build();
    }

    @Test
    void sendTestEmail_ShouldSendSuccessfully() throws Exception {
        String email = "test@example.com";
        String testType = "welcome";

        doNothing().when(emailService).sendTestEmail(email, testType);

        mockMvc.perform(post("/api/email/test")
                        .param("email", email)
                        .param("testType", testType))
                .andExpect(status().isOk());

        verify(emailService, times(1)).sendTestEmail(email, testType);
    }
}