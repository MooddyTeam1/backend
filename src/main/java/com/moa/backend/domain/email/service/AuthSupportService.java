package com.moa.backend.domain.email.service;

import com.moa.backend.domain.email.entity.AuthToken;
import com.moa.backend.domain.email.repository.AuthTokenRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.email.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 한글 설명:
 *  - 이메일 인증번호 발송/검증
 *  - 비밀번호 재설정 링크 발송/실제 비밀번호 변경
 *  - 네이버 SMTP(JavaMailSender) 사용
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AuthSupportService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    // 한글 설명: 이메일 인증번호 유효 시간(분)
    @Value("${app.email.verification-expiration-minutes:10}")
    private long verificationExpirationMinutes;

    // 한글 설명: 비밀번호 재설정 링크의 프론트엔드 진입 URL
    @Value("${app.password-reset.base-url:http://localhost:8080/password/reset}")
    private String resetBaseUrl;

    // 한글 설명: 비밀번호 재설정 토큰 유효 시간(분)
    @Value("${app.password-reset.token-validity-minutes:60}")
    private long resetTokenValidityMinutes;

    // 🔥 추가: spring.mail.username 주입해서 From 주소로 사용
    @Value("${spring.mail.username}")
    private String fromAddress;

    /**
     * 한글 설명: 이메일 인증번호 발송
     */
    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();

        // 동일 이메일, 동일 타입의 기존 토큰 삭제
        authTokenRepository.deleteByEmailAndType(email, AuthToken.TokenType.VERIFY);

        AuthToken token = AuthToken.builder()
                .email(email)
                .token(code)
                .type(AuthToken.TokenType.VERIFY)
                .expiresAt(LocalDateTime.now().plusMinutes(verificationExpirationMinutes))
                .build();
        authTokenRepository.save(token);

        String message = "MOA 서비스 이용을 위한 인증번호는 " + code + " 입니다.\n"
                + verificationExpirationMinutes + "분 이내에 입력해주세요.";

        sendEmail(email, "[MOA] 이메일 인증번호", message);
    }

    /**
     * 한글 설명: 이메일 인증번호 검증
     */
    public void verifyCode(String email, String code) {
        if (!StringUtils.hasText(code)) {
            throw new InvalidTokenException("인증번호를 입력해주세요.");
        }

        AuthToken token = authTokenRepository
                .findByEmailAndTokenAndType(email, code, AuthToken.TokenType.VERIFY)
                .orElseThrow(() -> new InvalidTokenException("인증번호가 일치하지 않습니다."));

        if (token.isExpired()) {
            authTokenRepository.delete(token);
            throw new InvalidTokenException("인증번호가 만료되었습니다. 다시 요청해주세요.");
        }

        // 인증 성공 시 토큰 삭제 (1회용)
        authTokenRepository.delete(token);
    }

    /**
     * 한글 설명: 비밀번호 재설정 링크 이메일 발송
     * - 유효한 유저 이메일인지 확인
     * - 기존 RESET 토큰 삭제 후 새 토큰 발급
     * - /password/reset?token=... 링크를 메일로 전송
     */
    public void sendPasswordResetLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("등록되지 않은 이메일입니다."));

        // 동일 이메일, RESET 타입 토큰 삭제
        authTokenRepository.deleteByEmailAndType(email, AuthToken.TokenType.RESET);

        AuthToken token = AuthToken.builder()
                .email(user.getEmail())
                .token(UUID.randomUUID().toString())
                .type(AuthToken.TokenType.RESET)
                .expiresAt(LocalDateTime.now().plusMinutes(resetTokenValidityMinutes))
                .build();
        authTokenRepository.save(token);

        String resetUrl = String.format("%s?token=%s", resetBaseUrl, token.getToken());
        String content = "비밀번호 재설정을 위해 아래 링크를 클릭하거나 브라우저 주소창에 입력해주세요.\n"
                + resetUrl
                + "\n유효 시간: " + resetTokenValidityMinutes + "분";

        sendEmail(email, "[MOA] 비밀번호 재설정", content);
    }

    /**
     * 한글 설명: 비밀번호 재설정 (토큰 + 새 비밀번호)
     */
    public void resetPassword(String tokenValue, String newPassword) {
        AuthToken token = authTokenRepository
                .findByTokenAndType(tokenValue, AuthToken.TokenType.RESET)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 토큰입니다."));

        if (token.isExpired()) {
            authTokenRepository.delete(token);
            throw new InvalidTokenException("토큰이 만료되었습니다. 다시 요청해주세요.");
        }

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new InvalidTokenException("사용자를 찾을 수 없습니다."));

        // 새 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(newPassword));

        // 사용한 토큰 삭제 (1회용)
        authTokenRepository.delete(token);
    }

    /**
     * 한글 설명: 공통 이메일 전송 로직
     * - From 주소를 spring.mail.username 값으로 명시적으로 지정
     *   (네이버 SMTP의 'The sender address is unauthorized' 오류 방지)
     */
    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        // 🔥 중요: 보내는 사람 주소를 네이버 계정 이메일과 동일하게 설정
        message.setFrom(fromAddress);

        mailSender.send(message);
    }

    /**
     * 한글 설명: 6자리 랜덤 인증번호 생성 (100000 ~ 999999)
     */
    private String generateVerificationCode() {
        int number = SECURE_RANDOM.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
    /**
     * 한글 설명: 비밀번호 재설정용 인증코드 발송 (링크 X, 6자리 코드)
     */
    public void sendPasswordResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("등록되지 않은 이메일입니다."));

        // 기존 RESET 토큰 삭제
        authTokenRepository.deleteByEmailAndType(email, AuthToken.TokenType.RESET);

        String code = generateVerificationCode(); // 6자리 숫자

        AuthToken token = AuthToken.builder()
                .email(user.getEmail())
                .token(code)
                .type(AuthToken.TokenType.RESET)
                .expiresAt(LocalDateTime.now().plusMinutes(resetTokenValidityMinutes))
                .build();
        authTokenRepository.save(token);

        String content = "비밀번호 재설정을 위한 인증번호는 " + code + " 입니다.\n"
                + resetTokenValidityMinutes + "분 이내에 입력해주세요.";

        sendEmail(email, "[MOA] 비밀번호 재설정 인증번호", content);
    }

    /**
     * 한글 설명: 이메일 + 인증코드 + 새 비밀번호로 비밀번호 재설정
     */
    public void resetPasswordByCode(String email, String code, String newPassword) {
        AuthToken token = authTokenRepository
                .findByEmailAndTokenAndType(email, code, AuthToken.TokenType.RESET)
                .orElseThrow(() -> new InvalidTokenException("인증번호가 일치하지 않습니다."));

        if (token.isExpired()) {
            authTokenRepository.delete(token);
            throw new InvalidTokenException("인증번호가 만료되었습니다. 다시 요청해주세요.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(newPassword));

        authTokenRepository.delete(token);
    }
}
