package com.nguyenthithuhuyen.example10.security.services;
import java.util.List;
import com.nguyenthithuhuyen.example10.entity.Product;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Long productId);
    List<Product> getAllProducts();
    Product updateProduct(Product product);
    void deleteProduct(Long productId);
}