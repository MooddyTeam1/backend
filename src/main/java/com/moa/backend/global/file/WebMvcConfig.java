// src/main/java/com/moa/backend/global/file/WebMvcConfig.java
package com.moa.backend.global.file;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 정적 리소스(업로드된 이미지 파일)를 서빙하기 위한 WebMvc 설정
 * - "/uploads/**" 경로로 들어온 요청을 로컬 디스크의 실제 파일로 매핑
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LocalFileStorageProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ 로컬 디스크 상의 업로드 루트 경로 (예: ./uploads)
        Path rootPath = Paths.get(properties.getRootDir()).toAbsolutePath().normalize();

        // ✅ "file:" 스킴을 붙여줘야 Spring 이 파일 시스템 경로로 인식
        String location = "file:" + rootPath.toString() + "/";

        // ✅ "/uploads/**" 로 들어오는 URL 을 로컬 디스크의 rootDir 로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);
    }
}
/**
 * 프로필 이미지 저장/접근 경로 예시
 *
 * 실제 파일 경로(Windows):
 *   C:/workspace/moa/uploads/images/profile/xxx.png
 *
 * 브라우저 접근 URL:
 *   http://localhost:8080/uploads/images/profile/xxx.png
 */