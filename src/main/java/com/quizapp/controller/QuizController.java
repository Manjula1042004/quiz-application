package com.quizapp.controller;

import com.quizapp.dto.QuestionDto;
import com.quizapp.dto.QuizDto;
import com.quizapp.entity.*;
import com.quizapp.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private BulkImportService bulkImportService;

    @Autowired
    private QuizAttemptService quizAttemptService;

    @GetMapping("/templates")
    public String listQuizTemplates(Model model) {
        try {
            List<Quiz> templates = quizService.getQuizTemplates();
            List<Question> questionTemplates = questionService.getTemplateQuestions();

            model.addAttribute("quizTemplates", templates);
            model.addAttribute("questionTemplates", questionTemplates);
            return "quiz/templates";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading templates: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/create-from-template/{templateId}")
    public String createQuizFromTemplate(@PathVariable Long templateId,
                                         @RequestParam String newTitle,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Quiz template = quizService.getQuizById(templateId)
                    .orElseThrow(() -> new RuntimeException("Template not found"));

            Quiz newQuiz = quizService.createQuizFromTemplate(template, user, newTitle);
            model.addAttribute("success", "Quiz created from template successfully!");
            return "redirect:/quiz/edit/" + newQuiz.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Error creating quiz from template: " + e.getMessage());
            return "redirect:/quiz/templates";
        }
    }

    @PostMapping("/save-as-template/{quizId}")
    public String saveQuizAsTemplate(@PathVariable Long quizId,
                                     @RequestParam String templateName,
                                     Model model) {
        try {
            quizService.saveAsTemplate(quizId, templateName);
            model.addAttribute("success", "Quiz saved as template successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error saving as template: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/question-bank")
    public String questionBank(Model model,
                               @RequestParam(value = "quizId", required = false) Long quizId) {
        try {
            List<Question> questions = questionService.getQuestionBankQuestions();
            List<Tag> allTags = tagService.getAllTags();

            model.addAttribute("questions", questions);
            model.addAttribute("allTags", allTags);
            model.addAttribute("currentQuizId", quizId);
            return "quiz/question-bank";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading question bank: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/add-from-bank/{quizId}")
    public String addQuestionFromBank(@PathVariable Long quizId,
                                      @RequestParam Long questionId,
                                      Model model) {
        try {
            Quiz quiz = quizService.getQuizById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            Question question = questionService.getQuestionById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            quizService.addQuestionToQuiz(quiz, question);
            model.addAttribute("success", "Question added to quiz successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error adding question: " + e.getMessage());
        }
        return "redirect:/quiz/edit/" + quizId;
    }

    @GetMapping("/bulk-import/{quizId}")
    public String showBulkImportForm(@PathVariable Long quizId, Model model) {
        model.addAttribute("quizId", quizId);
        return "quiz/bulk-import";
    }

    @PostMapping("/bulk-import/{quizId}")
    public String bulkImportQuestions(@PathVariable Long quizId,
                                      @RequestParam("file") MultipartFile file,
                                      Model model) {
        try {
            Quiz quiz = quizService.getQuizById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            if (file.isEmpty()) {
                throw new RuntimeException("Please select a file to import");
            }

            if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
                throw new RuntimeException("Please upload a CSV file");
            }

            BulkImportService.BulkImportResult result = bulkImportService.importQuestionsFromCSV(file, quiz);

            if (result.hasErrors()) {
                model.addAttribute("error", "Import completed with " + result.getSuccessCount() +
                        " successful imports and " + result.getErrors().size() + " errors");
                model.addAttribute("importErrors", result.getErrors());
            } else {
                model.addAttribute("success", "Successfully imported " + result.getSuccessCount() + " questions!");
            }

        } catch (Exception e) {
            model.addAttribute("error", "Import failed: " + e.getMessage());
        }

        return "quiz/bulk-import";
    }

    @PostMapping("/question/convert-to-template/{questionId}")
    public String convertQuestionToTemplate(@PathVariable Long questionId,
                                            RedirectAttributes redirectAttributes) {
        try {
            questionService.convertToTemplate(questionId);
            redirectAttributes.addFlashAttribute("success", "Question converted to template successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error converting question: " + e.getMessage());
        }
        return "redirect:/quiz/question-bank";
    }

    @GetMapping("/create")
    public String showCreateQuizForm(Model model) {
        model.addAttribute("quizDto", new QuizDto());
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "quiz/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditQuizForm(@PathVariable Long id, Model model) {
        try {
            Quiz quiz = quizService.getQuizById(id)
                    .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

            QuizDto quizDto = new QuizDto();
            quizDto.setId(quiz.getId());
            quizDto.setTitle(quiz.getTitle());
            quizDto.setDescription(quiz.getDescription());
            quizDto.setTimeLimit(quiz.getTimeLimit());
            quizDto.setIsTemplate(quiz.getIsTemplate());

            if (quiz.getCategory() != null) {
                quizDto.setCategoryId(quiz.getCategory().getId());
            }

            model.addAttribute("quizDto", quizDto);
            model.addAttribute("quiz", quiz);
            model.addAttribute("categories", categoryService.getAllCategories());

            return "quiz/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading quiz: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    @PostMapping("/update/{id}")
    public String updateQuiz(@PathVariable Long id,
                             @Valid @ModelAttribute("quizDto") QuizDto quizDto,
                             BindingResult result,
                             Model model) {
        try {
            if (result.hasErrors()) {
                Quiz quiz = quizService.getQuizById(id)
                        .orElseThrow(() -> new RuntimeException("Quiz not found"));
                model.addAttribute("quiz", quiz);
                model.addAttribute("categories", categoryService.getAllCategories());
                return "quiz/edit";
            }

            Quiz quizDetails = new Quiz();
            quizDetails.setTitle(quizDto.getTitle());
            quizDetails.setDescription(quizDto.getDescription());
            quizDetails.setTimeLimit(quizDto.getTimeLimit());
            quizDetails.setIsTemplate(quizDto.getIsTemplate());

            // Set category if provided
            if (quizDto.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(quizDto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                quizDetails.setCategory(category);
            }

            Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
            model.addAttribute("success", "Quiz updated successfully!");

            return "redirect:/admin/dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error updating quiz: " + e.getMessage());

            // Return to edit form with existing data
            try {
                Quiz quiz = quizService.getQuizById(id).orElse(null);
                model.addAttribute("quiz", quiz);
                model.addAttribute("categories", categoryService.getAllCategories());
            } catch (Exception ex) {
                // Ignore
            }

            return "quiz/edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteQuiz(@PathVariable Long id, Model model) {
        try {
            quizService.deleteQuiz(id);
            model.addAttribute("success", "Quiz deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting quiz: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/create")
    public String createQuiz(@Valid @ModelAttribute("quizDto") QuizDto quizDto,
                             BindingResult result,
                             @AuthenticationPrincipal UserDetails userDetails,
                             Model model) {
        if (result.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "quiz/create";
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create quiz with all fields
            Quiz quiz = new Quiz();
            quiz.setTitle(quizDto.getTitle());
            quiz.setDescription(quizDto.getDescription());
            quiz.setTimeLimit(quizDto.getTimeLimit());
            quiz.setCreatedBy(user);
            quiz.setIsTemplate(quizDto.getIsTemplate());

            // Set difficulty level and visibility
            quiz.setDifficultyLevel(quizDto.getDifficultyLevel() != null ?
                    quizDto.getDifficultyLevel() : DifficultyLevel.MEDIUM);
            quiz.setIsPublic(quizDto.getIsPublic() != null ? quizDto.getIsPublic() : true);
            quiz.setEnabled(quizDto.getEnabled() != null ? quizDto.getEnabled() : true);

            // Set category if provided
            if (quizDto.getCategoryId() != null) {
                Category category = categoryService.getCategoryById(quizDto.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                quiz.setCategory(category);
            }

            Quiz savedQuiz = quizService.createQuiz(quiz, user);

            // Add questions
            if (quizDto.getQuestions() != null) {
                for (QuestionDto questionDto : quizDto.getQuestions()) {
                    if (questionDto.getQuestionText() != null && !questionDto.getQuestionText().trim().isEmpty()) {
                        Question question = new Question();
                        question.setQuestionText(questionDto.getQuestionText());

                        // Filter out empty options
                        if (questionDto.getOptions() != null) {
                            List<String> filteredOptions = questionDto.getOptions().stream()
                                    .filter(opt -> opt != null && !opt.trim().isEmpty())
                                    .collect(Collectors.toList());

                            // Validate at least 2 options
                            if (filteredOptions.size() < 2) {
                                throw new RuntimeException("Question must have at least 2 options: " + questionDto.getQuestionText());
                            }

                            question.setOptions(filteredOptions);
                        } else {
                            throw new RuntimeException("Options are required for question: " + questionDto.getQuestionText());
                        }

                        // Validate correct answer index
                        if (questionDto.getCorrectAnswerIndex() != null) {
                            if (questionDto.getCorrectAnswerIndex() < 0 ||
                                    questionDto.getCorrectAnswerIndex() >= question.getOptions().size()) {
                                throw new RuntimeException("Invalid correct answer index for question: " + questionDto.getQuestionText());
                            }
                            question.setCorrectAnswerIndex(questionDto.getCorrectAnswerIndex());
                        } else {
                            throw new RuntimeException("Correct answer is required for question: " + questionDto.getQuestionText());
                        }

                        question.setDifficultyLevel(questionDto.getDifficultyLevel());
                        question.setExplanation(questionDto.getExplanation());
                        question.setPoints(questionDto.getPoints());
                        question.setQuiz(savedQuiz);

                        // Add tags if provided
                        if (questionDto.getTags() != null && !questionDto.getTags().isEmpty()) {
                            questionService.createQuestionWithTags(question, questionDto.getTags());
                        } else {
                            questionService.createQuestion(question);
                        }
                    }
                }
            }

            model.addAttribute("success", "Quiz created successfully!");
            return "redirect:/admin/dashboard";

        } catch (Exception e) {
            e.printStackTrace();
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("error", "Error creating quiz: " + e.getMessage());
            return "quiz/create";
        }
    }

    @GetMapping("/list")
    public String listQuizzes(Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<Quiz> quizzes;
            User currentUser = null;

            if (userDetails != null) {
                currentUser = userService.findByUsername(userDetails.getUsername())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                if (currentUser.getRole().name().equals("ADMIN")) {
                    // Admin sees all quizzes
                    quizzes = quizService.getAllQuizzes();
                    model.addAttribute("isAdmin", true);
                } else {
                    // Student sees only public quizzes
                    quizzes = quizService.getPublicQuizzes();
                    model.addAttribute("isAdmin", false);
                }
            } else {
                // Public access sees only public quizzes
                quizzes = quizService.getPublicQuizzes();
                model.addAttribute("isAdmin", false);
            }

            // Initialize questions for each quiz to avoid LazyInitializationException
            for (Quiz quiz : quizzes) {
                if (quiz.getQuestions() != null) {
                    quiz.getQuestions().size(); // Force initialization
                }
            }

            model.addAttribute("quizzes", quizzes);
            model.addAttribute("user", currentUser);
            return "quiz/list";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading quizzes: " + e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/take/{quizId}")
    public String takeQuiz(@PathVariable Long quizId,
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        try {
            System.out.println("Direct quiz take for quiz ID: " + quizId);

            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Quiz> quizOpt = quizService.getQuizById(quizId);
            if (quizOpt.isEmpty()) {
                model.addAttribute("error", "Quiz not found");
                return "redirect:/quiz/list";
            }

            Quiz quiz = quizOpt.get();

            // Check if user already has an active attempt
            QuizAttempt attempt = quizAttemptService.startQuizAttempt(user, quiz);

            // Directly redirect to take quiz page with attempt ID
            return "redirect:/attempt/take/" + attempt.getId();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error starting quiz: " + e.getMessage());
            return "redirect:/quiz/list";
        }
    }

    @GetMapping("/question/edit/{quizId}/{questionId}")
    public String showEditQuestionForm(@PathVariable Long quizId,
                                       @PathVariable Long questionId,
                                       Model model) {
        try {
            Quiz quiz = quizService.getQuizById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            Question question = questionService.getQuestionById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            // ✅ FIX: Force initialization of options
            if (question.getOptions() != null) {
                question.getOptions().size(); // Force initialization
            }

            model.addAttribute("quiz", quiz);
            model.addAttribute("question", question);

            // ✅ FIX: Add options separately to model for easier access in template
            model.addAttribute("options", question.getOptions() != null ?
                    question.getOptions() : new ArrayList<>());

            return "quiz/edit-question";

        } catch (Exception e) {
            model.addAttribute("error", "Error loading question: " + e.getMessage());
            return "redirect:/quiz/edit/" + quizId;
        }
    }

    @PostMapping("/question/update/{quizId}/{questionId}")
    public String updateQuestion(@PathVariable Long quizId,
                                 @PathVariable Long questionId,
                                 @RequestParam String questionText,
                                 @RequestParam(value = "options", required = false) List<String> options,
                                 @RequestParam Integer correctAnswerIndex,
                                 @RequestParam(required = false) DifficultyLevel difficultyLevel,
                                 @RequestParam(required = false) String explanation,
                                 @RequestParam(required = false) Integer points,
                                 Model model) {
        try {
            System.out.println("=== DEBUG: Updating question " + questionId + " ===");

            // ✅ FIX: Handle null options
            List<String> filteredOptions = new ArrayList<>();
            if (options != null) {
                filteredOptions = options.stream()
                        .filter(opt -> opt != null && !opt.trim().isEmpty())
                        .collect(Collectors.toList());
            } else {
                // Get existing options if new ones are not provided
                Question existingQuestion = questionService.getQuestionById(questionId)
                        .orElseThrow(() -> new RuntimeException("Question not found"));
                if (existingQuestion.getOptions() != null) {
                    filteredOptions = new ArrayList<>(existingQuestion.getOptions());
                }
            }

            System.out.println("Options count: " + filteredOptions.size());
            System.out.println("Options: " + filteredOptions);

            if (filteredOptions.size() < 2) {
                throw new RuntimeException("At least 2 non-empty options are required");
            }

            // ✅ FIX: Validate correct answer index
            if (correctAnswerIndex < 0 || correctAnswerIndex >= filteredOptions.size()) {
                throw new RuntimeException("Invalid correct answer index. Must be between 0 and " + (filteredOptions.size() - 1));
            }

            // ✅ FIX: Create question details
            Question questionDetails = new Question();
            questionDetails.setQuestionText(questionText);
            questionDetails.setOptions(filteredOptions);
            questionDetails.setCorrectAnswerIndex(correctAnswerIndex);
            questionDetails.setDifficultyLevel(difficultyLevel != null ? difficultyLevel : DifficultyLevel.MEDIUM);
            questionDetails.setExplanation(explanation);
            questionDetails.setPoints(points != null ? points : 1);

            // ✅ FIX: Update the question
            Question updatedQuestion = questionService.updateQuestion(questionId, questionDetails);
            model.addAttribute("success", "Question updated successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error updating question: " + e.getMessage());
        }

        return "redirect:/quiz/edit/" + quizId;
    }

    @PostMapping("/question/add/{quizId}")
    public String addQuestionToQuiz(@PathVariable Long quizId,
                                    @RequestParam String questionText,
                                    @RequestParam(value = "options", required = false) List<String> options,
                                    @RequestParam(value = "correctAnswerIndex", required = false) Integer correctAnswerIndex,
                                    @RequestParam(required = false) DifficultyLevel difficultyLevel,
                                    @RequestParam(required = false) String explanation,
                                    @RequestParam(required = false) Integer points,
                                    @RequestParam(required = false) String tags,
                                    Model model) {
        try {
            System.out.println("=== DEBUG: Adding question to quiz ID: " + quizId + " ===");
            System.out.println("Question Text: " + questionText);
            System.out.println("Options: " + (options != null ? options : "NULL"));
            System.out.println("Correct Answer Index: " + correctAnswerIndex);

            Quiz quiz = quizService.getQuizById(quizId)
                    .orElseThrow(() -> new RuntimeException("Quiz not found"));

            // Validate required parameters
            if (questionText == null || questionText.trim().isEmpty()) {
                throw new RuntimeException("Question text is required");
            }

            // Handle options - this is the key fix
            List<String> filteredOptions = new ArrayList<>();
            if (options != null) {
                // Filter out empty or null options
                filteredOptions = options.stream()
                        .filter(opt -> opt != null && !opt.trim().isEmpty())
                        .collect(Collectors.toList());
            }

            System.out.println("Filtered options count: " + filteredOptions.size());
            System.out.println("Filtered options: " + filteredOptions);

            // Validate at least 2 options
            if (filteredOptions.size() < 2) {
                throw new RuntimeException("At least 2 non-empty options are required. Found: " + filteredOptions.size());
            }

            // Validate correct answer index
            if (correctAnswerIndex == null) {
                throw new RuntimeException("Correct answer index is required");
            }

            if (correctAnswerIndex < 0 || correctAnswerIndex >= filteredOptions.size()) {
                throw new RuntimeException("Invalid correct answer index. Must be between 0 and " + (filteredOptions.size() - 1));
            }

            Question question = new Question();
            question.setQuestionText(questionText);
            question.setOptions(filteredOptions);
            question.setCorrectAnswerIndex(correctAnswerIndex);
            question.setDifficultyLevel(difficultyLevel != null ? difficultyLevel : DifficultyLevel.MEDIUM);
            question.setExplanation(explanation);
            question.setPoints(points != null ? points : 1);
            question.setQuiz(quiz);

            // Add tags if provided
            if (tags != null && !tags.trim().isEmpty()) {
                List<String> tagNames = Arrays.stream(tags.split(","))
                        .map(String::trim)
                        .filter(tag -> !tag.isEmpty())
                        .collect(Collectors.toList());
                questionService.createQuestionWithTags(question, tagNames);
            } else {
                questionService.createQuestion(question);
            }

            model.addAttribute("success", "Question added successfully!");
            System.out.println("=== Question added successfully ===");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ERROR adding question: " + e.getMessage());
            model.addAttribute("error", "Error adding question: " + e.getMessage());
        }

        return "redirect:/quiz/edit/" + quizId;
    }

    // NEW: Delete question from quiz
    @PostMapping("/question/delete/{quizId}/{questionId}")
    public String deleteQuestionFromQuiz(@PathVariable Long quizId,
                                         @PathVariable Long questionId,
                                         Model model) {
        try {
            // Get the question first to verify it belongs to the quiz
            Question question = questionService.getQuestionById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            // Verify question belongs to the quiz
            if (question.getQuiz() == null || !question.getQuiz().getId().equals(quizId)) {
                throw new RuntimeException("Question does not belong to this quiz");
            }

            // Delete the question
            questionService.deleteQuestion(questionId);
            model.addAttribute("success", "Question deleted successfully!");

        } catch (Exception e) {
            model.addAttribute("error", "Error deleting question: " + e.getMessage());
        }

        return "redirect:/quiz/edit/" + quizId;
    }

    @GetMapping("/")
    public String redirectToQuizList() {
        return "redirect:/quiz/list";
    }
}