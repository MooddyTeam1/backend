package com.moa.backend.global.init;

import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.entity.UserRole;
import com.moa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 기동 시 admin 계정이 없으면 자동으로 1개 생성.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String adminEmail = "admin@moa.com";
        String rawPassword = "admin1234!";

        log.info("🔐 AdminUserInitializer 실행: {}", (Object) args);

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("✅ admin 계정 이미 존재: {}", adminEmail);
            return;
        }

        // LOCAL 사용자 생성 (ROLE_USER / PROVIDER = LOCAL)
        User admin = User.createUser(
                adminEmail,
                passwordEncoder.encode(rawPassword),
                "관리자"
        );

        // 역할만 ADMIN으로 올려주기
        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);

        log.info("✅ admin 계정 생성 완료: {}", adminEmail);
    }
}
