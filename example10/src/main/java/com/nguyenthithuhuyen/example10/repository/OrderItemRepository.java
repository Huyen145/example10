package com.nguyenthithuhuyen.example10.repository;

import com.nguyenthithuhuyen.example10.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
