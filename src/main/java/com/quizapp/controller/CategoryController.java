package com.quizapp.controller;

import com.quizapp.dto.CategoryDto;
import com.quizapp.entity.Category;
import com.quizapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        try {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("categoryDto", new CategoryDto());
            return "admin/categories";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading categories: " + e.getMessage());
            return "admin/categories";
        }
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute CategoryDto categoryDto,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/categories";
        }

        try {
            Category category = new Category();
            category.setName(categoryDto.getName());
            category.setDescription(categoryDto.getDescription());
            category.setColor(categoryDto.getColor());

            categoryService.createCategory(category);
            model.addAttribute("success", "Category created successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute CategoryDto categoryDto,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/categories";
        }

        try {
            Category categoryDetails = new Category();
            categoryDetails.setName(categoryDto.getName());
            categoryDetails.setDescription(categoryDto.getDescription());
            categoryDetails.setColor(categoryDto.getColor());

            categoryService.updateCategory(id, categoryDetails);
            model.addAttribute("success", "Category updated successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, Model model) {
        try {
            categoryService.deleteCategory(id);
            model.addAttribute("success", "Category deleted successfully!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}