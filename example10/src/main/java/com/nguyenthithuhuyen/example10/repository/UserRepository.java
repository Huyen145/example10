package com.nguyenthithuhuyen.example10.repository;

import com.nguyenthithuhuyen.example10.entity.User;
import com.nguyenthithuhuyen.example10.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // Thay String bằng ERole
    Page<User> findDistinctByRoles_Name(ERole roleName, Pageable pageable);
}
