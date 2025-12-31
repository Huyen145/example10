package com.nguyenthithuhuyen.example10.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nguyenthithuhuyen.example10.entity.enums.PaymentMethod;
import com.nguyenthithuhuyen.example10.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@OneToOne(fetch = FetchType.LAZY) 
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) 
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    // table đã là EAGER, giữ nguyên
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "table_id")
    private TableEntity table;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private LocalDateTime issuedAt;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        issuedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
