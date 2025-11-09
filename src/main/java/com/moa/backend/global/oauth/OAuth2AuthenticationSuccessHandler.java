package com.moa.backend.global.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.user.dto.LoginResponse;
import com.moa.backend.domain.user.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * ✅ OAuth2AuthenticationSuccessHandler
 *
 * 🔹 역할:
 *   - 카카오, 구글 등 OAuth2 소셜 로그인 성공 시 동작하는 Success Handler.
 *   - Spring Security의 OAuth2 로그인 흐름에서 마지막 단계(성공 시점)에 호출됨.
 *   - 로그인한 사용자의 이메일을 기반으로 JWT AccessToken / RefreshToken을 발급.
 *   - 발급된 토큰을 JSON 형식으로 프론트엔드에 직접 응답함.
 *
 * 🔹 등록 위치:
 *   - SecurityConfig.java → oauth2Login().successHandler(...)
 *
 * 🔹 동작 시나리오:
 *   1. 사용자가 카카오 로그인 동의창에서 승인
 *   2. 카카오가 redirect_uri 로 인가 코드 전달
 *   3. Spring Security가 인가 코드로 Access Token 교환 후 OAuth2User 생성
 *   4. 이 SuccessHandler가 호출되어 JWT 발급 및 JSON 응답 반환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // ✅ JWT 발급 및 RefreshToken 저장을 담당하는 서비스
    private final AuthService authService;

    // ✅ 객체 → JSON 변환을 위한 Jackson ObjectMapper
    private final ObjectMapper objectMapper;

    /**
     * ✅ OAuth2 로그인 성공 시 호출되는 메서드
     *
     * @param request        현재 HTTP 요청 객체
     * @param response       HTTP 응답 객체
     * @param authentication 인증 객체 (OAuth2User 포함)
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 1️⃣ Principal 객체가 OAuth2User 타입인지 검사
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            log.warn("⚠️ OAuth2AuthenticationSuccessHandler 호출 - OAuth2User가 아님: {}", authentication.getPrincipal());
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 2️⃣ OAuth2User 객체에서 이메일 추출
        String email = oauth2User.getAttribute("email");

        // 3️⃣ 이메일이 없을 경우 → 카카오 정책에 따라 제공되지 않았거나 동의 안 됨
        if (email == null) {
            log.error("❌ OAuth2 인증 성공 후 이메일 정보를 찾을 수 없습니다. attributes={}", oauth2User.getAttributes());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "OAuth2 사용자 이메일 정보를 찾을 수 없습니다.");
            return;
        }

        // 4️⃣ 이메일 기반으로 JWT AccessToken / RefreshToken 발급
        // AuthService 내부에서 User 조회 → JWT 생성 → RefreshToken DB 저장 처리
        LoginResponse tokenResponse = authService.issueTokensForOAuthLogin(email);

        // 5️⃣ 응답 헤더 및 바디 설정 (JSON 반환)
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 6️⃣ 프론트엔드로 JWT 정보를 JSON 형태로 응답
        objectMapper.writeValue(response.getWriter(), tokenResponse);

        log.info("✅ OAuth2 로그인 성공 - JWT 발급 완료: {}", email);
    }
}
