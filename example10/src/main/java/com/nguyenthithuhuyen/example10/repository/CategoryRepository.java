package com.nguyenthithuhuyen.example10.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nguyenthithuhuyen.example10.entity.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
