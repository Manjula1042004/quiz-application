package com.quizapp.controller;

import com.quizapp.dto.CategoryDto;
import com.quizapp.entity.Category;
import com.quizapp.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private Category category;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();

        category = new Category();
        category.setId(1L);
        category.setName("Mathematics");
        category.setDescription("Math related quizzes");
        category.setColor("#FF0000");

        categoryDto = new CategoryDto();
        categoryDto.setName("Science");
        categoryDto.setDescription("Science related quizzes");
        categoryDto.setColor("#00FF00");
    }

    @Test
    void listCategories_ShouldReturnCategories() throws Exception {
        List<Category> categories = Arrays.asList(category);
        when(categoryService.getAllCategories()).thenReturn(categories);

        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categories"))
                .andExpect(model().attributeExists("categories", "categoryDto"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void createCategory_ShouldCreateSuccessfully() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());
        when(categoryService.createCategory(any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/admin/categories/create")
                        .flashAttr("categoryDto", categoryDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"))
                .andExpect(flash().attributeExists("success"));

        verify(categoryService, times(1)).createCategory(any(Category.class));
    }

    @Test
    void createCategory_ShouldHandleValidationErrors() throws Exception {
        CategoryDto invalidDto = new CategoryDto();
        // Empty name should cause validation error

        mockMvc.perform(post("/admin/categories/create")
                        .flashAttr("categoryDto", invalidDto))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categories"));
    }

    @Test
    void createCategory_ShouldHandleServiceException() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Arrays.asList());
        when(categoryService.createCategory(any(Category.class)))
                .thenThrow(new RuntimeException("Category already exists"));

        mockMvc.perform(post("/admin/categories/create")
                        .flashAttr("categoryDto", categoryDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void updateCategory_ShouldUpdateSuccessfully() throws Exception {
        when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(category);

        mockMvc.perform(post("/admin/categories/update/1")
                        .flashAttr("categoryDto", categoryDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"))
                .andExpect(flash().attributeExists("success"));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(Category.class));
    }

    @Test
    void deleteCategory_ShouldDeleteSuccessfully() throws Exception {
        doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(post("/admin/categories/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"))
                .andExpect(flash().attributeExists("success"));

        verify(categoryService, times(1)).deleteCategory(1L);
    }

    @Test
    void deleteCategory_ShouldHandleException() throws Exception {
        doThrow(new RuntimeException("Cannot delete category with quizzes"))
                .when(categoryService).deleteCategory(1L);

        mockMvc.perform(post("/admin/categories/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"))
                .andExpect(flash().attributeExists("error"));
    }
}