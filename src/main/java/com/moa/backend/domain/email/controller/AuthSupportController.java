package com.moa.backend.domain.email.controller;

import com.moa.backend.domain.email.dto.EmailRequest;
import com.moa.backend.domain.email.dto.PasswordResetRequest;
import com.moa.backend.domain.email.dto.PasswordResetByCodeRequest;
import com.moa.backend.domain.email.service.AuthSupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 한글 설명:
 *  - 이메일 인증번호 발송/검증
 *  - 비밀번호 재설정 링크/코드 발송 및 비밀번호 변경
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthSupportController {

    private final AuthSupportService authSupportService;

    /**
     * 한글 설명: 이메일 인증번호 전송
     */
    @PostMapping("/email/send")
    public ResponseEntity<Map<String, String>> sendVerificationEmail(@RequestBody @Valid EmailRequest request) {
        authSupportService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "인증번호를 전송했습니다."));
    }

    /**
     * 한글 설명: 이메일 인증번호 검증
     */
    @PostMapping("/email/verify")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody @Valid EmailRequest request) {
        authSupportService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }

    /**
     * 한글 설명: 비밀번호 재설정 링크 메일 전송 (기존 방식)
     */
    @PostMapping("/password/send")
    public ResponseEntity<Map<String, String>> sendPasswordReset(@RequestBody @Valid EmailRequest request) {
        authSupportService.sendPasswordResetLink(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 메일을 전송했습니다."));
    }

    /**
     * 한글 설명: 비밀번호 재설정 (링크 기반 - 토큰 + 새 비밀번호)
     */
    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        authSupportService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(Map.of("message", "비밀번호를 변경했습니다."));
    }

    /**
     * 한글 설명: 비밀번호 재설정 인증코드 발송 (코드 기반 방식)
     */
    @PostMapping("/password/code/send")
    public ResponseEntity<Map<String, String>> sendPasswordResetCode(@RequestBody @Valid EmailRequest request) {
        authSupportService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 인증번호를 전송했습니다."));
    }

    /**
     * 한글 설명: 이메일 + 인증코드 + 새 비밀번호로 비밀번호 재설정
     */
    @PostMapping("/password/code/reset")
    public ResponseEntity<Map<String, String>> resetPasswordByCode(@RequestBody @Valid PasswordResetByCodeRequest request) {
        authSupportService.resetPasswordByCode(
                request.getEmail(),
                request.getCode(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(Map.of("message", "비밀번호를 변경했습니다."));
    }
}
