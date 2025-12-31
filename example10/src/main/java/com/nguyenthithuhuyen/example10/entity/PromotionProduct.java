package com.nguyenthithuhuyen.example10.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promotion_products")
public class PromotionProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Khi trả JSON, không cho Jackson load lazy proxy -> tránh lỗi & vòng lặp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "promotionProducts"}) 
    private Promotion promotion;

@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({ "promotionProducts" }) // ✅ Bỏ qua mối quan hệ ngược lại
    private Product product;}
