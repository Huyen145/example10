package com.nguyenthithuhuyen.example10.security.services.impl;

import com.nguyenthithuhuyen.example10.entity.Product;
import com.nguyenthithuhuyen.example10.entity.Promotion;
import com.nguyenthithuhuyen.example10.entity.PromotionProduct;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import com.nguyenthithuhuyen.example10.repository.PromotionProductRepository;
import com.nguyenthithuhuyen.example10.repository.PromotionRepository;
import com.nguyenthithuhuyen.example10.security.services.PromotionProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionProductServiceImpl implements PromotionProductService {

    private final PromotionProductRepository promotionProductRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public PromotionProduct create(PromotionProduct promotionProduct) {
        // ✅ Lấy Product và Promotion thật từ DB để tránh null hoặc lazy proxy
        Product product = productRepository.findById(promotionProduct.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("❌ Product not found with ID: " + promotionProduct.getProduct().getId()));

        Promotion promotion = promotionRepository.findById(promotionProduct.getPromotion().getId())
                .orElseThrow(() -> new RuntimeException("❌ Promotion not found with ID: " + promotionProduct.getPromotion().getId()));

        // ✅ Gán lại đối tượng đầy đủ
        promotionProduct.setProduct(product);
        promotionProduct.setPromotion(promotion);

        // ✅ Lưu và trả về entity kèm dữ liệu đầy đủ
        PromotionProduct saved = promotionProductRepository.save(promotionProduct);

        // ✅ Trả về bản có thông tin đầy đủ sau khi save
        return promotionProductRepository.findByIdWithDetails(saved.getId())
                .orElse(saved);
    }

    @Override
    public List<PromotionProduct> getByProductId(Long productId) {
        return promotionProductRepository.findByProductIdWithDetails(productId);
    }

    @Override
    public List<PromotionProduct> getByPromotionId(Long promotionId) {
        return promotionProductRepository.findByPromotionIdWithDetails(promotionId);
    }

    @Override
    public void delete(Long id) {
        promotionProductRepository.deleteById(id);
    }
}
