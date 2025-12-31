package com.nguyenthithuhuyen.example10.security.services.impl;

import com.nguyenthithuhuyen.example10.dto.PromotionRequestDTO;
import com.nguyenthithuhuyen.example10.entity.Product;
import com.nguyenthithuhuyen.example10.entity.Promotion;
import com.nguyenthithuhuyen.example10.entity.PromotionProduct;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import com.nguyenthithuhuyen.example10.repository.PromotionProductRepository;
import com.nguyenthithuhuyen.example10.repository.PromotionRepository;
import com.nguyenthithuhuyen.example10.security.services.PromotionService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductRepository productRepository;

    // ============================================================
    // BASIC CRUD
    // ============================================================

    @Override
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found with id: " + promotionId));
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Promotion updatePromotion(Promotion promotion) {
        Promotion existing = getPromotionById(promotion.getId());

        existing.setName(promotion.getName());
        existing.setDiscountAmount(promotion.getDiscountAmount());
        existing.setStartDate(promotion.getStartDate());
        existing.setEndDate(promotion.getEndDate());
        existing.setIsActive(promotion.getIsActive());
        existing.setUpdatedAt(LocalDateTime.now());

        return promotionRepository.save(existing);
    }

    @Override
    public void deletePromotion(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new RuntimeException("Promotion not found with id: " + promotionId);
        }
        promotionRepository.deleteById(promotionId);
    }

    @Override
    public List<Promotion> getActivePromotions() {
        return promotionRepository.findByIsActiveTrue();
    }

    // ============================================================
    // CREATE + PRODUCT MAPPING
    // ============================================================

    @Override
    @Transactional
    public Promotion createPromotionWithProducts(PromotionRequestDTO dto) {

        Promotion promotion = Promotion.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .discountPercent(dto.getDiscountPercent())
                .discountAmount(dto.getDiscountAmount())
                .startDate(dto.getStartDate().atStartOfDay())          // 🔥 convert LocalDate → LocalDateTime
                .endDate(dto.getEndDate().atTime(23, 59, 59))          // 🔥 convert LocalDate → LocalDateTime
                .isActive(dto.getIsActive())
                .build();

        promotion = promotionRepository.save(promotion);

        // Tạo mapping sản phẩm
        if (dto.getProductIds() != null) {
            for (Long productId : dto.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                PromotionProduct pp = new PromotionProduct();
                pp.setProduct(product);
                pp.setPromotion(promotion);
                promotionProductRepository.save(pp);
            }
        }

        return promotion;
    }

    // ============================================================
    // UPDATE + PRODUCT MAPPING
    // ============================================================

    @Override
    @Transactional
    public Promotion updatePromotionWithProducts(Long promotionId, PromotionRequestDTO dto) {

        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Promotion not found: " + promotionId));

        promotion.setName(dto.getName());
        promotion.setDescription(dto.getDescription());
        promotion.setDiscountPercent(dto.getDiscountPercent());
        promotion.setDiscountAmount(dto.getDiscountAmount());
        promotion.setStartDate(dto.getStartDate().atStartOfDay());      // 🔥 FIX
        promotion.setEndDate(dto.getEndDate().atTime(23, 59, 59));      // 🔥 FIX
        promotion.setIsActive(dto.getIsActive());

        promotion = promotionRepository.save(promotion);

        // Xóa mapping cũ
        promotionProductRepository.deleteByPromotionId(promotionId);

        // Tạo mapping mới
        if (dto.getProductIds() != null) {
            for (Long productId : dto.getProductIds()) {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                PromotionProduct pp = new PromotionProduct();
                pp.setProduct(product);
                pp.setPromotion(promotion);
                promotionProductRepository.save(pp);
            }
        }

        return promotion;
    }
}
