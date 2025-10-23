package com.example.mooddy.config;

import com.example.mooddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    // 프론트엔드 URL (application.yml에서 설정 가능)
    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // 사용자 인증 정보 로드
    @Bean
    public UserDetailsService userDetailsService() {
        return loginId -> {
            try {
                Long userId = Long.parseLong(loginId);
                return userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
            } catch (NumberFormatException e) {
                return userRepository.findByEmail(loginId)
                        .or(() -> userRepository.findByNickname(loginId))
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    // 인증 관리자
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 비밀번호 인코더 (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl)); // CORS 허용할 프론트 URL
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 쿠키 허용
        config.setExposedHeaders(List.of("Authorization")); // 응답 헤더 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
