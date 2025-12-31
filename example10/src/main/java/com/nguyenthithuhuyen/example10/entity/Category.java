package com.nguyenthithuhuyen.example10.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
// ✅ Tránh lỗi serialization khi sử dụng proxy cho lazy loading
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;
    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
@JsonIgnore // <-- Đảm bảo cái này đã được thêm
private List<Product> products;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}