package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.dto.ProductResponseDto;
import com.nguyenthithuhuyen.example10.dto.ProductWithPromotionsDTO;
import com.nguyenthithuhuyen.example10.entity.Category;
import com.nguyenthithuhuyen.example10.entity.Product;
import com.nguyenthithuhuyen.example10.repository.CategoryRepository;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import com.nguyenthithuhuyen.example10.security.services.ProductService;
import com.nguyenthithuhuyen.example10.security.services.impl.ProductServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductServiceImpl productWithPromotionService;
    

    // ======================
    // GET ALL PRODUCTS
    // ======================
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAllWithCategory();
        return ResponseEntity.ok(products);
    }

    // ======================
    // GET PRODUCT BY ID
    // ======================
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(product);
    }

    // ======================
    // CREATE PRODUCT
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product p = new Product();
        p.setName(request.getName());
        p.setDescription(request.getDescription());
        p.setPrice(request.getPrice());
        p.setStockQuantity(request.getStockQuantity());
        p.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        p.setCategory(category);
        p.setImageUrl(request.getImageUrl()); // Lấy URL từ frontend

        Product saved = productRepository.save(p);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDto(saved));
    }

    // ======================
    // UPDATE PRODUCT
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setPrice(request.getPrice());
        existing.setStockQuantity(request.getStockQuantity());
        existing.setIsActive(request.getIsActive() != null ? request.getIsActive() : existing.getIsActive());
        existing.setCategory(category);
        existing.setImageUrl(request.getImageUrl()); // Lấy URL từ frontend

        Product updated = productRepository.save(existing);
        return ResponseEntity.ok(mapToDto(updated));
    }

    // ======================
    // DELETE PRODUCT
    // ======================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted");
    }

    // ======================
    // DTO Mapper
    // ======================
    private ProductResponseDto mapToDto(Product p) {
        ProductResponseDto r = new ProductResponseDto();
        r.id = p.getId();
        r.name = p.getName();
        r.description = p.getDescription();
        r.price = p.getPrice();
        r.imageUrl = p.getImageUrl();
        r.stockQuantity = p.getStockQuantity();
        r.isActive = p.getIsActive();
        r.category = p.getCategory();
        return r;
    }

    // ======================
    // ProductRequest DTO
    // ======================
    public static class ProductRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private Long categoryId;
        private Integer stockQuantity;
        private Boolean isActive;
        private String imageUrl;

        // getters & setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
        public Integer getStockQuantity() { return stockQuantity; }
        public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }
    @GetMapping("/with-active-promotions")
public ResponseEntity<List<ProductWithPromotionsDTO>> getAllProductsWithActivePromotions() {
    return ResponseEntity.ok(productWithPromotionService.getAllProductsWithActivePromotions());
}

}
