package com.nguyenthithuhuyen.example10.repository;

import com.nguyenthithuhuyen.example10.entity.Order;
import com.nguyenthithuhuyen.example10.entity.User;
import com.nguyenthithuhuyen.example10.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.Map;
import java.util.Optional;


import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByTable_Id(Long tableId);
List<Order> findByTable_IdAndStatus(Long tableId, OrderStatus status);
Optional<Order> findFirstByTable_IdAndStatusIn(Long tableId, List<OrderStatus> statuses);

    @Query("SELECT new map(p.id as productId, p.name as productName, SUM(oi.quantity) as quantitySold) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.product p " +
           "WHERE o.status = :status " +
           "GROUP BY p.id, p.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Map<String, Object>> findTopSellingProducts(OrderStatus status, Pageable pageable);

    // Doanh thu theo category
    @Query("SELECT new map(c.name as category, SUM(oi.subtotal) as revenue) " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.product p " +
           "LEFT JOIN p.category c " +
           "WHERE o.status = :status " +
           "GROUP BY c.name")
    List<Map<String, Object>> findRevenueByCategory(OrderStatus status);

    // Doanh thu theo ngày
    @Query("SELECT new map(FUNCTION('DATE', o.createdAt) as date, SUM(o.finalAmount) as revenue) " +
           "FROM Order o " +
           "WHERE o.status = :status " +
           "GROUP BY FUNCTION('DATE', o.createdAt) " +
           "ORDER BY FUNCTION('DATE', o.createdAt)")
    List<Map<String, Object>> findRevenueByDay(OrderStatus status);
     Optional<Order> findFirstByTableIdAndStatusIn(Long tableId, List<OrderStatus> statuses);
}
