package com.nguyenthithuhuyen.example10.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductWithPromotionsDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private String imageUrl;
    private String categoryName;
    private List<PromotionDTO> promotions;
}
