package com.quizapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SimpleTestRunner {

    @Test
    void contextLoads() {
        assertTrue(true, "Spring context should load successfully");
        System.out.println("✅ Spring context loaded successfully!");
    }

    @Test
    void testBasicSetup() {
        int result = 2 + 2;
        assertTrue(result == 4, "Basic math should work");
        System.out.println("✅ Basic test passed!");
    }
}