package com.moa.backend.global.config;

import com.moa.backend.global.oauth.OAuth2AuthenticationSuccessHandler;
import com.moa.backend.global.security.RestAccessDeniedHandler;
import com.moa.backend.global.security.RestAuthenticationEntryPoint;
import com.moa.backend.global.security.jwt.JwtAuthenticationFilter;
import com.moa.backend.global.security.jwt.JwtTokenProvider;

import com.moa.backend.domain.user.service.OAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2UserService oAuth2UserService; // ✅ 카카오 사용자 정보 처리 서비스
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler; // ✅ JWT 발급 핸들러

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ H2 콘솔 및 기본 보안 설정
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                        .accessDeniedHandler(new RestAccessDeniedHandler()))

                // ✅ 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/projects/**"
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/actuator/health",
                                "/api/health",
                                "/h2-console/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // ✅ OAuth2 로그인 설정 추가 (카카오/구글)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(user -> user.userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                // ✅ JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
