package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Order;
import com.nguyenthithuhuyen.example10.entity.enums.OrderStatus;
import com.nguyenthithuhuyen.example10.security.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SimpMessagingTemplate messagingTemplate;

    // -------------------- Lấy tất cả orders --------------------
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // -------------------- Khách tạo order mới --------------------
    @PreAuthorize("hasAnyRole('USER','MODERATOR')")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Order created = orderService.createOrder(order, username, false); // false = khách
            messagingTemplate.convertAndSend("/topic/orders", created);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -------------------- Nhân viên thêm món hoặc tạo order --------------------
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PostMapping("/staff")
    public ResponseEntity<?> createOrAddOrder(@RequestBody Order order) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Order created = orderService.createOrder(order, username, true); // true = nhân viên
            messagingTemplate.convertAndSend("/topic/orders", created);
            return ResponseEntity.status(201).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -------------------- Lấy order mở theo bàn --------------------
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/table/{tableId}/open")
    public ResponseEntity<Order> getOpenOrderByTable(@PathVariable Long tableId) {
        try {
            Order openOrder = orderService.getOpenOrderByTableId(tableId);
            if (openOrder == null) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(openOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    // -------------------- Update trạng thái order --------------------
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Order updated = orderService.updateOrderStatus(id, Enum.valueOf(OrderStatus.class, status));
            messagingTemplate.convertAndSend("/topic/orders", updated);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // -------------------- Báo cáo --------------------
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/report/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts(@RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(orderService.getTopSellingProducts(topN));
    }

    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/report/revenue-by-category")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByCategory() {
        return ResponseEntity.ok(orderService.getRevenueByCategory());
    }

    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/report/revenue-by-day")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByDay() {
        return ResponseEntity.ok(orderService.getRevenueByDay());
    }

    @GetMapping("/by-table/{tableId}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByTable(
            @PathVariable Long tableId,
            @RequestParam(name = "status", required = false) String statusStr) {

        OrderStatus status = null;
        if (statusStr != null) {
            try {
                status = OrderStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build(); // status không hợp lệ
            }
        }

        List<Order> orders = orderService.findByTableIdAndStatus(tableId, status);
        return ResponseEntity.ok(orders);
    }
}
