package com.nguyenthithuhuyen.example10.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import com.nguyenthithuhuyen.example10.entity.Order;

@RestController
@RequiredArgsConstructor
public class WebSocketOrderController {

    // Khi client gửi tới /app/order (theo prefix /app trong WebSocketConfig)
    @MessageMapping("/order")
    @SendTo("/topic/orders") // Broadcast đến tất cả client đang subscribe topic này
    public Order broadcastNewOrder(Order order) {
        System.out.println("📦 Đơn hàng mới realtime: " + order);
        return order; // gửi lại cho tất cả client đang nghe /topic/orders
    }
}
