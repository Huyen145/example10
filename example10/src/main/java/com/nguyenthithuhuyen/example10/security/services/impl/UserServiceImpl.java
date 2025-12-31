package com.nguyenthithuhuyen.example10.security.services.impl;

import com.nguyenthithuhuyen.example10.entity.User;
import com.nguyenthithuhuyen.example10.model.ERole;
import com.nguyenthithuhuyen.example10.model.Role; // entity Role đúng
import com.nguyenthithuhuyen.example10.payload.request.UserRequest;
import com.nguyenthithuhuyen.example10.repository.RoleRepository;
import com.nguyenthithuhuyen.example10.repository.UserRepository;
import com.nguyenthithuhuyen.example10.security.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Page<User> getUsersByRole(String roleName, Pageable pageable) {
        ERole eRole;
        try {
            eRole = ERole.valueOf(roleName);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Role không hợp lệ: " + roleName);
        }
        return userRepository.findDistinctByRoles_Name(eRole, pageable);
    }

    @Override
    public User createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setImageUrl(request.getImageUrl());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = mapRolesFromStrings(request.getRoles());
        if (roles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            roles.add(userRole);
        }
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setImageUrl(request.getImageUrl());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUserRoles(Long id, List<String> rolesList) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = mapRolesFromStrings(rolesList);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Chuyển String role từ FE -> ERole -> Role entity
    private Set<Role> mapRolesFromStrings(List<String> roleNames) {
        Set<Role> roles = new HashSet<>();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                try {
                    ERole eRole = ERole.valueOf(roleName);
                    Role role = roleRepository.findByName(eRole)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                    roles.add(role);
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("Role không hợp lệ: " + roleName);
                }
            }
        }
        return roles;
    }
}
