// File: src/main/java/com/quizapp/controller/DebugController.java
package com.quizapp.controller;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.service.DebugService;
import com.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private DebugService debugService;

    @GetMapping("/quiz/{quizId}")
    public String debugQuiz(@PathVariable Long quizId, Model model) {
        Optional<Quiz> quizOpt = quizService.getQuizById(quizId);

        if (quizOpt.isPresent()) {
            Quiz quiz = quizOpt.get();
            model.addAttribute("quiz", quiz);
            model.addAttribute("debug", true);

            // Log detailed info
            System.out.println("=== DEBUG QUIZ " + quizId + " ===");
            System.out.println("Title: " + quiz.getTitle());
            System.out.println("Questions: " + quiz.getQuestions().size());

            for (int i = 0; i < quiz.getQuestions().size(); i++) {
                Question q = quiz.getQuestions().get(i);
                System.out.println("  Q" + (i+1) + ": " + q.getQuestionText());
                System.out.println("  Options: " + q.getOptions().size());
                for (int j = 0; j < q.getOptions().size(); j++) {
                    System.out.println("    [" + j + "] " + q.getOptions().get(j));
                }
            }

            return "quiz/take"; // Render the quiz with debug info
        }

        model.addAttribute("error", "Quiz not found");
        return "redirect:/dashboard";
    }
}