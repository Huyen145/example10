package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Product;
import com.nguyenthithuhuyen.example10.entity.Promotion;
import com.nguyenthithuhuyen.example10.entity.PromotionProduct;
import com.nguyenthithuhuyen.example10.security.services.PromotionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import com.nguyenthithuhuyen.example10.repository.PromotionRepository;
import com.nguyenthithuhuyen.example10.repository.PromotionProductRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/promotion-products")
public class PromotionProductController {

    private final PromotionProductService service;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository repository;

    @PostMapping
    public PromotionProduct create(@RequestBody PromotionProduct pp) {
        Product product = productRepository.findById(pp.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Promotion promotion = promotionRepository.findById(pp.getPromotion().getId())
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        pp.setProduct(product);
        pp.setPromotion(promotion);

        return repository.save(pp);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PromotionProduct>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getByProductId(productId));
    }

    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<PromotionProduct>> getByPromotion(@PathVariable Long promotionId) {
        return ResponseEntity.ok(service.getByPromotionId(promotionId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("PromotionProduct deleted");
    }
}
