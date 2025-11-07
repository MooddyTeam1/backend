package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.SocialUser;
import com.moa.backend.domain.user.entity.User;
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

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    private final UserService userService;

    public OAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. 기본 OAuth2UserService로 사용자 정보 로드
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 2. 제공자 정보 추출
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        logger.info("OAuth2 로그인 시도: 제공자={}, 사용자속성명={}",
                registrationId, userNameAttributeName);

        try {
            // 3. 소셜 사용자 정보 변환
            SocialUser socialUser = SocialUser.from(registrationId, oauth2User);

            // 4. 사용자 처리 (생성 또는 업데이트)
            User user = userService.processOAuth2User(socialUser);

            // 5. 마지막 로그인 시간 업데이트
            user.setLastLoginAt(LocalDateTime.now());
            userService.save(user);

            logger.info("OAuth2 로그인 성공: 사용자ID={}, 제공자={}, 이메일={}",
                    user.getId(), registrationId, user.getEmail());

            // 6. attributes에 provider 정보 추가
            Map<String, Object> enhancedAttributes = new HashMap<>(oauth2User.getAttributes());
            enhancedAttributes.put("provider", registrationId);
            enhancedAttributes.put("email", socialUser.getEmail());
            enhancedAttributes.put("name", socialUser.getName());

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

