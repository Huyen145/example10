package com.nguyenthithuhuyen.example10.repository;

import com.nguyenthithuhuyen.example10.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    List<Promotion> findByIsActiveTrue();
}
