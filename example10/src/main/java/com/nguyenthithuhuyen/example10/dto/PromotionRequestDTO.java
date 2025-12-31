package com.nguyenthithuhuyen.example10.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PromotionRequestDTO {
    private String name;
    private String description;
    private Double discountPercent;
    private BigDecimal discountAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private List<Long> productIds; // ✅ danh sách sản phẩm
}