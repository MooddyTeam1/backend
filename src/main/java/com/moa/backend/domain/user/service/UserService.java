package com.moa.backend.domain.user.service;


import com.moa.backend.domain.user.dto.SocialUser;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.entity.UserRole;
import com.moa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
 */
@Service
@RequiredArgsConstructor
@Transactional
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
        User user = User.createUser(email, encodedPassword, name);
        User saved = userRepository.save(user);
        userProfileInitializer.initializeFor(saved);
        return saved;
    }

    /**
     * ✅ 소셜 로그인 사용자 처리
     * - 기존 사용자면 프로필 사진 및 마지막 로그인 시간 갱신
     * - 없으면 신규 사용자 생성 후 프로필 자동 생성
     */
    public User handleSocialLogin(SocialUser socialUser) {
        Optional<User> existingUserOpt = userRepository.findByEmail(socialUser.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // 프로필 사진 업데이트 (필요시)
            if (existingUser.getPicture() == null && socialUser.getPicture() != null) {
                existingUser.setPicture(socialUser.getPicture());
            }

            existingUser.setLastLoginAt(LocalDateTime.now());
            User saved = userRepository.save(existingUser);
            userProfileInitializer.initializeFor(saved);
            return saved;
        } else {
            // 신규 소셜 유저 생성
            User newUser = User.createSocialUser(
                    socialUser.getEmail(),
                    socialUser.getName(),
                    socialUser.getPicture()
            );
            newUser.setRole(UserRole.USER);
            newUser.setLastLoginAt(LocalDateTime.now());

            User saved = userRepository.save(newUser);
            userProfileInitializer.initializeFor(saved);
            return saved;
        }
    }

    /**
     * ✅ 이메일 기반 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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
}
