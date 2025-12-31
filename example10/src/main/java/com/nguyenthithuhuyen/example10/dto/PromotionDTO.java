package com.nguyenthithuhuyen.example10.dto;
import lombok.Data;
import java.math.BigDecimal;


@Data
public class PromotionDTO {
    private String name;
    private Double discountPercent;
    private BigDecimal discountAmount;
    private Boolean isActive;
}