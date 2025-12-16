package com.quizapp.util;

import com.quizapp.entity.QuizAttempt;
import java.time.Duration;
import java.util.List;

public class DashboardUtil {

    public static long getCompletedAttemptsCount(List<QuizAttempt> attempts) {
        if (attempts == null) return 0;
        return attempts.stream()
                .filter(a -> a.getCompletedAt() != null)
                .count();
    }

    public static long getInProgressAttemptsCount(List<QuizAttempt> attempts) {
        if (attempts == null) return 0;
        return attempts.stream()
                .filter(a -> a.getCompletedAt() == null)
                .count();
    }

    public static double calculateAverageScore(List<QuizAttempt> attempts) {
        if (attempts == null || attempts.isEmpty()) return 0.0;
        return attempts.stream()
                .filter(a -> a.getScore() != null)
                .mapToDouble(QuizAttempt::getScore)
                .average()
                .orElse(0.0);
    }

    public static long calculateTimeSpentMinutes(QuizAttempt attempt) {
        if (attempt.getCompletedAt() != null && attempt.getAttemptedAt() != null) {
            return Duration.between(attempt.getAttemptedAt(), attempt.getCompletedAt()).toMinutes();
        }
        return 0;
    }
}