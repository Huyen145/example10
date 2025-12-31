package com.nguyenthithuhuyen.example10.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // ✅ Đảm bảo khi trả về Product, nó bao gồm Category
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "category_id", nullable = false)
    // ✅ NGẮT VÒNG LẶP: Không serialize trường "products" bên trong Category
    @JsonIgnoreProperties({ "products" }) 
    private Category category;

    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
@JsonIgnore // <-- Đã thêm
private List<PromotionProduct> promotionProducts;
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}