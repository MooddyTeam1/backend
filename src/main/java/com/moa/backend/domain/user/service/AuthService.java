package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.*;
import com.moa.backend.domain.user.entity.RefreshToken;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.RefreshTokenRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * ✅ 회원가입
     * - 이메일 중복 검증
     * - 비밀번호 암호화 후 User 엔티티 생성 및 저장
     * - 프로필/지갑 자동 초기화 (UserService 내부에서 Cascade로 처리)
     */
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (userService.findByEmail(request.email()).isPresent()) {
            throw new AppException(ErrorCode.BUSINESS_CONFLICT, "이미 가입된 이메일입니다.");
        }

        // ✅ UserService 내부에서만 save() 호출 (SupporterProfile 중복 save 금지)
        User saved = userService.registerUser(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name()
        );

        return new SignUpResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

    /**
     * ✅ 로그인 (이메일/비밀번호 검증 후 JWT 발급)
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userService.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return issueTokens(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * ✅ Refresh Token 재발급
     */
    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {
        LocalDateTime now = LocalDateTime.now();

        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."));

        if (storedToken.isExpired(now) || storedToken.isRevoked()) {
            refreshTokenRepository.delete(storedToken);
            throw new AppException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");
        }

        JwtUserPrincipal principal = jwtTokenProvider.getPrincipalFromRefreshToken(request.refreshToken());

        if (!storedToken.getUserId().equals(principal.getId())) {
            refreshTokenRepository.delete(storedToken);
            throw new AppException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
        }

        refreshTokenRepository.delete(storedToken);
        return issueTokens(principal.getId(), principal.getUsername(), principal.getRole());
    }

    /**
     * ✅ OAuth2 로그인 시 JWT 발급
     */
    @Transactional
    public LoginResponse issueTokensForOAuthLogin(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        return issueTokens(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * ✅ Access / Refresh Token 발급 공통 로직
     */
    private LoginResponse issueTokens(Long userId, String email, String role) {
        // Access Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(userId, email, role);

        // Refresh Token 생성
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId, email, role);

        // 기존 Refresh Token 모두 제거
        refreshTokenRepository.deleteAllByUserId(userId);

        // 새 Refresh Token 저장
        RefreshToken refreshTokenEntity = RefreshToken.issue(
                userId,
                refreshToken,
                LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds())
        );
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(
                userId,
                email,
                role,
                "Bearer",
                accessToken,
                jwtTokenProvider.getAccessTokenValidityInSeconds(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidityInSeconds()
        );
    }
}
