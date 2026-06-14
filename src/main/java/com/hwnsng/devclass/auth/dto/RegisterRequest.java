package com.hwnsng.devclass.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 4, max = 100)
    private String password;

    @NotBlank @Size(min = 2, max = 50)
    private String name;

    @NotBlank @Pattern(regexp = "STUDENT|INSTRUCTOR")
    private String role;
}
