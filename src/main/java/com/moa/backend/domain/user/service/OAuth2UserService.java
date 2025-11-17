package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.SocialUser;
import com.moa.backend.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ✅ OAuth2UserService
 *
 * Spring Security의 DefaultOAuth2UserService를 확장하여
 * 소셜 로그인 사용자 정보를 우리 시스템의 User로 변환 및 처리.
 *
 * 주요 기능:
 *  - Google, Kakao 등 OAuth2 provider로부터 사용자 정보 수집
 *  - SocialUser DTO로 표준화
 *  - 신규 사용자 자동 등록 or 기존 사용자 업데이트
 *  - User 엔티티에 로그인 기록(lastLoginAt) 업데이트
 *  - SecurityContext에 사용할 DefaultOAuth2User 반환
 */
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본적으로 OAuth2 사용자 정보 가져오기
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 2. 어떤 제공자인지 확인 (google, kakao 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        logger.info("OAuth2 로그인 시도: 제공자={}, 사용자속성명={}", registrationId, userNameAttributeName);

        try {
            // 3. 소셜 사용자 정보 변환
            SocialUser socialUser = SocialUser.from(registrationId, oauth2User);

            // 4. 사용자 처리 (생성 또는 업데이트)
            User user = userService.handleSocialLogin(socialUser);

            // 5. 마지막 로그인 시간 업데이트
            user.setLastLoginAt(LocalDateTime.now());
            userService.save(user);

            logger.info("OAuth2 로그인 성공: 사용자ID={}, 제공자={}, 이메일={}",
                    user.getId(), registrationId, user.getEmail());

            // 6. attributes에 provider 및 사용자 정보 추가
            Map<String, Object> enhancedAttributes = new HashMap<>(oauth2User.getAttributes());
            enhancedAttributes.put("provider", registrationId);
            enhancedAttributes.put("email", user.getEmail());
            enhancedAttributes.put("name", user.getName());
            enhancedAttributes.put("userId", user.getId());

            // 7. Spring Security OAuth2User 반환
            return new DefaultOAuth2User(
                    Collections.singleton(() -> "ROLE_USER"),
                    enhancedAttributes,
                    userNameAttributeName
            );

        } catch (Exception e) {
            logger.error("OAuth2 사용자 처리 중 오류 발생: 제공자={}, 오류={}",
                    registrationId, e.getMessage(), e);
            throw new OAuth2AuthenticationException("사용자 정보 처리 실패: " + e.getMessage());
        }
    }
}
