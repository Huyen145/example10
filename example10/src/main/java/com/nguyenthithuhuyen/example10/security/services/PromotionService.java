package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.dto.PromotionRequestDTO;
import com.nguyenthithuhuyen.example10.entity.Promotion;

import java.util.List;

public interface PromotionService {
    Promotion createPromotion(Promotion promotion);
    Promotion getPromotionById(Long promotionId);
    List<Promotion> getAllPromotions();
    Promotion updatePromotion(Promotion promotion);
    void deletePromotion(Long promotionId);
    List<Promotion> getActivePromotions();
    Promotion createPromotionWithProducts(PromotionRequestDTO dto);
Promotion updatePromotionWithProducts(Long id, PromotionRequestDTO dto);

}
