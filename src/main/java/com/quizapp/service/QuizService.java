package com.quizapp.service;

import com.quizapp.entity.Question;
import com.quizapp.entity.Quiz;
import com.quizapp.entity.User;
import com.quizapp.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionService questionService;

    @Transactional
    public Quiz createQuiz(Quiz quiz, User createdBy) {
        quiz.setCreatedBy(createdBy);
        Quiz savedQuiz = quizRepository.save(quiz);
        System.out.println("Created quiz: " + savedQuiz.getTitle() + " with ID: " + savedQuiz.getId());
        return savedQuiz;
    }

    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        System.out.println("Found " + quizzes.size() + " quizzes");
        return quizzes;
    }

    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesByUser(String username) {
        List<Quiz> quizzes = quizRepository.findByCreatedByUsername(username);
        System.out.println("Found " + quizzes.size() + " quizzes for user: " + username);
        return quizzes;
    }



    @Transactional
    public Quiz updateQuiz(Long id, Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        quiz.setTitle(quizDetails.getTitle());
        quiz.setDescription(quizDetails.getDescription());
        quiz.setTimeLimit(quizDetails.getTimeLimit());
        quiz.setDifficultyLevel(quizDetails.getDifficultyLevel());
        quiz.setIsPublic(quizDetails.getIsPublic());
        quiz.setEnabled(quizDetails.getEnabled());

        return quizRepository.save(quiz);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quizRepository.delete(quiz);
        System.out.println("Deleted quiz with ID: " + id);
    }


    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesForStudent() {
        return getPublicQuizzes();
    }

    public List<Quiz> getQuizTemplates() {
        return quizRepository.findByIsTemplateTrue();
    }

    @Transactional
    public Quiz createQuizFromTemplate(Quiz template, User user, String newTitle) {
        // Create a new quiz based on the template
        Quiz newQuiz = new Quiz();
        newQuiz.setTitle(newTitle);
        newQuiz.setDescription(template.getDescription() + " (Created from template)");
        newQuiz.setTimeLimit(template.getTimeLimit());
        newQuiz.setCreatedBy(user);
        newQuiz.setDifficultyLevel(template.getDifficultyLevel());
        newQuiz.setIsPublic(template.getIsPublic());
        newQuiz.setEnabled(true);
        newQuiz.setIsTemplate(false);

        // Copy category if exists
        if (template.getCategory() != null) {
            newQuiz.setCategory(template.getCategory());
        }

        Quiz savedQuiz = quizRepository.save(newQuiz);

        // Copy questions from template
        if (template.getQuestions() != null && !template.getQuestions().isEmpty()) {
            for (Question templateQuestion : template.getQuestions()) {
                Question newQuestion = new Question();
                newQuestion.setQuestionText(templateQuestion.getQuestionText());
                newQuestion.setOptions(new ArrayList<>(templateQuestion.getOptions()));
                newQuestion.setCorrectAnswerIndex(templateQuestion.getCorrectAnswerIndex());
                newQuestion.setDifficultyLevel(templateQuestion.getDifficultyLevel());
                newQuestion.setExplanation(templateQuestion.getExplanation());
                newQuestion.setPoints(templateQuestion.getPoints());
                newQuestion.setQuiz(savedQuiz);
                newQuestion.setIsTemplate(false);

                // Copy tags if they exist
                if (templateQuestion.getTags() != null && !templateQuestion.getTags().isEmpty()) {
                    newQuestion.setTags(new ArrayList<>(templateQuestion.getTags()));
                }

                questionService.createQuestion(newQuestion);
            }
        }

        System.out.println("Created quiz from template: " + newTitle + " with ID: " + savedQuiz.getId());
        return savedQuiz;
    }

    @Transactional
    public void saveAsTemplate(Long quizId, String templateName) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        // Create a copy of the quiz as a template
        Quiz template = new Quiz();
        template.setTitle(templateName);
        template.setDescription(quiz.getDescription() + " (Template)");
        template.setTimeLimit(quiz.getTimeLimit());
        template.setCreatedBy(quiz.getCreatedBy());
        template.setDifficultyLevel(quiz.getDifficultyLevel());
        template.setIsPublic(false); // Templates are not public by default
        template.setEnabled(true);
        template.setIsTemplate(true);

        // Copy category if exists
        if (quiz.getCategory() != null) {
            template.setCategory(quiz.getCategory());
        }

        Quiz savedTemplate = quizRepository.save(template);

        // Copy questions as template questions
        if (quiz.getQuestions() != null && !quiz.getQuestions().isEmpty()) {
            for (Question originalQuestion : quiz.getQuestions()) {
                Question templateQuestion = new Question();
                templateQuestion.setQuestionText(originalQuestion.getQuestionText());
                templateQuestion.setOptions(new ArrayList<>(originalQuestion.getOptions()));
                templateQuestion.setCorrectAnswerIndex(originalQuestion.getCorrectAnswerIndex());
                templateQuestion.setDifficultyLevel(originalQuestion.getDifficultyLevel());
                templateQuestion.setExplanation(originalQuestion.getExplanation());
                templateQuestion.setPoints(originalQuestion.getPoints());
                templateQuestion.setQuiz(savedTemplate);
                templateQuestion.setIsTemplate(true);

                // Copy tags if they exist
                if (originalQuestion.getTags() != null && !originalQuestion.getTags().isEmpty()) {
                    templateQuestion.setTags(new ArrayList<>(originalQuestion.getTags()));
                }

                questionService.createQuestion(templateQuestion);
            }
        }

        System.out.println("Saved quiz as template: " + templateName + " with ID: " + savedTemplate.getId());
    }

    @Transactional
    public void addQuestionToQuiz(Quiz quiz, Question question) {
        // Create a copy of the question for the quiz
        Question newQuestion = new Question();
        newQuestion.setQuestionText(question.getQuestionText());
        newQuestion.setOptions(new ArrayList<>(question.getOptions()));
        newQuestion.setCorrectAnswerIndex(question.getCorrectAnswerIndex());
        newQuestion.setDifficultyLevel(question.getDifficultyLevel());
        newQuestion.setExplanation(question.getExplanation());
        newQuestion.setPoints(question.getPoints());
        newQuestion.setQuiz(quiz);
        newQuestion.setIsTemplate(false);

        // Copy tags if they exist
        if (question.getTags() != null && !question.getTags().isEmpty()) {
            newQuestion.setTags(new ArrayList<>(question.getTags()));
        }

        questionService.createQuestion(newQuestion);
        System.out.println("Added question to quiz: " + quiz.getTitle() + ", Question: " + question.getQuestionText());
    }

    // In QuizService.java, update the getQuizById method:
    @Transactional(readOnly = true)
    public Optional<Quiz> getQuizById(Long id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        if (quiz.isPresent()) {
            Quiz q = quiz.get();
            // Initialize questions to avoid LazyInitializationException
            if (q.getQuestions() != null) {
                q.getQuestions().size(); // Force initialization

                // Also initialize options for each question
                q.getQuestions().forEach(question -> {
                    if (question.getOptions() != null) {
                        question.getOptions().size(); // Force initialization
                    }
                });
            }
            System.out.println("Found quiz by ID " + id + ": " + q.getTitle() +
                    " with " + (q.getQuestions() != null ? q.getQuestions().size() : 0) + " questions");
        } else {
            System.out.println("Quiz not found with ID: " + id);
        }
        return quiz;
    }

    // In QuizService.java, update the getPublicQuizzes method:


    // In QuizService.java, replace the getPublicQuizzes() method with this:
    @Transactional(readOnly = true)
    public List<Quiz> getPublicQuizzes() {
        System.out.println("🔍 === DEBUG getPublicQuizzes() START ===");

        List<Quiz> allQuizzes = quizRepository.findAll();
        System.out.println("Total quizzes in database: " + allQuizzes.size());

        if (allQuizzes.isEmpty()) {
            System.out.println("❌ NO QUIZZES FOUND IN DATABASE!");
            System.out.println("🔍 === DEBUG getPublicQuizzes() END ===");
            return new ArrayList<>();
        }

        // Print all quizzes with their properties
        for (int i = 0; i < allQuizzes.size(); i++) {
            Quiz quiz = allQuizzes.get(i);
            System.out.println("\n📊 Quiz " + (i+1) + ":");
            System.out.println("  ID: " + quiz.getId());
            System.out.println("  Title: " + quiz.getTitle());
            System.out.println("  isPublic: " + quiz.getIsPublic());
            System.out.println("  enabled: " + quiz.getEnabled());
            System.out.println("  isTemplate: " + quiz.getIsTemplate());
            System.out.println("  Questions: " + (quiz.getQuestions() != null ? quiz.getQuestions().size() : 0));

            // Check if any fields are null
            if (quiz.getIsPublic() == null) {
                System.out.println("  ⚠️ WARNING: isPublic is NULL!");
            }
            if (quiz.getEnabled() == null) {
                System.out.println("  ⚠️ WARNING: enabled is NULL!");
            }
            if (quiz.getIsTemplate() == null) {
                System.out.println("  ⚠️ WARNING: isTemplate is NULL!");
            }
        }

        // Filter only public and enabled quizzes
        List<Quiz> publicQuizzes = allQuizzes.stream()
                .filter(quiz -> {
                    boolean isPublic = Boolean.TRUE.equals(quiz.getIsPublic());
                    boolean isEnabled = Boolean.TRUE.equals(quiz.getEnabled());
                    boolean notTemplate = !Boolean.TRUE.equals(quiz.getIsTemplate());

                    boolean passesFilter = isPublic && isEnabled && notTemplate;

                    System.out.println("\n🎯 Filter check for '" + quiz.getTitle() + "':");
                    System.out.println("  isPublic=" + isPublic + " (raw: " + quiz.getIsPublic() + ")");
                    System.out.println("  isEnabled=" + isEnabled + " (raw: " + quiz.getEnabled() + ")");
                    System.out.println("  notTemplate=" + notTemplate + " (raw: " + quiz.getIsTemplate() + ")");
                    System.out.println("  Passes filter: " + passesFilter);

                    return passesFilter;
                })
                .collect(Collectors.toList());

        System.out.println("\n✅ Filtered result: " + publicQuizzes.size() + " public quizzes");
        for (Quiz quiz : publicQuizzes) {
            System.out.println("  - " + quiz.getTitle() + " (ID: " + quiz.getId() + ")");
        }

        // Initialize questions for each quiz
        for (Quiz quiz : publicQuizzes) {
            if (quiz.getQuestions() != null) {
                quiz.getQuestions().size(); // Force initialization
            }
        }

        System.out.println("🔍 === DEBUG getPublicQuizzes() END ===");
        return publicQuizzes;
    }
}