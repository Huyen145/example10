package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.entity.*;
import com.nguyenthithuhuyen.example10.entity.enums.OrderStatus;
import com.nguyenthithuhuyen.example10.entity.enums.Status;
import com.nguyenthithuhuyen.example10.model.ERole;
import com.nguyenthithuhuyen.example10.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TableRepository tableRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
public Order createOrder(Order order, String username, boolean isStaff) {
    try {
        // 1️⃣ Lấy user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        order.setUser(user);

        // 2️⃣ Kiểm tra bàn
        if (order.getTable() == null || order.getTable().getId() == null)
            throw new RuntimeException("Table is required");

        TableEntity table = tableRepository.findById(order.getTable().getId())
                .orElseThrow(() -> new RuntimeException("Table not found: " + order.getTable().getId()));

        if (table.getStatus() != Status.FREE && table.getStatus() != Status.OCCUPIED)
            throw new RuntimeException("Table is not available");

        order.setTable(table);

        // 3️⃣ Kiểm tra orderItems
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty())
            throw new RuntimeException("Order must contain at least one orderItem");

        // 4️⃣ Tính tổng tiền và gán Product cho OrderItem
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null)
                throw new RuntimeException("Product id is required for each order item");

            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));

            item.setProduct(product);
            item.setOrder(order); // ✅ cực kỳ quan trọng

            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0)
                item.setPrice(product.getPrice());

            BigDecimal qty = BigDecimal.valueOf(item.getQuantity() != null ? item.getQuantity() : 1);
            BigDecimal subtotal = item.getPrice().multiply(qty);
            item.setSubtotal(subtotal);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);

        // 5️⃣ Xử lý promotion (nếu có)
        BigDecimal discount = BigDecimal.ZERO;
        if (order.getPromotion() != null && order.getPromotion().getId() != null) {
            Promotion promotion = promotionRepository.findById(order.getPromotion().getId())
                    .orElseThrow(() -> new RuntimeException("Promotion not found: " + order.getPromotion().getId()));

            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(promotion.getStartDate()) && now.isBefore(promotion.getEndDate()) && promotion.getIsActive()) {
                order.setPromotion(promotion);
                if (promotion.getDiscountPercent() != null)
                    discount = total.multiply(BigDecimal.valueOf(promotion.getDiscountPercent()))
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                else if (promotion.getDiscountAmount() != null)
                    discount = promotion.getDiscountAmount();
            }
        }

        order.setDiscount(discount);
        order.setFinalAmount(total.subtract(discount));

        // 6️⃣ Cập nhật trạng thái bàn
        if (table.getStatus() == Status.FREE) {
            table.setStatus(Status.OCCUPIED);
            tableRepository.save(table);
        }

        // 7️⃣ Set các trường khác
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        // 8️⃣ Lưu Order cùng OrderItems (cascade)
        Order saved = orderRepository.save(order);

        // 9️⃣ Gửi WebSocket
        messagingTemplate.convertAndSend("/topic/orders", saved);

        return saved;

    } catch (Exception e) {
        log.error("createOrder failed: {}", e.getMessage(), e);
        throw e;
    }
}
    public Order getOrderByIdAndCheckOwner(Long orderId, String username) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy danh sách tên role của user
        Set<String> roleNames = currentUser.getRoles().stream()
                .map(r -> r.getName().name()) // Lấy enum ERole thành String
                .collect(Collectors.toSet());

        // Nếu không phải admin/moderator → chỉ cho xem đơn của chính mình
        if (!roleNames.contains("ROLE_ADMIN") && !roleNames.contains("ROLE_MODERATOR")) {
            if (!order.getUser().getUsername().equals(username)) {
                throw new RuntimeException("Access denied");
            }
        }
        return order;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED) {
            TableEntity table = order.getTable();
            if (table != null) {
                table.setStatus(Status.FREE);
                tableRepository.save(table);
            }
        }

        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    // ================= REPORT =================

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTopSellingProducts(int topN) {
        return orderRepository.findTopSellingProducts(OrderStatus.PAID, PageRequest.of(0, topN));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRevenueByCategory() {
        return orderRepository.findRevenueByCategory(OrderStatus.PAID);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getRevenueByDay() {
        return orderRepository.findRevenueByDay(OrderStatus.PAID);
    }

    public Order getOpenOrderByTableId(Long tableId) {
    // Trạng thái OPEN hoặc PENDING / PREPARING
return orderRepository.findFirstByTable_IdAndStatusIn(
    tableId,
    Arrays.asList(OrderStatus.PENDING, OrderStatus.PREPARING)
).orElse(null);
}
public List<Order> findByTableIdAndStatus(Long tableId, OrderStatus status) {
    if (status == null) {
        return orderRepository.findByTable_Id(tableId);
    }
    return orderRepository.findByTable_IdAndStatus(tableId, status);
}

}
