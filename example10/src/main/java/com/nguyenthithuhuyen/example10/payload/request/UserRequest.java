package com.nguyenthithuhuyen.example10.payload.request;

import lombok.Data;
import java.util.List;

@Data
public class UserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String imageUrl;
    private List<String> roles;
}