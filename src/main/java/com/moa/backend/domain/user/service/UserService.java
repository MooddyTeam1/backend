package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.SocialUser;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.entity.UserRole;
import com.moa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // 1. 로깅을 위해 import 추가
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * ✅ UserService (수정됨)
 *
 * - 사용자 등록 및 조회를 담당
 * - 일반 회원가입 및 소셜 로그인 신규 사용자 생성 로직 포함
 * - 모든 신규 사용자는 자동으로 supporter/maker 프로필이 생성됨
 *
 * (수정 사항)
 * - handleSocialLogin 로직을 3단계로 분리
 * 1. 소셜 ID (Provider + ProviderId)로 기존 사용자 조회 (가장 우선)
 * 2. (없으면) 이메일로 기존 사용자 조회 (계정 연동)
 * 3. (없으면) 신규 사용자 생성 (이메일 없으면 가상 이메일 발급)
 * - 모든 흐름에서 SocialConnection이 저장되도록 수정
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j // 2. 클래스 레벨에 @Slf4j 어노테이션 추가
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileInitializer userProfileInitializer;

    /**
     * ✅ 일반 회원가입
     * - 이메일 중복 확인
     * - 비밀번호 암호화 후 User 저장
     * - supporter/maker 프로필 자동 생성
     * (이 메서드는 기존 코드와 동일합니다)
     */
    public User registerUser(String email, String rawPassword, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다: " + email);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.createUser(email, encodedPassword, name);
        User saved = userRepository.save(user);
        userProfileInitializer.initializeFor(saved);
        return saved;
    }

    /**
     * ✅ 소셜 로그인 사용자 처리 (수정된 핵심 로직)
     * - 1. (가장 우선) 소셜 ID로 기존 사용자인지 확인
     * - 2. (신규 소셜 로그인 시) 이메일로 연동할 기존 계정이 있는지 확인
     * - 3. (완전 신규) 이메일이 없으면 가상 이메일을 생성하여 신규 가입
     */
    public User handleSocialLogin(SocialUser socialUser) {
        String provider = socialUser.getProvider();
        String providerId = socialUser.getProviderId();

        // 1. (가장 우선) Provider + ProviderId로 이미 연결된 계정이 있는지 확인
        Optional<User> existingUserBySocial = userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUserBySocial.isPresent()) {
            // 1-1. 이미 소셜 로그인을 한 적이 있는 사용자 (가장 일반적인 케이스)
            log.info("[소셜 로그인] 기방문 유저 로그인: provider={}, providerId={}", provider, providerId);
            User user = existingUserBySocial.get();

            updateUserSocialInfo(user, socialUser); // 이름/사진 등 변경 시 업데이트
            user.setLastLoginAt(LocalDateTime.now());

            User saved = userRepository.save(user);
            userProfileInitializer.initializeFor(saved); // 프로필 무결성 검사
            return saved;
        }

        // 2. (신규 소셜 로그인) 이메일로 기존 계정 연동 시도
        String email = socialUser.getEmail();
        if (email != null) {
            Optional<User> existingUserByEmail = userRepository.findByEmail(email);

            if (existingUserByEmail.isPresent()) {
                // 2-1. 이메일은 같으나(예: 로컬 가입) 소셜 연동은 처음인 사용자
                log.info("[소셜 로그인] 기존 계정 연동: email={}, provider={}", email, provider);
                User user = existingUserByEmail.get();

                // 2-2. 새 소셜 정보 연결 (SocialConnection에 저장)
                user.addSocialConnection(provider, providerId, email);
                updateUserSocialInfo(user, socialUser);
                user.setLastLoginAt(LocalDateTime.now());

                User saved = userRepository.save(user);
                userProfileInitializer.initializeFor(saved);
                return saved;
            }
        }

        // 3. (완전 신규) 신규 사용자 생성
        String userEmail = email;
        if (userEmail == null) {
            // 3-1. 이메일 정보 제공에 동의하지 않은 경우, 고유한 가상 이메일 생성
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

            user.addSocialConnection(provider, providerId, socialUser.getEmail()); // socialUser.getEmail()은 null일 수 있음
            updateUserSocialInfo(user, socialUser);
            user.setLastLoginAt(LocalDateTime.now());

            User saved = userRepository.save(user);
            userProfileInitializer.initializeFor(saved);
            return saved;
        }

        // 3-3. 신규 소셜 유저 생성
        User newUser = User.createSocialUser(
                userEmail,
                socialUser.getName(),
                socialUser.getPicture()
        );
        newUser.setRole(UserRole.USER);
        newUser.setLastLoginAt(LocalDateTime.now());

        // 3-4. 소셜 연결 정보 추가 (SocialConnection에 저장)
        newUser.addSocialConnection(provider, providerId, socialUser.getEmail()); // socialUser.getEmail()은 null일 수 있음

        User saved = userRepository.save(newUser);
        userProfileInitializer.initializeFor(saved); // 신규 유저 프로필 생성
        return saved;
    }

    /**
     * ✅ 이메일 기반 사용자 조회
     * (이 메서드는 기존 코드와 동일합니다)
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * ✅ 마지막 로그인 시간 갱신
     * (이 메서드는 기존 코드와 동일합니다)
     */
    public void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * ✅ 사용자 저장 (외부 서비스 호출용)
     * (이 메서드는 기존 코드와 동일합니다)
     */
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * ✅ 사용자 소셜 정보 업데이트 헬퍼 메서드
     * (새로 추가된 편의 메서드입니다)
     */
    private void updateUserSocialInfo(User user, SocialUser socialUser) {
        // 이름이 변경되었을 수 있으니 업데이트
        if (socialUser.getName() != null) {
            user.setName(socialUser.getName());
        }
        // 프로필 사진이 없었는데 새로 생겼거나 변경되었으면 업데이트
        if (socialUser.getPicture() != null && !socialUser.getPicture().equals(user.getPicture())) {
            user.setPicture(socialUser.getPicture());
        }
    }
}