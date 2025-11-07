package com.moa.backend.domain.user.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Data
@Builder
public class SocialUser {
    private String providerId;
    private String provider;
    private String name;
    private String email;
    private String picture;
    private boolean emailVerified;
    private Map<String, Object> attributes;

    public static SocialUser from(String provider, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();

        switch (provider.toLowerCase()) {
            case "google":
                return fromGoogle(attributes);
            case "kakao":
                return fromKakao(attributes);
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자: " + provider);
        }
    }

    private static SocialUser fromGoogle(Map<String, Object> attributes) {
        return SocialUser.builder()
                .providerId((String) attributes.get("sub"))
                .provider("google")
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .emailVerified(Boolean.TRUE.equals(attributes.get("email_verified")))
                .attributes(attributes)
                .build();
    }

    private static SocialUser fromGitHub(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String login = (String) attributes.get("login");

        // kakao 사용자가 이메일을 공개하지 않은 경우 처리
        if (email == null) {
            email = login + "@kakao.local"; // 가상 이메일 생성
        }

        // name이 null인 경우 login 사용
        if (name == null) {
            name = login;
        }
        @SuppressWarnings("unchecked")
        private static SocialUser fromKakao(Map<String, Object> attributes) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            return SocialUser.builder()
                    .providerId(String.valueOf(attributes.get("id")))
                    .provider("kakao")
                    .name((String) profile.get("nickname"))
                    .email((String) kakaoAccount.get("email"))
                    .picture((String) profile.get("profile_image_url"))
                    .emailVerified(true) // 카카오는 휴대폰 인증된 것으로 간주
                    .attributes(attributes)
                    .build();
        }
    }
}
