package com.nguyenthithuhuyen.example10.model;

import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Set<String> roles; // chỉ cần tên role hoặc roleId, không ánh xạ DB
}
