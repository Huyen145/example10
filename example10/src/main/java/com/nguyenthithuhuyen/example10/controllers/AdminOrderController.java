package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Order;
import com.nguyenthithuhuyen.example10.security.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
