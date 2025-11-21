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

@Service
@RequiredArgsConstructor
@Transactional
public class AuthSupportService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AuthTokenRepository authTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.email.verification-expiration-minutes:10}")
    private long verificationExpirationMinutes;

    @Value("${app.password-reset.base-url:http://localhost:8080/password/reset}")
    private String resetBaseUrl;

    @Value("${app.password-reset.token-validity-minutes:60}")
    private long resetTokenValidityMinutes;

    public void sendVerificationCode(String email) {
        String code = generateVerificationCode();
        authTokenRepository.deleteByEmailAndType(email, AuthToken.TokenType.VERIFY);

        AuthToken token = AuthToken.builder()
                .email(email)
                .token(code)
                .type(AuthToken.TokenType.VERIFY)
                .expiresAt(LocalDateTime.now().plusMinutes(verificationExpirationMinutes))
                .build();
        authTokenRepository.save(token);

        String message = "MOA 서비스 이용을 위한 인증번호는 " + code + " 입니다.\n" +
                verificationExpirationMinutes + "분 이내에 입력해주세요.";
        sendEmail(email, "[MOA] 이메일 인증번호", message);
    }

    public void verifyCode(String email, String code) {
        if (!StringUtils.hasText(code)) {
            throw new InvalidTokenException("인증번호를 입력해주세요.");
        }

        AuthToken token = authTokenRepository.findByEmailAndTokenAndType(email, code, AuthToken.TokenType.VERIFY)
                .orElseThrow(() -> new InvalidTokenException("인증번호가 일치하지 않습니다."));

        if (token.isExpired()) {
            authTokenRepository.delete(token);
            throw new InvalidTokenException("인증번호가 만료되었습니다. 다시 요청해주세요.");
        }

        authTokenRepository.delete(token);
    }

    public void sendPasswordResetLink(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("등록되지 않은 이메일입니다."));

        authTokenRepository.deleteByEmailAndType(email, AuthToken.TokenType.RESET);

        AuthToken token = AuthToken.builder()
                .email(user.getEmail())
                .token(UUID.randomUUID().toString())
                .type(AuthToken.TokenType.RESET)
                .expiresAt(LocalDateTime.now().plusMinutes(resetTokenValidityMinutes))
                .build();
        authTokenRepository.save(token);

        String resetUrl = String.format("%s?token=%s", resetBaseUrl, token.getToken());
        String content = "비밀번호 재설정을 위해 아래 링크를 클릭하거나 브라우저 주소창에 입력해주세요.\n" + resetUrl +
                "\n유효 시간: " + resetTokenValidityMinutes + "분";
        sendEmail(email, "[MOA] 비밀번호 재설정", content);
    }

    public void resetPassword(String tokenValue, String newPassword) {
        AuthToken token = authTokenRepository.findByTokenAndType(tokenValue, AuthToken.TokenType.RESET)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 토큰입니다."));

        if (token.isExpired()) {
            authTokenRepository.delete(token);
            throw new InvalidTokenException("토큰이 만료되었습니다. 다시 요청해주세요.");
        }

        User user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new InvalidTokenException("사용자를 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(newPassword));
        authTokenRepository.delete(token);
    }

    private void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    private String generateVerificationCode() {
        int number = SECURE_RANDOM.nextInt(900000) + 100000;
        return String.valueOf(number);
    }
}