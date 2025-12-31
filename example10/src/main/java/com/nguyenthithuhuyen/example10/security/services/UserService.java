package com.nguyenthithuhuyen.example10.security.services;

import com.nguyenthithuhuyen.example10.entity.User;
import com.nguyenthithuhuyen.example10.payload.request.UserRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface UserService {
    Page<User> getUsersByRole(String roleName, Pageable pageable);
    User createUser(UserRequest request);
    User updateUser(Long id, UserRequest request);
    User updateUserRoles(Long id, List<String> roles);
    void deleteUser(Long id);
}