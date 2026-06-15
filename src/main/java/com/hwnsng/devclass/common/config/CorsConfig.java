package com.hwnsng.devclass.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = getAllowedOrigins();

        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Range", "Accept-Ranges", "Content-Length");

        registry.addMapping("/uploads/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration apiConfiguration = new CorsConfiguration();
        apiConfiguration.setAllowedOrigins(Arrays.asList(getAllowedOrigins()));
        apiConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        apiConfiguration.setAllowedHeaders(Arrays.asList("*"));
        apiConfiguration.setExposedHeaders(Arrays.asList("Content-Range", "Accept-Ranges", "Content-Length"));

        CorsConfiguration uploadConfiguration = new CorsConfiguration();
        uploadConfiguration.setAllowedOrigins(Arrays.asList(getAllowedOrigins()));
        uploadConfiguration.setAllowedMethods(Arrays.asList("GET", "OPTIONS"));
        uploadConfiguration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", apiConfiguration);
        source.registerCorsConfiguration("/uploads/**", uploadConfiguration);
        return source;
    }

    private String[] getAllowedOrigins() {
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 상대/절대 경로 모두 올바른 file URI로 변환
        String absoluteUri = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        if (!absoluteUri.endsWith("/")) absoluteUri += "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absoluteUri);
    }
}
