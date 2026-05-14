package com.hwnsng.devclass.auth.service;

import com.hwnsng.devclass.auth.dto.AuthResponse;
import com.hwnsng.devclass.auth.dto.LoginRequest;
import com.hwnsng.devclass.auth.dto.RegisterRequest;
import com.hwnsng.devclass.auth.jwt.JwtTokenProvider;
import com.hwnsng.devclass.common.exception.CustomException;
import com.hwnsng.devclass.user.entity.User;
import com.hwnsng.devclass.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtProvider;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new CustomException(HttpStatus.CONFLICT, "EMAIL_DUPLICATE", "이미 사용 중인 이메일입니다.");
        }
        User.Role role = parseRole(req.getRole());
        User user = User.create(req.getEmail(), passwordEncoder.encode(req.getPassword()), req.getName(), role);
        userRepository.save(user);
        String token = jwtProvider.generate(user.getId(), user.getRole().name());
        return new AuthResponse(user.getId(), user.getEmail(), user.getName(), user.getRole().name(), token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        if (user.getStatus() == User.UserStatus.INACTIVE) {
            throw new CustomException(HttpStatus.FORBIDDEN, "ACCOUNT_INACTIVE", "비활성화된 계정입니다.");
        }
        String token = jwtProvider.generate(user.getId(), user.getRole().name());
        return new AuthResponse(user.getId(), user.getEmail(), user.getName(), user.getRole().name(), token);
    }

    private User.Role parseRole(String role) {
        if ("INSTRUCTOR".equalsIgnoreCase(role)) return User.Role.INSTRUCTOR;
        return User.Role.STUDENT;
    }
}
