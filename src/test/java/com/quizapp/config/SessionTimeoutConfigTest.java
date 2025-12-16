package com.quizapp.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import jakarta.servlet.http.HttpSessionListener;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SessionTimeoutConfig Tests")
class SessionTimeoutConfigTest {

    private SessionTimeoutConfig sessionTimeoutConfig;

    @BeforeEach
    void setUp() {
        sessionTimeoutConfig = new SessionTimeoutConfig();
    }

    @Test
    @DisplayName("Should create HttpSessionEventPublisher bean")
    void httpSessionEventPublisher_ShouldCreateBean() {
        // When
        HttpSessionEventPublisher publisher = sessionTimeoutConfig.httpSessionEventPublisher();

        // Then
        assertNotNull(publisher);
    }

    @Test
    @DisplayName("Should create session listener registration bean")
    void sessionListener_ShouldCreateRegistrationBean() {
        // When
        ServletListenerRegistrationBean<HttpSessionListener> registrationBean =
                sessionTimeoutConfig.sessionListener();

        // Then
        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getListener());
    }
}