package com.nguyenthithuhuyen.example10.security.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.nguyenthithuhuyen.example10.entity.Category;
import com.nguyenthithuhuyen.example10.security.services.CategoryService;
import com.nguyenthithuhuyen.example10.repository.CategoryRepository;

import java.util.List;


@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

@Override
public Category updateCategory(Category category) {
    Category existingCategory = categoryRepository.findById(category.getId())
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + category.getId()));

    existingCategory.setName(category.getName());
    existingCategory.setDescription(category.getDescription());
    existingCategory.setImageUrl(category.getImageUrl());
    existingCategory.setUpdatedAt(category.getUpdatedAt());

    return categoryRepository.save(existingCategory);
}

    @Override
    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
