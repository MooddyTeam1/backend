package com.moa.backend.global.init;

import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 로컬 부하 테스트용: <strong>현재 기동 중인 서버 프로세스</strong>의 {@link JwtTokenProvider}로 액세스 토큰을 생성한다.
 * Gradle export 와 달리 JWT_SECRET 이 반드시 일치한다(같은 JVM·같은 설정).
 */
@RestController
@Profile("dev")
@RequiredArgsConstructor
public class K6TokenExportController {

    private static final int MAX_TOKENS = 1000;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 응답 본문은 JSON 문자열 배열 — k6/tokens.json 과 동일 형식으로 저장하면 된다.
     */
    @GetMapping(value = "/public/k6-tokens", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional(readOnly = true)
    public List<String> k6Tokens() {
        List<User> users =
                userRepository.findByEmailStartingWithOrderByIdAsc(UserSeedingService.LOAD_TEST_EMAIL_PREFIX);
        if (users.size() > MAX_TOKENS) {
            users = users.subList(0, MAX_TOKENS);
        }
        List<String> tokens = new ArrayList<>(users.size());
        for (User u : users) {
            tokens.add(jwtTokenProvider.generateAccessToken(u.getId(), u.getEmail(), u.getRole().name()));
        }
        return tokens;
    }
}
