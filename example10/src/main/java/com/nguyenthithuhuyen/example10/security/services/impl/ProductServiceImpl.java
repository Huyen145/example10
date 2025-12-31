package com.nguyenthithuhuyen.example10.security.services.impl;

import com.nguyenthithuhuyen.example10.dto.ProductWithPromotionsDTO;
import com.nguyenthithuhuyen.example10.dto.PromotionDTO;
import com.nguyenthithuhuyen.example10.entity.Product;
import com.nguyenthithuhuyen.example10.entity.Promotion;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import com.nguyenthithuhuyen.example10.security.services.ProductService;
import com.nguyenthithuhuyen.example10.security.services.PromotionProductService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final PromotionProductService promotionProductService;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findByIdWithCategory(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategory();
    }

    @Override
    public Product updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + product.getId()));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImageUrl(product.getImageUrl());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setStockQuantity(product.getStockQuantity());
        existingProduct.setIsActive(product.getIsActive());
        existingProduct.setUpdatedAt(product.getUpdatedAt());

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
    public List<ProductWithPromotionsDTO> getAllProductsWithActivePromotions() {
    List<Product> products = productRepository.findAllWithCategory();

    return products.stream().map(product -> {
        ProductWithPromotionsDTO dto = new ProductWithPromotionsDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setCategoryName(product.getCategory().getName());

        // Lấy promotion active
        List<PromotionDTO> promotionDTOs = promotionProductService.getByProductId(product.getId())
                .stream()
                .map(pp -> pp.getPromotion())
                .filter(Promotion::getIsActive)
                .map(promo -> {
                    PromotionDTO pdto = new PromotionDTO();
                    pdto.setName(promo.getName());
                    pdto.setDiscountPercent(promo.getDiscountPercent());
                    pdto.setDiscountAmount(promo.getDiscountAmount());
                    pdto.setIsActive(promo.getIsActive());
                    return pdto;
                }).collect(Collectors.toList());

        dto.setPromotions(promotionDTOs);
        return dto;
    }).collect(Collectors.toList());
}

}
