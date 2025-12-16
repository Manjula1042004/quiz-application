package com.quizapp.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import jakarta.servlet.http.HttpSessionListener;

@Configuration
public class SessionTimeoutConfig {

    private static final int SESSION_TIMEOUT_SECONDS = 1800; // 30 minutes

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener() {
        return new ServletListenerRegistrationBean<>(new HttpSessionListener() {
            @Override
            public void sessionCreated(jakarta.servlet.http.HttpSessionEvent se) {
                se.getSession().setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
            }

            @Override
            public void sessionDestroyed(jakarta.servlet.http.HttpSessionEvent se) {
                // Session destroyed logic
                System.out.println("Session destroyed: " + se.getSession().getId());
            }
        });
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}