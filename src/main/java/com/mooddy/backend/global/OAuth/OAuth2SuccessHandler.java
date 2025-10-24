package com.mooddy.backend.global.OAuth;

import com.mooddy.backend.external.spotify.domain.SpotifyToken;
import com.mooddy.backend.external.spotify.repository.SpotifyTokenRepository;
import com.mooddy.backend.feature.user.domain.AuthProvider;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.global.security.JwtService;
import com.mooddy.backend.feature.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final SpotifyTokenRepository spotifyTokenRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String registrationId = token.getAuthorizedClientRegistrationId();

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        log.info("OAuth2User Attributes: {}", oAuth2User.getAttributes());
        log.info("Principal: {}", authentication.getPrincipal());

        // 이메일과 프로필 이미지만 가져오기 (Google / Spotify)
        String email = oAuth2User.getAttribute("email");
        String avatarUrl = oAuth2User.getAttribute("picture");

        final String finalEmail = email;
        final String finalAvatarUrl = avatarUrl;

        // DB에서 사용자 조회 또는 신규 생성
        User user = userRepository.findByEmail(finalEmail)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(finalEmail);
                    newUser.setNickname(generateNickname(finalEmail));
                    newUser.setPassword("");
                    newUser.setEnabled(true);
                    newUser.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Spotify OAuth 로그인 시 액세스 토큰을 가져와 User 객체에 저장
        if ("spotify".equalsIgnoreCase(registrationId)) {
            // Spotify용 OAuth2AuthorizedClient를 조회
            OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService
                    .loadAuthorizedClient(registrationId, authentication.getName());

            if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                String spotifyAccessToken = authorizedClient.getAccessToken().getTokenValue();
                Instant expiresAt = authorizedClient.getAccessToken().getExpiresAt();
                Long expiresIn = Duration.between(Instant.now(), expiresAt).getSeconds();

                log.info("Spotify 토큰: {}", spotifyAccessToken);

                // DB에 기존 SpotifyToken 조회, 없으면 새로 생성
                SpotifyToken spotifyToken = spotifyTokenRepository.findByUser(user)
                        .orElse(SpotifyToken.builder().user(user).build());

                // 만료된 토큰 삭제 및 새로 생성
                if (spotifyToken.isExpired()) {
                    spotifyTokenRepository.delete(spotifyToken);
                    spotifyToken = SpotifyToken.builder().user(user).build();
                }

                spotifyToken.setAccessToken(spotifyAccessToken);
                spotifyToken.setExpiresIn(expiresIn);
                spotifyToken.setCreatedAt(LocalDateTime.now());

                spotifyTokenRepository.save(spotifyToken);
            }
        }

        // JWT 발급
        String accessToken = jwtService.generateToken(user);
        log.info("JWT 발급됨: {}", accessToken);
        String refreshToken = jwtService.generateRefreshToken(user);

        // 프론트로 리다이렉트 (나중에 프론트 연결 시 사용)
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/callback")
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 이메일 기반으로 중복 없는 닉네임 생성
    private String generateNickname(String email) {
        String baseUsername = email.split("@")[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByNickname(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }
}