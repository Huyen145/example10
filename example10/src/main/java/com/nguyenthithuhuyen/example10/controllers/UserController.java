package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.User;
import com.nguyenthithuhuyen.example10.payload.request.UserRequest;
import com.nguyenthithuhuyen.example10.security.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/employees")
    public Page<User> getEmployees(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByRole("ROLE_MODERATOR", PageRequest.of(page, size));
    }

    @GetMapping("/admins")
    public Page<User> getAdmins(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByRole("ROLE_ADMIN", PageRequest.of(page, size));
    }

    @GetMapping("/customers")
    public Page<User> getCustomers(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return userService.getUsersByRole("ROLE_USER", PageRequest.of(page, size));
    }

    @PostMapping("/create")
    public User createUser(@RequestBody UserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        return userService.updateUser(id, request);
    }

    @PutMapping("/{id}/roles")
    public User updateUserRoles(@PathVariable Long id, @RequestBody List<String> roles) {
        return userService.updateUserRoles(id, roles);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}