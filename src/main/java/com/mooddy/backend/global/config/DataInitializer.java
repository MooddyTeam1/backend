package com.mooddy.backend.global.config;

import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 개발/테스트용 초기 데이터 생성
 * 서버 시작 시 자동으로 테스트 유저 3명 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        log.info("🚀 초기 데이터 생성 시작");

        // 이미 유저가 있으면 생성하지 않음
        if (userRepository.count() > 0) {
            log.info("✅ 이미 유저 데이터가 존재함 - 초기 데이터 생성 스킵");
            return;
        }

        // 유저 1: happy (소유자용)
        User user1 = User.builder()
                .nickname("test")
                .email("test@example.com")
                .password(passwordEncoder.encode("secret123"))
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
        userRepository.save(user1);
        log.info("✅ 유저 1 생성: {} (ID: {})", user1.getEmail(), user1.getId());

        // 유저 2: happy2 (협업자용)
        User user2 = User.builder()
                .nickname("test2")
                .email("test2@example.com")
                .password(passwordEncoder.encode("secret123"))
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
        userRepository.save(user2);
        log.info("✅ 유저 2 생성: {} (ID: {})", user2.getEmail(), user2.getId());

        // 유저 3: happy3 (추가 테스트용)
        User user3 = User.builder()
                .nickname("test3")
                .email("test3@example.com")
                .password(passwordEncoder.encode("secret123"))
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
        userRepository.save(user3);
        log.info("✅ 유저 3 생성: {} (ID: {})", user3.getEmail(), user3.getId());

        log.info("🎉 초기 데이터 생성 완료! (총 3명)");
    }
}
