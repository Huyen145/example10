// src/main/java/com/nguyenthithuhuyen/example10/controllers/MenuController.java
package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Product;
import com.nguyenthithuhuyen.example10.security.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Menu không cần bảo mật vì Khách hàng (User không cần đăng nhập) cũng phải xem được
@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;

    /**
     * Lấy toàn bộ Menu, có thể lọc theo Category ID hoặc tìm kiếm theo tên.
     * URL: GET /api/menu?categoryId=3
     * URL: GET /api/menu?search=trà
     * URL: GET /api/menu
     */
    @GetMapping
    public ResponseEntity<List<Product>> getMenu(
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String search
    ) {
        // Ưu tiên lọc theo Category
        if (categoryId != null) {
            return ResponseEntity.ok(menuService.getMenuByCategory(categoryId));
        } 
        
        // Tiếp theo là tìm kiếm theo tên
        else if (search != null && !search.trim().isEmpty()) {
            return ResponseEntity.ok(menuService.searchProducts(search));
        } 
        
        // Mặc định: Lấy toàn bộ Menu
        else {
            return ResponseEntity.ok(menuService.getFullMenu());
        }
    }
    
    /**
     * Lấy chi tiết một sản phẩm theo ID.
     * URL: GET /api/menu/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            Product product = menuService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}