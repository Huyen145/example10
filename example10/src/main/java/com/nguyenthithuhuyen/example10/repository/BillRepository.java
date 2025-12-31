package com.nguyenthithuhuyen.example10.repository;

import com.nguyenthithuhuyen.example10.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
    boolean existsByOrderId(Long orderId);
}

