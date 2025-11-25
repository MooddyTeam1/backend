package com.moa.backend.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    /**
     * Swagger UI 메타데이터와 JWT 보안 스킴을 설정합니다.
     * - UI: /swagger-ui/index.html
     * - 문서: /v3/api-docs
     */
    @Bean
    public OpenAPI openAPI(
            @Value("${spring.application.name:moa-backend}") String appName,
            @Value("${springdoc.version:1.0.0}") String version,
            @Value("${springdoc.server.local:http://localhost:8080}") String localServerUrl,
            @Value("${springdoc.server.prod:https://api.moa.com}") String prodServerUrl
    ) {
        // JWT Bearer 보안 스킴 정의 (Authorization: Bearer <token>)
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("BearerAuth");

        Info info = new Info()
                .title(appName + " API")
                .description("""
                        크라우드펀딩 플랫폼 백엔드 API 문서입니다.
                        JWT Bearer 토큰이 필요한 API는 Swagger UI 우측 상단 Authorize 버튼을 눌러 토큰을 입력하세요.
                        """)
                .version(version)
                .contact(new Contact()
                        .name("MOA Team")
                        .email("support@moa.com")
                        .url("https://moa.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(
                        new Server().url(localServerUrl).description("로컬"),
                        new Server().url(prodServerUrl).description("프로덕션")
                ))
                .components(new Components().addSecuritySchemes("BearerAuth", bearerAuth))
                .addSecurityItem(securityRequirement);
    }
}
