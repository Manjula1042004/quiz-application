package com.quizapp.service;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.QuizAttempt;
import com.quizapp.entity.User;
import com.quizapp.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizService quizService;

    @Autowired
    private EmailService emailService;

    @Transactional
    public QuizAttempt startQuizAttempt(User user, Quiz quiz) {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setQuiz(quiz);
        attempt.setAttemptedAt(LocalDateTime.now());

        // Set expiry time for server-side timeout validation
        if (quiz.getTimeLimit() != null && quiz.getTimeLimit() > 0) {
            attempt.setExpiresAt(LocalDateTime.now().plusMinutes(quiz.getTimeLimit()));
        }

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        System.out.println("✅ Created new quiz attempt with ID: " + savedAttempt.getId() +
                ", Expires at: " + savedAttempt.getExpiresAt());
        return savedAttempt;
    }

    private double calculateScore(QuizAttempt attempt) {
        Quiz quiz = attempt.getQuiz();
        Map<Long, Integer> userAnswers = attempt.getAnswers();

        // FIX: Safe access to questions
        if (quiz.getQuestions() == null) {
            return 0.0;
        }

        int correctAnswers = 0;
        int totalQuestions = 0;
        int totalPoints = 0;
        int earnedPoints = 0;

        // FIX: Safe initialization of questions collection
        try {
            totalQuestions = quiz.getQuestions().size();
        } catch (org.hibernate.LazyInitializationException e) {
            System.err.println("⚠️ LazyInitializationException in calculateScore, using 0 questions");
            return 0.0;
        }

        if (totalQuestions == 0) {
            return 0.0;
        }

        System.out.println("=== SCORE CALCULATION START ===");
        System.out.println("Total questions: " + totalQuestions);
        System.out.println("User answers: " + userAnswers);

        for (Question question : quiz.getQuestions()) {
            Long questionId = question.getId();
            Integer userAnswer = userAnswers.get(questionId);
            Integer correctAnswer = question.getCorrectAnswerIndex();
            Integer questionPoints = question.getPoints() != null ? question.getPoints() : 1;

            totalPoints += questionPoints;

            System.out.println("Question ID: " + questionId);
            System.out.println("Question: " + question.getQuestionText());
            System.out.println("Points: " + questionPoints);
            System.out.println("Options: " + question.getOptions());
            System.out.println("Correct answer index: " + correctAnswer);
            System.out.println("Correct answer text: " +
                    (correctAnswer != null && correctAnswer < question.getOptions().size() ?
                            question.getOptions().get(correctAnswer) : "INVALID"));
            System.out.println("User answer index: " + userAnswer);
            System.out.println("User answer text: " +
                    (userAnswer != null && userAnswer < question.getOptions().size() ?
                            question.getOptions().get(userAnswer) : "NOT ANSWERED"));

            // Validate answer is within bounds
            boolean isValidAnswer = userAnswer != null &&
                    userAnswer >= 0 &&
                    userAnswer < question.getOptions().size();

            boolean isCorrectAnswer = correctAnswer != null &&
                    userAnswer != null &&
                    userAnswer.equals(correctAnswer);

            if (isValidAnswer && isCorrectAnswer) {
                correctAnswers++;
                earnedPoints += questionPoints;
                System.out.println("✅ Question " + questionId + ": CORRECT (+" + questionPoints + " points)");
            } else if (isValidAnswer) {
                System.out.println("❌ Question " + questionId + ": INCORRECT (0 points)");
                System.out.println("   Expected: " + correctAnswer + ", Got: " + userAnswer);
            } else if (userAnswer != null) {
                System.out.println("⚠️ Question " + questionId + ": INVALID ANSWER (0 points)");
                System.out.println("   Answer " + userAnswer + " is out of bounds (0-" +
                        (question.getOptions().size() - 1) + ")");
            } else {
                System.out.println("⏸️ Question " + questionId + ": NOT ANSWERED (0 points)");
            }
            System.out.println("---");
        }

        // Store points in attempt
        attempt.setTotalPoints(totalPoints);
        attempt.setEarnedPoints(earnedPoints);

        double score = totalPoints > 0 ? ((double) earnedPoints / totalPoints) * 100 : 0;
        System.out.println("Final score: " + earnedPoints + "/" + totalPoints + " points = " + score + "%");
        System.out.println("Correct answers: " + correctAnswers + "/" + totalQuestions);
        System.out.println("=== SCORE CALCULATION END ===");

        return score;
    }

    @Transactional(readOnly = true)
    public List<QuizAttempt> getUserAttempts(Long userId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(userId);

        // FIX: Initialize all lazy-loaded associations
        for (QuizAttempt attempt : attempts) {
            if (attempt.getQuiz() != null) {
                // Initialize quiz proxy
                attempt.getQuiz().getTitle();

                // Initialize questions if needed
                if (attempt.getQuiz().getQuestions() != null) {
                    try {
                        attempt.getQuiz().getQuestions().size();
                    } catch (org.hibernate.LazyInitializationException e) {
                        // Ignore - we'll handle it in email service
                    }
                }
            }
        }

        return attempts;
    }

    @Transactional
    public QuizAttempt submitQuiz(Long attemptId, Map<Long, Integer> answers) {
        System.out.println("🚀 Starting quiz submission for attempt: " + attemptId);

        Optional<QuizAttempt> attemptOpt = quizAttemptRepository.findById(attemptId);
        if (attemptOpt.isEmpty()) {
            throw new RuntimeException("Quiz attempt not found with ID: " + attemptId);
        }

        QuizAttempt attempt = attemptOpt.get();
        System.out.println("✅ Found attempt for quiz: " + attempt.getQuiz().getTitle());

        // SERVER-SIDE TIMEOUT VALIDATION
        if (attempt.isExpired()) {
            System.out.println("⏰ Quiz attempt EXPIRED at: " + attempt.getExpiresAt());
            System.out.println("⚠️ Time ran out - auto-grading incomplete submission");

            if (answers == null) {
                answers = new HashMap<>();
            }
        } else if (attempt.getExpiresAt() != null) {
            System.out.println("✅ Attempt valid until: " + attempt.getExpiresAt());
        }

        // DATA VALIDATION: Remove invalid answers
        Map<Long, Integer> validatedAnswers = new HashMap<>();
        if (answers != null) {
            for (Map.Entry<Long, Integer> entry : answers.entrySet()) {
                Long questionId = entry.getKey();
                Integer userAnswer = entry.getValue();

                // Find the question to validate
                if (attempt.getQuiz() != null && attempt.getQuiz().getQuestions() != null) {
                    for (Question question : attempt.getQuiz().getQuestions()) {
                        if (question.getId().equals(questionId)) {
                            // Validate answer is within bounds
                            if (userAnswer != null &&
                                    userAnswer >= 0 &&
                                    userAnswer < question.getOptions().size()) {
                                validatedAnswers.put(questionId, userAnswer);
                            } else {
                                System.out.println("❌ Removing invalid answer for question " +
                                        questionId + ": " + userAnswer);
                            }
                            break;
                        }
                    }
                }
            }
        }

        // Set answers and completion time
        attempt.setAnswers(validatedAnswers);
        attempt.setCompletedAt(LocalDateTime.now());

        // Calculate score
        double score = calculateScore(attempt);
        attempt.setScore(score);

        System.out.println("📊 Calculated score: " + score + "%");

        // Save the updated attempt
        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        System.out.println("💾 Quiz attempt saved successfully");

        // Send email notification
        System.out.println("📧 Attempting to send quiz results email...");
        try {
            emailService.sendQuizResultEmail(savedAttempt);
            System.out.println("✅ Quiz results email process completed");
        } catch (Exception e) {
            System.err.println("⚠️ Quiz results email failed but quiz was submitted: " + e.getMessage());
        }

        return savedAttempt;
    }

    @Transactional(readOnly = true)
    public Optional<QuizAttempt> getAttemptById(Long id) {
        Optional<QuizAttempt> attempt = quizAttemptRepository.findById(id);

        // FIX: Initialize lazy-loaded associations
        if (attempt.isPresent()) {
            QuizAttempt quizAttempt = attempt.get();
            if (quizAttempt.getQuiz() != null) {
                // Initialize quiz proxy
                quizAttempt.getQuiz().getTitle();
            }
        }

        return attempt;
    }

    // Auto-submit expired attempts (runs every minute)
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void autoSubmitExpiredAttempts() {
        System.out.println("🔄 Checking for expired quiz attempts...");

        List<QuizAttempt> allAttempts = quizAttemptRepository.findAll();
        List<QuizAttempt> expiredAttempts = allAttempts.stream()
                .filter(attempt ->
                        attempt.getCompletedAt() == null &&
                                attempt.getExpiresAt() != null &&
                                attempt.isExpired()
                )
                .collect(Collectors.toList());

        System.out.println("Found " + expiredAttempts.size() + " expired attempts");

        for (QuizAttempt attempt : expiredAttempts) {
            try {
                System.out.println("⏰ Auto-submitting expired attempt: " + attempt.getId());

                // Use existing answers or empty map
                Map<Long, Integer> answers = attempt.getAnswers() != null ?
                        attempt.getAnswers() : new HashMap<>();

                submitQuiz(attempt.getId(), answers);

            } catch (Exception e) {
                System.err.println("❌ Failed to auto-submit attempt " + attempt.getId() +
                        ": " + e.getMessage());
            }
        }
    }
}