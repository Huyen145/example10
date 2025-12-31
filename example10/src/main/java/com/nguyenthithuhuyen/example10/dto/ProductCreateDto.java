package com.nguyenthithuhuyen.example10.dto;

import java.math.BigDecimal;

public class ProductCreateDto {
    public String name;
    public String description;
    public BigDecimal price;
    public String imageUrl;
    public Integer stockQuantity;
    public Boolean isActive;
    public Long categoryId;
}
