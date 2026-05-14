package com.hwnsng.devclass.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevClass API")
                        .description("개발자 인강 플랫폼 DevClass API 문서 (P1 - 인증 제외)")
                        .version("v1.0.0"));
    }
}