package com.nguyenthithuhuyen.example10.security.services;

import java.util.List;
import com.nguyenthithuhuyen.example10.entity.Category;

public interface CategoryService {

    // Tạo category mới
    Category createCategory(Category category);

    // Lấy category theo ID
    Category getCategoryById(Long categoryId);

    // Lấy tất cả category
    List<Category> getAllCategories();

    // Cập nhật category
    Category updateCategory(Category category);

    // Xóa category theo ID
    void deleteCategory(Long categoryId);
}