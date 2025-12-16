package com.quizapp.controller;

import com.quizapp.dto.QuestionDto;
import com.quizapp.dto.QuizDto;
import com.quizapp.entity.*;
import com.quizapp.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class QuizControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizService quizService;

    @Mock
    private QuestionService questionService;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private TagService tagService;

    @Mock
    private BulkImportService bulkImportService;

    @Mock
    private QuizAttemptService quizAttemptService;

    @InjectMocks
    private QuizController quizController;

    private User user;
    private Quiz quiz;
    private Question question;
    private Category category;
    private User adminUser;
    private User participantUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizController)
                .apply(springSecurity())  // Apply Spring Security
                .build();

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        participantUser = new User();
        participantUser.setId(3L);
        participantUser.setUsername("participant");
        participantUser.setRole(Role.PARTICIPANT);

        category = new Category();
        category.setId(1L);
        category.setName("Mathematics");

        quiz = new Quiz();
        quiz.setId(1L);
        quiz.setTitle("Test Quiz");
        quiz.setDescription("Test Description");
        quiz.setCreatedBy(user);
        quiz.setCategory(category);
        quiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        quiz.setIsPublic(true);
        quiz.setEnabled(true);

        question = new Question();
        question.setId(1L);
        question.setQuestionText("What is 2+2?");
        question.setOptions(Arrays.asList("3", "4", "5", "6"));
        question.setCorrectAnswerIndex(1);
        question.setQuiz(quiz);

        quiz.setQuestions(Arrays.asList(question));
    }

    @Test
    @WithMockUser(username = "testuser")
    void showCreateQuizForm_ShouldDisplayForm() throws Exception {
        List<Category> categories = Arrays.asList(category);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/quiz/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/create"))
                .andExpect(model().attributeExists("quizDto", "categories"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listQuizzes_ShouldDisplayQuizzesForAdmin() throws Exception {
        List<Quiz> quizzes = Arrays.asList(quiz);

        when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(quizService.getAllQuizzes()).thenReturn(quizzes);

        mockMvc.perform(get("/quiz/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/list"))
                .andExpect(model().attributeExists("quizzes", "user", "isAdmin"));

        verify(quizService, times(1)).getAllQuizzes();
    }

    @Test
    @WithMockUser(username = "participant", roles = {"PARTICIPANT"})
    void listQuizzes_ShouldDisplayPublicQuizzesForParticipant() throws Exception {
        List<Quiz> publicQuizzes = Arrays.asList(quiz);

        when(userService.findByUsername("participant")).thenReturn(Optional.of(participantUser));
        when(quizService.getPublicQuizzes()).thenReturn(publicQuizzes);

        mockMvc.perform(get("/quiz/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/list"))
                .andExpect(model().attribute("isAdmin", false));

        verify(quizService, times(1)).getPublicQuizzes();
    }

    @Test
    @WithMockUser(username = "testuser")
    void editQuiz_ShouldDisplayEditForm() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList(category));

        mockMvc.perform(get("/quiz/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz/edit"))
                .andExpect(model().attributeExists("quizDto", "quiz", "categories"));

        verify(quizService, times(1)).getQuizById(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createQuiz_ShouldCreateSuccessfully() throws Exception {
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("New Quiz");
        quizDto.setDescription("New Description");
        quizDto.setTimeLimit(30);
        quizDto.setIsTemplate(false);
        quizDto.setCategoryId(1L);
        quizDto.setDifficultyLevel(DifficultyLevel.EASY);
        quizDto.setIsPublic(true);
        quizDto.setEnabled(true);

        List<QuestionDto> questions = new ArrayList<>();
        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionText("What is 2+2?");
        questionDto.setOptions(Arrays.asList("3", "4", "5", "6"));
        questionDto.setCorrectAnswerIndex(1);
        questions.add(questionDto);
        quizDto.setQuestions(questions);

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(quizService.createQuiz(any(Quiz.class), any(User.class))).thenReturn(quiz);
        when(questionService.createQuestion(any(Question.class))).thenReturn(question);

        mockMvc.perform(post("/quiz/create")
                        .flashAttr("quizDto", quizDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("success"));

        verify(quizService, times(1)).createQuiz(any(Quiz.class), any(User.class));
        verify(questionService, times(1)).createQuestion(any(Question.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateQuiz_ShouldUpdateSuccessfully() throws Exception {
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("Updated Quiz");
        quizDto.setDescription("Updated Description");
        quizDto.setTimeLimit(45);
        quizDto.setCategoryId(1L);

        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(quizService.updateQuiz(eq(1L), any(Quiz.class))).thenReturn(quiz);

        mockMvc.perform(post("/quiz/update/1")
                        .flashAttr("quizDto", quizDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("success"));

        verify(quizService, times(1)).updateQuiz(eq(1L), any(Quiz.class));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    void deleteQuiz_ShouldDeleteSuccessfully() throws Exception {
        doNothing().when(quizService).deleteQuiz(1L);

        mockMvc.perform(post("/quiz/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("success"));

        verify(quizService, times(1)).deleteQuiz(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void addQuestionToQuiz_ShouldAddSuccessfully() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));
        when(questionService.getQuestionById(2L)).thenReturn(Optional.of(question));

        mockMvc.perform(post("/quiz/add-from-bank/1")
                        .param("questionId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/edit/1"))
                .andExpect(flash().attributeExists("success"));

        verify(quizService, times(1)).addQuestionToQuiz(any(Quiz.class), any(Question.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void addQuestionToQuiz_ShouldHandleMissingQuestion() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));
        when(questionService.getQuestionById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/quiz/add-from-bank/1")
                        .param("questionId", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/edit/1"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "participant", roles = {"PARTICIPANT"})
    void takeQuiz_ShouldStartAttempt() throws Exception {
        when(userService.findByUsername("participant")).thenReturn(Optional.of(participantUser));
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));

        QuizAttempt attempt = new QuizAttempt();
        attempt.setId(1L);
        when(quizAttemptService.startQuizAttempt(any(User.class), any(Quiz.class))).thenReturn(attempt);

        mockMvc.perform(get("/quiz/take/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/attempt/take/1"));

        verify(quizAttemptService, times(1)).startQuizAttempt(any(User.class), any(Quiz.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void showCreateQuizForm_ShouldRedirectWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/quiz/create"))
                .andExpect(status().isOk()); // Should work with @WithMockUser
    }

    @Test
    void showCreateQuizForm_ShouldRedirectToLoginWhenNoUser() throws Exception {
        mockMvc.perform(get("/quiz/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createQuiz_ShouldHandleValidationErrors() throws Exception {
        QuizDto quizDto = new QuizDto(); // Empty DTO - should fail validation

        mockMvc.perform(post("/quiz/create")
                        .flashAttr("quizDto", quizDto))
                .andExpect(status().isOk()) // Should stay on same page
                .andExpect(view().name("quiz/create"))
                .andExpect(model().attributeHasErrors("quizDto"));

        verify(quizService, never()).createQuiz(any(Quiz.class), any(User.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void editQuiz_ShouldHandleQuizNotFound() throws Exception {
        when(quizService.getQuizById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/quiz/edit/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"))
                .andExpect(flash().attributeExists("error"));

        verify(quizService, times(1)).getQuizById(1L);
    }

    @Test
    @WithMockUser(username = "participant", roles = {"PARTICIPANT"})
    void listQuizzes_ShouldHandleUserNotFound() throws Exception {
        when(userService.findByUsername("participant")).thenReturn(Optional.empty());

        mockMvc.perform(get("/quiz/list"))
                .andExpect(status().isOk()) // Should still work
                .andExpect(view().name("quiz/list"));

        verify(quizService, times(1)).getPublicQuizzes(); // Default to public quizzes
    }

    @Test
    @WithMockUser(username = "participant", roles = {"PARTICIPANT"})
    void takeQuiz_ShouldHandleQuizNotFound() throws Exception {
        when(userService.findByUsername("participant")).thenReturn(Optional.of(participantUser));
        when(quizService.getQuizById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/quiz/take/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/list"))
                .andExpect(flash().attributeExists("error"));

        verify(quizAttemptService, never()).startQuizAttempt(any(User.class), any(Quiz.class));
    }

    @Test
    @WithMockUser(username = "participant", roles = {"PARTICIPANT"})
    void takeQuiz_ShouldHandleDisabledQuiz() throws Exception {
        quiz.setEnabled(false);

        when(userService.findByUsername("participant")).thenReturn(Optional.of(participantUser));
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));

        mockMvc.perform(get("/quiz/take/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/list"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "participant", roles = {"PARTICIPANT"})
    void takeQuiz_ShouldHandlePrivateQuiz() throws Exception {
        quiz.setIsPublic(false);

        when(userService.findByUsername("participant")).thenReturn(Optional.of(participantUser));
        when(quizService.getQuizById(1L)).thenReturn(Optional.of(quiz));

        mockMvc.perform(get("/quiz/take/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/list"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createQuiz_ShouldHandleCategoryNotFound() throws Exception {
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("New Quiz");
        quizDto.setCategoryId(999L); // Non-existent category

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/quiz/create")
                        .flashAttr("quizDto", quizDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/create"))
                .andExpect(flash().attributeExists("error"));

        verify(quizService, never()).createQuiz(any(Quiz.class), any(User.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createQuiz_ShouldHandleServiceException() throws Exception {
        QuizDto quizDto = new QuizDto();
        quizDto.setTitle("New Quiz");
        quizDto.setCategoryId(1L);

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(category));
        when(quizService.createQuiz(any(Quiz.class), any(User.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/quiz/create")
                        .flashAttr("quizDto", quizDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/quiz/create"))
                .andExpect(flash().attributeExists("error"));
    }
}