package com.moa.backend.global.config;

import com.moa.backend.global.oauth.OAuth2AuthenticationSuccessHandler;
import com.moa.backend.global.security.RestAccessDeniedHandler;
import com.moa.backend.global.security.RestAuthenticationEntryPoint;
import com.moa.backend.global.security.jwt.JwtAuthenticationFilter;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import com.moa.backend.domain.user.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2UserService oAuth2UserService; // ✅ 카카오/구글 사용자 정보 처리 서비스
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler; // ✅ 로그인 성공 시 JWT 발급 핸들러
    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                // ✅ CORS 설정 활성화 (아래 corsConfigurationSource() Bean 사용)
                .cors(cors -> {})  //

                // ✅ CSRF 및 H2 콘솔 설정 (dev 프로필에서만 콘솔 전용 예외 적용)
                .csrf(csrf -> {
                    if (h2ConsoleEnabled) {
                        csrf.ignoringRequestMatchers("/h2-console/**");
                    }
                    csrf.disable();
                })
                .headers(headers -> headers.frameOptions(frame -> {
                    if (h2ConsoleEnabled) {
                        frame.disable();
                    } else {
                        frame.sameOrigin();
                    }
                }))

                // ✅ 기본 폼 로그인 및 세션 비활성화 (JWT 기반 인증)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 예외 처리
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler())
                )

                // ✅ 요청별 인가 정책
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/actuator/health",
                                "/api/health",
                                "/h2-console/**",
                                "/login",        // 🔥 추가
                                "/login/**",      // 🔥 필요하면 같이
                                "/auth/**",
                                "/project/search",
                                "/project/category",
                                "/project/closing-soon",
                                "/project/id/**",
                                "/uploads/**"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // ✅ OAuth2 로그인 (카카오/구글)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            // 🔥 여기서 에러 로그 찍기
                            String message = exception.getMessage();
                            log.error("❌ OAuth2 로그인 실패: {}", message, exception);

                            String frontendUrl = "http://localhost:5173/login";
                            String redirect = frontendUrl
                                    + "?social=google&error="
                                    + java.net.URLEncoder.encode(
                                    message != null ? message : "OAuth2 login failed",
                                    java.nio.charset.StandardCharsets.UTF_8
                            );

                            response.sendRedirect(redirect);
                        })
                )

                // ✅ JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * ✅ JWT 인증 필터 등록
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    /**
     * ✅ CORS 설정
     * - 프론트엔드 개발 서버: http://localhost:5173 허용
     * - Credential(JWT 쿠키/Authorization 헤더) 포함 요청 허용
     * - 모든 HTTP 메서드/헤더 허용
     */
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

        // ✅ 프론트엔드 주소(Origin) 허용 (Vite dev server)
        config.setAllowedOriginPatterns(java.util.List.of(
                "http://localhost:5173",
                "https://frontend-97n5meqb9-jinhyuns-projects-6d19dc50.vercel.app/"
                // "https://moa-frontend.vercel.app"  // 나중에 실제 도메인 나오면 이렇게 명시적으로 추가해도 됨
        ));

        // ✅ 인증 정보(쿠키, Authorization 헤더) 포함한 요청 허용
        config.setAllowCredentials(true);

        // ✅ 허용할 HTTP 메서드
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // ✅ 허용할 요청 헤더 (프론트에서 보내는 헤더들)
        config.setAllowedHeaders(java.util.List.of(
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin"
        ));

        // ✅ 프론트에서 읽을 수 있는 응답 헤더
        config.setExposedHeaders(java.util.List.of(
                "Authorization",
                "Location"
        ));

        // ✅ 모든 경로에 대해 위 설정 적용
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
