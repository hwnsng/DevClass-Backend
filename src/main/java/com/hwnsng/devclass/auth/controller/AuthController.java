package com.hwnsng.devclass.auth.controller;

import com.hwnsng.devclass.auth.dto.AuthResponse;
import com.hwnsng.devclass.auth.dto.LoginRequest;
import com.hwnsng.devclass.auth.dto.RegisterRequest;
import com.hwnsng.devclass.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }
}
