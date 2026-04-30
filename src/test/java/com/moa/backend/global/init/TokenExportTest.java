package com.moa.backend.global.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * DB에 시드된 로드테스트 유저({@link UserSeedingService#LOAD_TEST_EMAIL_PREFIX})에 대해 JWT를 발급해 {@code k6/tokens.json}에 저장한다.
 *
 * <p>{@code dev} 프로필을 강제해 {@code BackendApplication}(dev)과 동일한 {@code jwt.*} 설정으로 토큰을 만든다. Gradle/IDE 실행 시
 * {@code JWT_SECRET} 을 서버와 동일하게 맞출 것.
 */
@SpringBootTest
@ActiveProfiles("dev")
class TokenExportTest {

    private static final int MAX_TOKENS = 1000;

    @Autowired private UserRepository userRepository;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void exportAccessTokensToK6TokensJson() throws Exception {
        List<User> users =
                userRepository.findByEmailStartingWithOrderByIdAsc(UserSeedingService.LOAD_TEST_EMAIL_PREFIX);
        if (users.size() > MAX_TOKENS) {
            users = users.subList(0, MAX_TOKENS);
        }
        List<String> tokens = new ArrayList<>(users.size());
        for (User u : users) {
            tokens.add(
                    jwtTokenProvider.generateAccessToken(
                            u.getId(), u.getEmail(), u.getRole().name()));
        }
        Path out = Path.of("k6", "tokens.json");
        Files.createDirectories(out.getParent());
        Files.writeString(out, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tokens));
    }
}
