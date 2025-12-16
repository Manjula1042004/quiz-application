package com.quizapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import com.quizapp.config.DataInitializer;

@TestConfiguration
@Order(1)  // Ensure this loads before other configurations
public class TestDatabaseConfig {

    @Bean
    @Primary  // Override the production DataInitializer
    public DataInitializer testDataInitializer() {
        return new DataInitializer() {
            @Override
            public void run(String... args) {
                // Completely skip data initialization for tests
                // This prevents the CREATOR role error
                System.out.println("Test: Skipping data initialization");
            }
        };
    }
}