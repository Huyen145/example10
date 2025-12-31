package com.nguyenthithuhuyen.example10.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Đường dẫn prefix mà client sẽ SUBSCRIBE để nhận tin (ví dụ: /topic/orders)
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefix cho client SEND tin (ví dụ: /app/order)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Cho phép tất cả client (React, Angular, Vue, nhiều tab...) cùng kết nối
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // ⭐ Cho phép nhiều kết nối & domain khác nhau
                .withSockJS(); // Dùng SockJS để tương thích trình duyệt cũ
    }
}
