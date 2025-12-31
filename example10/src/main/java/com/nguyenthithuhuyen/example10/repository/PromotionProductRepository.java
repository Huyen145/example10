package com.nguyenthithuhuyen.example10.repository;

import com.nguyenthithuhuyen.example10.entity.PromotionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionProductRepository extends JpaRepository<PromotionProduct, Long> {

    @Query("SELECT pp FROM PromotionProduct pp " +
           "JOIN FETCH pp.product " +
           "JOIN FETCH pp.promotion " +
           "WHERE pp.id = :id")
    Optional<PromotionProduct> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT pp FROM PromotionProduct pp " +
           "JOIN FETCH pp.product " +
           "JOIN FETCH pp.promotion " +
           "WHERE pp.product.id = :productId")
    List<PromotionProduct> findByProductIdWithDetails(@Param("productId") Long productId);

    @Query("SELECT pp FROM PromotionProduct pp " +
           "JOIN FETCH pp.product " +
           "JOIN FETCH pp.promotion " +
           "WHERE pp.promotion.id = :promotionId")
    List<PromotionProduct> findByPromotionIdWithDetails(@Param("promotionId") Long promotionId);
    void deleteByPromotionId(Long promotionId);

}
