package com.nguyenthithuhuyen.example10.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // Kiểu tên role (ROLE_ADMIN, ROLE_USER)
    @Column(nullable = false, unique = true)
    private ERole name;
}
