package com.quizapp.repository;

import com.quizapp.entity.DifficultyLevel;
import com.quizapp.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId")
    List<Question> findByQuizId(@Param("quizId") Long quizId);

    List<Question> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    List<Question> findByIsTemplateTrue();

    @Query("SELECT q FROM Question q WHERE q.quiz IS NULL AND q.isTemplate = true")
    List<Question> findTemplateQuestions();

    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t.name = :tagName")
    List<Question> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT q FROM Question q WHERE LOWER(q.questionText) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Question> searchByQuestionText(@Param("searchTerm") String searchTerm);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz.id = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT q FROM Question q WHERE q.quiz IS NULL")
    List<Question> findQuestionBankQuestions();

    // NEW: Delete question from quiz
    @Modifying
    @Query("DELETE FROM Question q WHERE q.id = :questionId AND q.quiz.id = :quizId")
    void deleteByQuizIdAndQuestionId(@Param("quizId") Long quizId, @Param("questionId") Long questionId);
}