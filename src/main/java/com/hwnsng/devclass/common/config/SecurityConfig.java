package com.hwnsng.devclass.common.config;

import com.hwnsng.devclass.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF disable (REST API 필수)
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ CORS 해결 핵심 부분
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();

                    config.setAllowedOrigins(List.of(
                            "https://devclass-zeta.vercel.app",
                            "http://localhost:3000"));

                    config.setAllowedMethods(List.of(
                            "GET", "POST", "PUT", "DELETE", "OPTIONS"));

                    config.setAllowedHeaders(List.of("*"));

                    config.setAllowCredentials(true);

                    return config;
                }))

                // Stateless JWT
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(401);
                            res.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"로그인이 필요합니다.\"}");
                        })
                        .accessDeniedHandler((req, res, ex) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(403);
                            res.getWriter().write("{\"code\":\"FORBIDDEN\",\"message\":\"접근 권한이 없습니다.\"}");
                        }))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()

                        // ✅ 인증 API 전체 허용 (register/login)
                        .requestMatchers("/api/auth/**").permitAll()

                        // public GET API
                        .requestMatchers(HttpMethod.GET, "/api/courses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/lessons/**").permitAll()

                        .anyRequest().authenticated())

                // JWT 필터
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}