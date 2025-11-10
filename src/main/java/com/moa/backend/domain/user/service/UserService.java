package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.SocialUser;
import com.moa.backend.domain.user.dto.UserProfileResponse;
import com.moa.backend.domain.user.entity.AuthProvider;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.entity.UserRole;
import com.moa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ✅ UserService
 *
 * - 사용자 등록 및 조회를 담당
 * - 일반 회원가입 및 소셜 로그인 신규 사용자 생성 로직 포함
 * - 모든 신규 사용자는 자동으로 supporter/maker 프로필이 생성됨
 *
 * handleSocialLogin 로직:
 * 1. 소셜 ID (provider + providerId)로 기존 사용자 조회
 * 2. 없으면 이메일로 기존 계정 연동
 * 3. 그래도 없으면 신규 유저 생성
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileInitializer userProfileInitializer;

    /**
     * ✅ 일반 회원가입
     * - 이메일 중복 확인
     * - 비밀번호 암호화 후 User 저장
     * - supporter/maker 프로필 자동 생성
     */
    public User registerUser(String email, String rawPassword, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다: " + email);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        // User.createUser 내부에서 provider = AuthProvider.LOCAL 로 세팅
        User user = User.createUser(email, encodedPassword, name);
        User saved = userRepository.save(user);
        userProfileInitializer.initializeFor(saved);
        return saved;
    }

    /**
     * ✅ 소셜 로그인 사용자 처리
     * - 1. 소셜 ID로 기존 사용자인지 확인
     * - 2. 이메일로 연동할 기존 계정이 있는지 확인
     * - 3. 없으면 신규 사용자 생성
     */
    public User handleSocialLogin(SocialUser socialUser) {
        String provider = socialUser.getProvider();      // "google", "kakao", "local", ...
        String providerId = socialUser.getProviderId();

        // ✅ 문자열 provider → AuthProvider enum으로 매핑
        AuthProvider providerEnum = mapToAuthProvider(provider);

        // 1. (가장 우선) Provider + ProviderId로 이미 연결된 계정이 있는지 확인
        Optional<User> existingUserBySocial = userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUserBySocial.isPresent()) {
            log.info("[소셜 로그인] 기방문 유저 로그인: provider={}, providerId={}", provider, providerId);
            User user = existingUserBySocial.get();

            updateUserSocialInfo(user, socialUser); // 이름/사진 등 변경 시 업데이트
            user.setLastLoginAt(LocalDateTime.now());

            // 과거 데이터에서 provider가 null일 수 있으니 방어 코드
            if (user.getProvider() == null) {
                user.setProvider(providerEnum);
            }

            User saved = userRepository.save(user);
            userProfileInitializer.initializeFor(saved); // 프로필 무결성 검사
            return saved;
        }

        // 2. (신규 소셜 로그인) 이메일로 기존 계정 연동 시도
        String email = socialUser.getEmail();
        if (email != null) {
            Optional<User> existingUserByEmail = userRepository.findByEmail(email);

            if (existingUserByEmail.isPresent()) {
                log.info("[소셜 로그인] 기존 계정 연동: email={}, provider={}", email, provider);
                User user = existingUserByEmail.get();

                // 새 소셜 연결 정보 추가
                user.addSocialConnection(provider, providerId, email);
                updateUserSocialInfo(user, socialUser);
                user.setLastLoginAt(LocalDateTime.now());

                if (user.getProvider() == null) {
                    user.setProvider(providerEnum);
                }

                User saved = userRepository.save(user);
                userProfileInitializer.initializeFor(saved);
                return saved;
            }
        }

        // 3. (완전 신규) 신규 사용자 생성
        String userEmail = email;
        if (userEmail == null) {
            userEmail = provider + "_" + providerId + "@moa.social";
            log.info("[소셜 로그인] 이메일 없는 신규 유저, 가상 이메일 생성: {}", userEmail);
        } else {
            log.info("[소셜 로그인] 이메일 있는 신규 유저 가입: {}", userEmail);
        }

        // 3-2. (방어 코드) 혹시라도 가상 이메일이나 실제 이메일이 중복되면 연동 로직으로 처리
        if (userRepository.existsByEmail(userEmail)) {
            log.warn("[소셜 로그인] 신규 가입 처리 중 이메일 중복 발견 (연동으로 전환): {}", userEmail);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new IllegalStateException("이메일 중복 검사 오류"));

            user.addSocialConnection(provider, providerId, socialUser.getEmail());
            updateUserSocialInfo(user, socialUser);
            user.setLastLoginAt(LocalDateTime.now());

            if (user.getProvider() == null) {
                user.setProvider(providerEnum);
            }

            User saved = userRepository.save(user);
            userProfileInitializer.initializeFor(saved);
            return saved;
        }

        // 3-3. 신규 소셜 유저 생성
        // ⚠️ createSocialUser 시그니처: (email, name, imageUrl, provider)
        User newUser = User.createSocialUser(
                userEmail,
                socialUser.getName(),
                socialUser.getImageUrl(),   // SocialUser에 picture 필드 있다고 가정
                providerEnum
        );
        newUser.setRole(UserRole.USER);
        newUser.setLastLoginAt(LocalDateTime.now());

        // 3-4. 소셜 연결 정보 추가 (SocialConnection에 저장)
        newUser.addSocialConnection(provider, providerId, socialUser.getEmail());

        User saved = userRepository.save(newUser);
        userProfileInitializer.initializeFor(saved); // 신규 유저 프로필 생성
        return saved;
    }

    /**
     * ✅ 문자열 provider → AuthProvider enum 매핑
     */
    private AuthProvider mapToAuthProvider(String provider) {
        if (provider == null) {
            return AuthProvider.LOCAL;
        }

        return switch (provider.toUpperCase()) {
            case "GOOGLE" -> AuthProvider.GOOGLE;
            case "KAKAO" -> AuthProvider.KAKAO;
            case "LOCAL", "CREDENTIALS" -> AuthProvider.LOCAL;
            default -> AuthProvider.LOCAL;
        };
    }

    /**
     * ✅ 이메일 기반 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * ✅ 마지막 로그인 시간 갱신
     */
    public void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * ✅ 사용자 저장 (외부 서비스 호출용)
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * ✅ 사용자 소셜 정보 업데이트 헬퍼 메서드
     */
    private void updateUserSocialInfo(User user, SocialUser socialUser) {
        if (socialUser.getName() != null) {
            user.setName(socialUser.getName());
        }
        if (socialUser.getImageUrl() != null && !socialUser.getImageUrl().equals(user.getImageUrl())) {
            user.setImageUrl(socialUser.getImageUrl());
        }
    }

    /**
     * ✅ userId 기준으로 프로필 조회
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다. id=" + userId));

        return UserProfileResponse.builder()
                .id(String.valueOf(user.getId()))
                .email(user.getEmail())
                .name(user.getName())
                .imageUrl(user.getImageUrl())
                .provider(toProviderString(user.getProvider()))
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * ✅ AuthProvider enum → 프론트에 내려줄 문자열로 변환
     */
    private String toProviderString(AuthProvider provider) {
        if (provider == null) return "credentials";

        return switch (provider) {
            case LOCAL -> "credentials";
            case GOOGLE -> "google";
            case KAKAO -> "kakao";
        };
    }
}
