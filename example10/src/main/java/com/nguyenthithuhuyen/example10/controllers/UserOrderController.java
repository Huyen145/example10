package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Order;
import com.nguyenthithuhuyen.example10.security.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/user/orders")
@RequiredArgsConstructor
public class UserOrderController {

    private final OrderService orderService;

    /**
     * Khách hàng gửi đơn hàng trực tiếp, tự động hiển thị trên màn hình nhân viên.
     * @param order Dữ liệu đơn hàng từ Khách hàng.
     * @param principal Thông tin User đang đăng nhập.
     * @return Order đã được tạo.
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order, Principal principal) {
        try {
            // isStaff = false vì đây là API dành cho Khách hàng
            Order saved = orderService.createOrder(order, principal.getName(), false); 
            
            // Trả về 201 CREATED (Tốt hơn 200 OK cho POST)
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Xử lý các lỗi nghiệp vụ như 'Table not found', 'Table not available'
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}