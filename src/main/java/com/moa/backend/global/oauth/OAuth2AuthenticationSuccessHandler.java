package com.moa.backend.global.oauth;

import com.moa.backend.domain.user.dto.LoginResponse;
import com.moa.backend.domain.user.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * ✅ OAuth2AuthenticationSuccessHandler
 *
 * 소셜 로그인 성공 후 호출되는 핸들러.
 * - OAuth2User → 우리 시스템 User 매핑은 OAuth2UserService에서 이미 처리됨
 * - 여기서는 그 User 정보 기반으로 JWT 발급 후
 *   프론트엔드 콜백 URL로 리다이렉트하면서 토큰을 쿼리스트링으로 넘긴다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    // TODO: 나중에 @Value("${app.oauth2.redirect-uri}") 등으로 yml에서 빼는 게 좋음
    private static final String FRONTEND_OAUTH2_CALLBACK_URL =
            "http://localhost:5173/oauth2/callback";

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 1️⃣ Principal 이 OAuth2User 인지 확인
        if (!(authentication.getPrincipal() instanceof OAuth2User oauth2User)) {
            log.warn("⚠️ OAuth2AuthenticationSuccessHandler 호출 - OAuth2User가 아님: {}",
                    authentication.getPrincipal());
            // 부모 기본 동작 (보통 / 로 리다이렉트)
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 2️⃣ OAuth2User 에서 우리가 OAuth2UserService 에서 심어둔 값 꺼내기
        //    - OAuth2UserService 에서 enhancedAttributes.put("email", user.getEmail());
        //    - OAuth2UserService 에서 enhancedAttributes.put("userId", user.getId());
        String email = oauth2User.getAttribute("email");
        Long userId = extractUserId(oauth2User.getAttribute("userId"));

        if (userId == null && email == null) {
            log.error("❌ OAuth2 성공 후 사용자 식별 정보를 찾을 수 없습니다. attributes={}",
                    oauth2User.getAttributes());
            // 실패로 간주하고 에러 페이지로 보내도 됨
            getRedirectStrategy().sendRedirect(
                    request,
                    response,
                    FRONTEND_OAUTH2_CALLBACK_URL + "?error=missing_user_info"
            );
            return;
        }

        // 3️⃣ OAuth 로그인용 JWT 발급 (AuthService 에 구현되어 있다고 가정)
        LoginResponse tokenResponse = authService.issueTokensForOAuthLogin(userId, email);

        // 4️⃣ 프론트 콜백 URL로 리다이렉트 + 쿼리스트링으로 토큰 전달
        String redirectUrl = UriComponentsBuilder
                .fromUriString(FRONTEND_OAUTH2_CALLBACK_URL)
                .queryParam("accessToken", tokenResponse.getAccessToken())
                .queryParam("refreshToken", tokenResponse.getRefreshToken())
                .build()
                .toUriString();

        log.info("✅ OAuth2 로그인 성공 - userId={}, email={}, redirect={}",
                userId, email, redirectUrl);

        // 5️⃣ 실제 리다이렉트 수행
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    /**
     * ✅ userId 속성 안전 변환 유틸
     */
    private Long extractUserId(Object attribute) {
        if (attribute instanceof Number number) {
            return number.longValue();
        }
        if (attribute instanceof String value) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                log.warn("⚠️ OAuth2 userId 속성을 Long으로 변환할 수 없습니다: {}", value);
            }
        }
        return null;
    }
}
