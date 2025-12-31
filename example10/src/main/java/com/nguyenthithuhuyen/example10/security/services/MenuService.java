// src/main/java/com/nguyenthithuhuyen/example10/security/services/MenuService.java
package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.entity.Product;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    
    private final ProductRepository productRepository;

    /**
     * Lấy toàn bộ menu đang hoạt động.
     */
    public List<Product> getFullMenu() {
        return productRepository.findByIsActiveTrue();
    }

    /**
     * Lọc menu theo Danh mục (Category ID).
     */
    public List<Product> getMenuByCategory(Long categoryId) {
        return productRepository.findByCategory_IdAndIsActiveTrue(categoryId);
    }
    
    /**
     * Tìm kiếm sản phẩm theo tên.
     */
    public List<Product> searchProducts(String name) {
        // Kiểm tra null/rỗng để tránh lỗi SQL
        if (name == null || name.trim().isEmpty()) {
            return getFullMenu();
        }
        return productRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(name.trim());
    }
    
    /**
     * Lấy sản phẩm theo ID (có thể dùng cho màn hình chi tiết sản phẩm).
     */
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy sản phẩm #" + id));
    }
}