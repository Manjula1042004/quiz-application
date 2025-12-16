package com.quizapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestEmailOverrideConfig {

    // This bean will override the EmailDiagnosticTest during tests
    @Bean
    @Primary
    public CommandLineRunner testEmailDiagnostic() {
        return args -> {
            System.out.println("✅ EmailDiagnosticTest disabled for unit tests");
            // Don't try to send emails during tests
        };
    }

    // This bean will override the EmailTest during tests
    @Bean
    @Primary
    public CommandLineRunner testEmailTest() {
        return args -> {
            System.out.println("✅ EmailTest disabled for unit tests");
            // Don't try to send emails during tests
        };
    }
}