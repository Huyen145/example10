package com.nguyenthithuhuyen.example10.dto;

import com.nguyenthithuhuyen.example10.entity.Category;
import java.math.BigDecimal;

public class ProductResponseDto {
    public Long id;
    public String name;
    public String description;
    public BigDecimal price;
    public String imageUrl;
    public Integer stockQuantity;
    public Boolean isActive;
    public Category category; // trả nguyên Category đã load
}
