package com.moa.backend.global.init;

import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * K6 부하 테스트용 유저 대량 시드. 이메일 규칙은 {@link #LOAD_TEST_EMAIL_PREFIX} 로 조회·토큰 추출에 사용한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSeedingService {

    public static final String LOAD_TEST_EMAIL_PREFIX = "loadtest.k6.";
    public static final String MAKER_SEED_EMAIL = "k6.maker.seed@moa.local";

    private static final int BATCH_SIZE = 100;
    private static final String SHARED_PASSWORD = "LoadTestK6!1";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker(Locale.KOREA);

    /**
     * 펀딩 메이커 전용 계정. 프로젝트 소유자로 사용하며 일반 부하 유저와 이메일 규칙이 다르다.
     */
    @Transactional
    public User ensureMakerSeedUser() {
        return userRepository
                .findByEmail(MAKER_SEED_EMAIL)
                .orElseGet(
                        () -> {
                            User u = User.createUser(
                                    MAKER_SEED_EMAIL,
                                    passwordEncoder.encode(SHARED_PASSWORD),
                                    "K6 부하테스트 메이커");
                            User saved = userRepository.save(u);
                            log.info("메이커 시드 유저 생성: id={}, email={}", saved.getId(), saved.getEmail());
                            return saved;
                        });
    }

    /**
     * {@code loadtest.k6.000001@moa.local} 형식으로 유저를 최대 {@code count}명까지 채운다. 이미 있으면 건너뛴다.
     *
     * @return 이번 트랜잭션에서 새로 저장한 건수
     */
    @Transactional
    public int seedLoadTestUsers(int count) {
        long existing = userRepository.countByEmailStartingWith(LOAD_TEST_EMAIL_PREFIX);
        if (existing >= count) {
            log.info("로드테스트 유저 이미 {}명 존재 (요청 {}명), 스킵", existing, count);
            return 0;
        }

        String encoded = passwordEncoder.encode(SHARED_PASSWORD);
        List<User> batch = new ArrayList<>();
        int created = 0;

        for (int i = 1; i <= count; i++) {
            String email = String.format("%s%06d@moa.local", LOAD_TEST_EMAIL_PREFIX, i);
            if (userRepository.existsByEmail(email)) {
                continue;
            }
            String name = truncate(faker.name().name() + " " + String.format("%04d", i), 50);
            batch.add(User.createUser(email, encoded, name));
            if (batch.size() >= BATCH_SIZE) {
                userRepository.saveAll(batch);
                created += batch.size();
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            userRepository.saveAll(batch);
            created += batch.size();
        }
        log.info("로드테스트 유저 시드 완료: 신규 {}명", created);
        return created;
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
