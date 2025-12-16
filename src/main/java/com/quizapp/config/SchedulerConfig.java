// File: src/main/java/com/quizapp/config/SchedulerConfig.java
package com.quizapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    // Enables @Scheduled annotations
}