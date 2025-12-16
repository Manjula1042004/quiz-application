package com.quizapp.repository;

import com.quizapp.entity.QuizAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserId(Long userId);
    List<QuizAttempt> findByQuizId(Long quizId);
    List<QuizAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);

    // NEW: Added missing method for recent attempts
    @Query("SELECT qa FROM QuizAttempt qa ORDER BY qa.attemptedAt DESC")
    List<QuizAttempt> findRecentAttempts(Pageable pageable);

    // NEW: Count methods
    Long countByUserId(Long userId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId")
    Long countByQuizId(Long quizId);

    @Query("SELECT AVG(qa.score) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.score IS NOT NULL")
    Double findAverageScoreByQuizId(Long quizId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId AND qa.quiz.id = :quizId ORDER BY qa.attemptedAt DESC")
    List<QuizAttempt> findByUserIdAndQuizIdOrderByAttemptedAtDesc(Long userId, Long quizId);
}