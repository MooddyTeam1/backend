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

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 일반 회원가입
     */
    public User registerUser(String email, String rawPassword, String name) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다: " + email);
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = User.createUser(email, encodedPassword, name);
        return userRepository.save(user);
    }

    /**
     * 소셜 로그인 사용자 처리
     * - 기존 사용자면 업데이트
     * - 없으면 새로 생성
     */
    public User handleSocialLogin(SocialUser socialUser) {
        Optional<User> existingUserOpt = userRepository.findByEmail(socialUser.getEmail());


        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            // 프로필 사진이 없거나 변경된 경우 업데이트
            if (existingUser.getPicture() == null && socialUser.getPicture() != null) {
                existingUser.setPicture(socialUser.getPicture());
            }

            existingUser.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(existingUser);
        } else {
            // 신규 소셜 유저 생성
            User newUser = User.createSocialUser(
                    socialUser.getEmail(),
                    socialUser.getName(),
                    socialUser.getPicture()
            );
            newUser.setRole(UserRole.USER);
            newUser.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(newUser);
        }
    }

    /**
     * 사용자 이메일로 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 마지막 로그인 갱신
     */
    public void updateLastLogin(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 저장 (다른 서비스에서 직접 호출 가능)
     */
    public User save(User user) {
        return userRepository.save(user);

    }
}
