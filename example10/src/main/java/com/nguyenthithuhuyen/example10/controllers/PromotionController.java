package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.dto.PromotionRequestDTO;
import com.nguyenthithuhuyen.example10.entity.Promotion;
import com.nguyenthithuhuyen.example10.security.services.PromotionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    // ===================== BASIC CRUD =====================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/basic")
    public ResponseEntity<Promotion> savePromotion(@RequestBody Promotion promotion) {
        Promotion saved = promotionService.createPromotion(promotion);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotion(@PathVariable Long id) {
        Promotion promotion = promotionService.getPromotionById(id);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping
    public ResponseEntity<List<Promotion>> getAllPromotions() {
        return ResponseEntity.ok(promotionService.getAllPromotions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Promotion>> getActivePromotions() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return ResponseEntity.ok("Promotion successfully deleted");
    }

    // ===================== PROMOTION + PRODUCT MAPPING =====================

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Promotion> createPromotionWithProducts(@RequestBody PromotionRequestDTO dto) {
        Promotion promotion = promotionService.createPromotionWithProducts(dto);
        return ResponseEntity.status(201).body(promotion);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Promotion> updatePromotionWithProducts(
            @PathVariable Long id,
            @RequestBody PromotionRequestDTO dto
    ) {
        Promotion promotion = promotionService.updatePromotionWithProducts(id, dto);
        return ResponseEntity.ok(promotion);
    }
}
