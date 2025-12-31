package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.User;
import com.nguyenthithuhuyen.example10.model.ERole;
import com.nguyenthithuhuyen.example10.model.Role;
import com.nguyenthithuhuyen.example10.payload.request.LoginRequest;
import com.nguyenthithuhuyen.example10.payload.request.SignUpRequest;
import com.nguyenthithuhuyen.example10.payload.response.JwtResponse;
import com.nguyenthithuhuyen.example10.payload.response.MessageResponse;
import com.nguyenthithuhuyen.example10.repository.RoleRepository;
import com.nguyenthithuhuyen.example10.repository.UserRepository;
import com.nguyenthithuhuyen.example10.security.jwt.JwtUtils;
import com.nguyenthithuhuyen.example10.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    // ----------------- Đăng nhập -----------------
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
System.out.println("Logged in user: " + userDetails.getUsername());
userDetails.getAuthorities().forEach(a -> System.out.println("ROLE: " + a.getAuthority()));
        // Lấy tất cả roles của user
        List<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    // ----------------- Đăng ký -----------------
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        // Gán role (FE gửi list roles hoặc mặc định ROLE_USER)
        Set<Role> roles = new HashSet<>();

        if (signUpRequest.getRole() != null && !signUpRequest.getRole().isEmpty()) {
            for (String r : signUpRequest.getRole()) {
                ERole eRole;
                try {
                    eRole = ERole.valueOf(r.toUpperCase());
                } catch (IllegalArgumentException ex) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Invalid role: " + r));
                }

                Role roleEntity = roleRepository.findByName(eRole)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found: " + r));

                roles.add(roleEntity);
            }
        } else {
            // Mặc định ROLE_USER
            Role defaultRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Default role not found"));
            roles.add(defaultRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
