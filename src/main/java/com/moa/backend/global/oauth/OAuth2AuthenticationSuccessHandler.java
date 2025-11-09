package com.moa.backend.global.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.user.dto.LoginResponse;
import com.moa.backend.domain.user.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * ✅ OAuth2AuthenticationSuccessHandler
 *
 * 소셜 로그인 성공 후 호출되는 핸들러.
 * 로그인 성공 시 AccessToken / RefreshToken 발급 후 JSON 형태로 응답.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
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

        // 2️⃣ OAuth2User 객체에서 사용자 식별 정보 추출
        String email = oauth2User.getAttribute("email");
        Long userId = extractUserId(oauth2User.getAttribute("userId"));

        // 3️⃣ 이메일과 userId 둘 다 없을 경우 → 오류 처리
        if (userId == null && email == null) {
            log.error("❌ OAuth2 인증 성공 후 사용자 식별 정보를 찾을 수 없습니다. attributes={}", oauth2User.getAttributes());
            response.sendError(HttpStatus.BAD_REQUEST.value(), "OAuth2 사용자 정보를 찾을 수 없습니다.");
            return;
        }

        // 4️⃣ 사용자 식별 정보 기반으로 JWT AccessToken / RefreshToken 발급
        LoginResponse tokenResponse = authService.issueTokensForOAuthLogin(userId, email);

        // 5️⃣ 응답 헤더 및 바디 설정 (JSON 반환)
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 6️⃣ 프론트엔드로 JWT 정보를 JSON 형태로 응답
        objectMapper.writeValue(response.getWriter(), tokenResponse);

        log.info("✅ OAuth2 로그인 성공 - JWT 발급 완료: userId={}, email={}", userId, email);
    }

    /**
     * ✅ userId 속성 안전 변환 유틸리티
     */
    private Long extractUserId(Object attribute) {
        if (attribute instanceof Number number) {
            return number.longValue();
        }
        if (attribute instanceof String value) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ignored) {
                log.warn("⚠️ OAuth2 userId 속성을 Long으로 변환할 수 없습니다: {}", value);
            }
        }
        return null;
    }
}
