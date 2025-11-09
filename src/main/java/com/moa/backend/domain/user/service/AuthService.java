package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.*;
import com.moa.backend.domain.user.entity.RefreshToken;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.RefreshTokenRepository;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ✅ AuthService
 *
 * 인증(Authentication)과 JWT 토큰 발급 관련 핵심 로직을 담당하는 서비스 클래스입니다.
 *
 * 주요 기능:
 *  - 회원가입 (signUp)
 *  - 일반 로그인 (login)
 *  - 토큰 재발급 (refresh)
 *  - 소셜 로그인 성공 시 토큰 발급 (issueTokensForOAuthLogin)
 *
 * 모든 경우에서 AccessToken / RefreshToken 발급 로직은
 * 공통 메서드 issueTokens() 에서 처리됩니다.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * ✅ 회원가입
     * - 이메일 중복 검증
     * - 비밀번호 암호화 후 User 엔티티 생성 및 저장
     */
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.BUSINESS_CONFLICT, "이미 가입된 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.createUser(request.email(), encodedPassword, request.name());
        User saved = userRepository.save(user);

        return new SignUpResponse(saved.getId(), saved.getEmail(), saved.getName());
    }

    /**
     * ✅ 기본 로그인
     * - 이메일/비밀번호 검증
     * - 성공 시 AccessToken, RefreshToken 발급 및 저장
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 토큰 발급 (공통 로직 사용)
        return issueTokens(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * ✅ Refresh Token을 통한 Access/Refresh 재발급
     * - DB에 저장된 RefreshToken의 유효성 및 만료 여부 검사
     * - 토큰이 만료/폐기되었을 경우 삭제 및 예외 발생
     * - 새 토큰을 발급하고 기존 토큰 제거
     */
    @Transactional
    public LoginResponse refresh(RefreshTokenRequest request) {
        LocalDateTime now = LocalDateTime.now();

        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."));

        // 만료 또는 폐기된 토큰 처리
        if (storedToken.isExpired(now) || storedToken.isRevoked()) {
            refreshTokenRepository.delete(storedToken);
            throw new AppException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");
        }

        // RefreshToken 에서 사용자 정보 추출
        JwtUserPrincipal principal = jwtTokenProvider.getPrincipalFromRefreshToken(request.refreshToken());

        // RefreshToken 주인과 실제 사용자 불일치 시 탈취 의심
        if (!storedToken.getUserId().equals(principal.getId())) {
            refreshTokenRepository.delete(storedToken);
            throw new AppException(ErrorCode.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다.");
        }

        // 기존 토큰 삭제 후 새 토큰 발급
        refreshTokenRepository.delete(storedToken);
        return issueTokens(principal.getId(), principal.getUsername(), principal.getRole());
    }

    /**
     * ✅ OAuth2 로그인 성공 후 토큰 발급
     * - 이메일로 기존 사용자 조회
     * - 존재할 경우 동일한 JWT 발급 로직 재사용
     * - OAuth2AuthenticationSuccessHandler 에서 호출됨
     */
    @Transactional
    public LoginResponse issueTokensForOAuthLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

        return issueTokens(user.getId(), user.getEmail(), user.getRole().name());
    }

    /**
     * ✅ AccessToken / RefreshToken 발급 공통 메서드
     * - AccessToken, RefreshToken 생성
     * - 기존 RefreshToken 모두 삭제 (1유저 1토큰 정책)
     * - 새 RefreshToken 저장 후 응답 DTO로 반환
     */
    private LoginResponse issueTokens(Long userId, String email, String role) {
        // AccessToken 생성
        String accessToken = jwtTokenProvider.generateAccessToken(
                userId,
                email,
                role
        );

        // RefreshToken 생성
        String refreshToken = jwtTokenProvider.generateRefreshToken(
                userId,
                email,
                role
        );

        // 기존 RefreshToken 삭제 (보안 정책상 유저당 1개만 유지)
        refreshTokenRepository.deleteAllByUserId(userId);

        // 새 RefreshToken 엔티티 저장
        RefreshToken refreshTokenEntity = RefreshToken.issue(
                userId,
                refreshToken,
                LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds())
        );
        refreshTokenRepository.save(refreshTokenEntity);

        // 최종 JWT 응답 반환
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
