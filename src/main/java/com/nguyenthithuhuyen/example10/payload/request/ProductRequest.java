package com.nguyenthithuhuyen.example10.payload.request;
import lombok.Data;
import java.math.BigDecimal;


@Data
public class ProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Long categoryId;
    private Boolean isActive;
    private String imageUrl;
}
