package com.moa.backend.global.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moa.backend.BackendApplication;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.security.jwt.JwtTokenProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Gradle {@code test} 워커 없이 {@code java} 프로세스로 k6/tokens.json 을 쓴다 (Windows 한글 경로에서
 * TokenExportTest 가 ClassNotFound 로 실패할 때 대안).
 */
public final class TokenExportCli {

    private static final int MAX_TOKENS = 1000;

    private TokenExportCli() {}

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx =
                new SpringApplicationBuilder(BackendApplication.class)
                        .profiles("dev")
                        .web(WebApplicationType.NONE)
                        .run(args);
        try {
            UserRepository userRepository = ctx.getBean(UserRepository.class);
            JwtTokenProvider jwtTokenProvider = ctx.getBean(JwtTokenProvider.class);
            ObjectMapper objectMapper = ctx.getBean(ObjectMapper.class);
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
        } finally {
            ctx.close();
        }
    }
}
