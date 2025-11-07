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

        return switch (provider.toLowerCase()) {
            case "google" -> fromGoogle(attributes);
            case "kakao" -> fromKakao(attributes);
            default -> throw new IllegalArgumentException("지원하지 않는 OAuth 제공자: " + provider);
        };
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

    @SuppressWarnings("unchecked")
    private static SocialUser fromKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null
                ? (Map<String, Object>) kakaoAccount.get("profile")
                : null;

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String name = profile != null ? (String) profile.get("nickname") : null;
        String picture = profile != null ? (String) profile.get("profile_image_url") : null;

        return SocialUser.builder()
                .providerId(String.valueOf(attributes.get("id")))
                .provider("kakao")
                .name(name)
                .email(email)
                .picture(picture)
                .emailVerified(true)
                .attributes(attributes)
                .build();
    }
}
