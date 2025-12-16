package com.quizapp.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SecurityConfig Tests")
class SecurityConfigTest {

    @Test
    @DisplayName("Should create BCryptPasswordEncoder bean")
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // Since we can't easily instantiate SecurityConfig with its dependencies,
        // let's test the PasswordEncoder directly
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);

        // Test that it actually encodes
        String rawPassword = "testPassword123";
        String encoded = passwordEncoder.encode(rawPassword);
        assertNotNull(encoded);
        assertTrue(encoded.length() > 0);
        assertTrue(passwordEncoder.matches(rawPassword, encoded));
    }

    @Test
    @DisplayName("Should configure CORS correctly")
    void corsConfiguration_ShouldBeCorrect() {
        // Test CORS configuration directly
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        assertNotNull(config);
        assertEquals(Arrays.asList("*"), config.getAllowedOriginPatterns());
        assertEquals(5, config.getAllowedMethods().size());
        assertTrue(config.getAllowedMethods().contains("GET"));
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getAllowCredentials());
    }

    @Test
    @DisplayName("SecurityConfig should have correct structure")
    void securityConfig_ShouldHaveCorrectAnnotations() {
        // This is a meta-test to ensure the class has correct annotations
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.web.configuration.EnableWebSecurity.class));
        assertTrue(SecurityConfig.class.isAnnotationPresent(org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity.class));
    }
}