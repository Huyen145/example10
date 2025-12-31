package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.entity.PromotionProduct;

import java.util.List;

public interface PromotionProductService {
    PromotionProduct create(PromotionProduct promotionProduct);
    List<PromotionProduct> getByProductId(Long productId);
    List<PromotionProduct> getByPromotionId(Long promotionId);
    void delete(Long id);
}
