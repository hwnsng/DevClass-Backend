package com.hwnsng.devclass.auth.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
    private String role; // STUDENT | INSTRUCTOR
}
