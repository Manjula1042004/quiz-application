package com.quizapp.service;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DebugService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public void debugQuizData(Long quizId) {
        System.out.println("🔍 === DEBUG QUIZ DATA START ===");

        Optional<Quiz> quizOpt = quizRepository.findById(quizId);
        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            System.out.println("📋 Quiz: " + quiz.getTitle() + " (ID: " + quiz.getId() + ")");

            // Method 1: Check via quiz entity
            System.out.println("📝 Method 1 - Via Quiz Entity:");
            if (quiz.getQuestions() != null) {
                System.out.println("   Total questions: " + quiz.getQuestions().size());
                for (int i = 0; i < quiz.getQuestions().size(); i++) {
                    Question q = quiz.getQuestions().get(i);
                    System.out.println("   Question " + (i+1) + ": " + q.getQuestionText());
                    System.out.println("   Options count: " + (q.getOptions() != null ? q.getOptions().size() : 0));
                    if (q.getOptions() != null) {
                        for (int j = 0; j < q.getOptions().size(); j++) {
                            System.out.println("     Option " + j + ": " + q.getOptions().get(j));
                        }
                    }
                }
            }

            // Method 2: Check via direct question repository query
            System.out.println("📝 Method 2 - Via Question Repository:");
            List<Question> questions = questionRepository.findByQuizId(quizId);
            System.out.println("   Total questions (direct query): " + questions.size());
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                System.out.println("   Question " + (i+1) + ": " + q.getQuestionText());
                System.out.println("   Options count: " + (q.getOptions() != null ? q.getOptions().size() : 0));
                if (q.getOptions() != null) {
                    for (int j = 0; j < q.getOptions().size(); j++) {
                        System.out.println("     Option " + j + ": " + q.getOptions().get(j));
                    }
                }
            }
        } else {
            System.out.println("❌ Quiz not found with ID: " + quizId);
        }

        System.out.println("🔍 === DEBUG QUIZ DATA END ===");
    }
}