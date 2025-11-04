package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.LoginRequest;
import com.moa.backend.domain.user.dto.LoginResponse;
import com.moa.backend.domain.user.dto.RefreshTokenRequest;
import com.moa.backend.domain.user.dto.SignUpRequest;
import com.moa.backend.domain.user.dto.SignUpResponse;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.entity.RefreshToken;
import com.moa.backend.domain.user.repository.RefreshTokenRepository;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

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

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(
            user.getId(),
            user.getEmail(),
            user.getRole().name()
        );

        String refreshToken = jwtTokenProvider.generateRefreshToken(
            user.getId(),
            user.getEmail(),
            user.getRole().name()
        );

        refreshTokenRepository.deleteAllByUserId(user.getId());
        RefreshToken refreshTokenEntity = RefreshToken.issue(
            user.getId(),
            refreshToken,
            LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds())
        );
        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginResponse(
            user.getId(),
            user.getEmail(),
            user.getRole().name(),
            "Bearer",
            accessToken,
            jwtTokenProvider.getAccessTokenValidityInSeconds(),
            refreshToken,
            jwtTokenProvider.getRefreshTokenValidityInSeconds()
        );
    }

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

        String newAccessToken = jwtTokenProvider.generateAccessToken(
            principal.getId(),
            principal.getUsername(),
            principal.getRole()
        );

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(
            principal.getId(),
            principal.getUsername(),
            principal.getRole()
        );

        refreshTokenRepository.delete(storedToken);
        RefreshToken newRefreshTokenEntity = RefreshToken.issue(
            principal.getId(),
            newRefreshToken,
            now.plusSeconds(jwtTokenProvider.getRefreshTokenValidityInSeconds())
        );
        refreshTokenRepository.save(newRefreshTokenEntity);

        return new LoginResponse(
            principal.getId(),
            principal.getUsername(),
            principal.getRole(),
            "Bearer",
            newAccessToken,
            jwtTokenProvider.getAccessTokenValidityInSeconds(),
            newRefreshToken,
            jwtTokenProvider.getRefreshTokenValidityInSeconds()
        );
    }
}
