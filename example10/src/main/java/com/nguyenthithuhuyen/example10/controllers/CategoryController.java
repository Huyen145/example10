package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Category;
import com.nguyenthithuhuyen.example10.security.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    // Create Category
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.status(201).body(savedCategory);
    }

    // Get Category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get All Categories
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Update Category
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                   @RequestBody Category category) {
        try {
            category.setId(id); // giữ id đồng nhất
            Category updatedCategory = categoryService.updateCategory(category);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete Category
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Category successfully deleted!");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body("Category not found!");
        }
    }
}
