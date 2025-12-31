package com.nguyenthithuhuyen.example10.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nguyenthithuhuyen.example10.entity.Product;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByIsActiveTrue();
    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category")
    List<Product> findAllWithCategory();
    // Tìm kiếm sản phẩm còn hoạt động (Hiển thị cho khách hàng)
    
    // Tìm kiếm sản phẩm theo Category và còn hoạt động
    List<Product> findByCategory_IdAndIsActiveTrue(Long categoryId);
    
    // Tìm kiếm sản phẩm theo tên (cho Nhân viên/Khách hàng tìm kiếm)
    List<Product> findByNameContainingIgnoreCaseAndIsActiveTrue(String name);
}
