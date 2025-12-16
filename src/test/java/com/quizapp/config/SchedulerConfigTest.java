package com.quizapp.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SchedulerConfig Tests")
class SchedulerConfigTest {

    @Test
    @DisplayName("Should be annotated with @EnableScheduling")
    void schedulerConfig_ShouldHaveEnableSchedulingAnnotation() {
        // When
        Class<SchedulerConfig> configClass = SchedulerConfig.class;
        Annotation[] annotations = configClass.getAnnotations();

        // Then
        boolean hasEnableScheduling = false;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(EnableScheduling.class)) {
                hasEnableScheduling = true;
                break;
            }
        }
        assertTrue(hasEnableScheduling, "SchedulerConfig should be annotated with @EnableScheduling");
    }

    @Test
    @DisplayName("Should create SchedulerConfig instance")
    void schedulerConfig_ShouldCreateInstance() {
        // Given
        SchedulerConfig config = new SchedulerConfig();

        // Then
        assertTrue(config != null);
    }
}