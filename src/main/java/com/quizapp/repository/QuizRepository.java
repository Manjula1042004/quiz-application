package com.quizapp.repository;

import com.quizapp.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("SELECT q FROM Quiz q WHERE q.createdBy.username = :username")
    List<Quiz> findByCreatedByUsername(@Param("username") String username);

    @Query("SELECT q FROM Quiz q WHERE q.isTemplate = true")
    List<Quiz> findByIsTemplateTrue();
}