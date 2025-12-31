package com.nguyenthithuhuyen.example10.entity;

import com.nguyenthithuhuyen.example10.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant_tables") // tránh trùng từ khóa SQL
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity = 4;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.FREE;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "table_number")
    private Integer tableNumber;
 // Nếu không dùng @Data, cần có getter/setter
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
