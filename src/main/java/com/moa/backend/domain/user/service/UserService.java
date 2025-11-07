package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.dto.SocialUser;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ✅ UserService (공통 유저 관리 서비스)
 * - 일반 로그인과 소셜 로그인이 공통으로 사용하는 User 관련 DB 로직
 * - OAuth2 로그인 시 신규 생성 or 기존 계정 연동 기능 포함
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * ✅ 이메일로 유저 조회 (일반 로그인용)
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * ✅ 소셜 로그인 사용자 처리 (Google, Kakao 등)
     * - 1. provider + providerId 로 기존 연결된 사용자 찾기
     * - 2. 이메일로 기존 계정과 연동 시도
     * - 3. 없으면 신규 생성
     */
    public User processOAuth2User(SocialUser socialUser) {
        // 1️⃣ provider & providerId 기준 조회
        Optional<User> existingUserByProvider = userRepository.findByProviderAndProviderId(
                socialUser.getProvider(), socialUser.getProviderId());

        if (existingUserByProvider.isPresent()) {
            log.info("기존 소셜 계정으로 로그인: provider={}, providerId={}",
                    socialUser.getProvider(), socialUser.getProviderId());
            return existingUserByProvider.get();
        }

        // 2️⃣ 이메일 기반 연동 (github.local 제외)
        if (socialUser.getEmail() != null && !socialUser.getEmail().endsWith("@github.local")) {
            Optional<User> existingUserByEmail = userRepository.findByEmail(socialUser.getEmail());
            if (existingUserByEmail.isPresent()) {
                return linkSocialAccount(existingUserByEmail.get(), socialUser);
            }
        }

        // 3️⃣ 신규 사용자 생성
        return createNewUser(socialUser);
    }

    /**
     * ✅ 기존 유저에 소셜 계정 추가 연결
     */
    private User linkSocialAccount(User existingUser, SocialUser socialUser) {
        if (existingUser.hasProvider(socialUser.getProvider())) {
            log.info("이미 연동된 소셜 계정: userId={}, provider={}",
                    existingUser.getId(), socialUser.getProvider());
            return existingUser;
        }

        // 새로운 소셜 계정 연결
        existingUser.addSocialConnection(
                socialUser.getProvider(),
                socialUser.getProviderId(),
                socialUser.getEmail()
        );

        // 프로필 정보가 비어있으면 보충
        if (existingUser.getPicture() == null && socialUser.getPicture() != null) {
            existingUser.setPicture(socialUser.getPicture());
        }

        log.info("소셜 계정 연동 완료: userId={}, provider={}",
                existingUser.getId(), socialUser.getProvider());

        return userRepository.save(existingUser);
    }

    /**
     * ✅ 신규 유저 생성 (소셜 로그인 최초 시도)
     */
    private User createNewUser(SocialUser socialUser) {
        User newUser = new User();
        newUser.setEmail(socialUser.getEmail());
        newUser.setName(socialUser.getName());
        newUser.setPicture(socialUser.getPicture());

        newUser.addSocialConnection(
                socialUser.getProvider(),
                socialUser.getProviderId(),
                socialUser.getEmail()
        );

        User savedUser = userRepository.save(newUser);

        log.info("새 사용자 생성 완료: userId={}, email={}, provider={}",
                savedUser.getId(), savedUser.getEmail(), socialUser.getProvider());

        return savedUser;
    }
}
