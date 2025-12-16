package com.quizapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestEmailOverride {

    // This overrides EmailDiagnosticTest
    @Bean
    @Primary
    public CommandLineRunner testEmailDiagnostic() {
        return args -> {
            System.out.println("✅ EmailDiagnosticTest disabled for tests");
            // Don't send emails during tests
        };
    }

    // This overrides EmailTest
    @Bean
    @Primary
    public CommandLineRunner testEmailTest() {
        return args -> {
            System.out.println("✅ EmailTest disabled for tests");
            // Don't send emails during tests
        };
    }
}