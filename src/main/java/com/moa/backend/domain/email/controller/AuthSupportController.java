package com.moa.backend.domain.email.controller;

import com.moa.backend.domain.email.dto.EmailRequest;
import com.moa.backend.domain.email.dto.PasswordResetRequest;
import com.moa.backend.domain.email.service.AuthSupportService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth-Support", description = "이메일 인증/비밀번호 재설정 보조 API")
public class AuthSupportController {

    private final AuthSupportService authSupportService;

    @PostMapping("/email/send")
    @Operation(summary = "이메일 인증번호 발송")
    public ResponseEntity<Map<String, String>> sendVerificationEmail(@RequestBody @Valid EmailRequest request) {
        authSupportService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "인증번호를 전송했습니다."));
    }

    @PostMapping("/email/verify")
    @Operation(summary = "이메일 인증번호 검증")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody @Valid EmailRequest request) {
        authSupportService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("message", "이메일 인증이 완료되었습니다."));
    }

    @PostMapping("/password/send")
    @Operation(summary = "비밀번호 재설정 메일 발송")
    public ResponseEntity<Map<String, String>> sendPasswordReset(@RequestBody @Valid EmailRequest request) {
        authSupportService.sendPasswordResetLink(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "비밀번호 재설정 메일을 전송했습니다."));
    }

    @PostMapping("/password/reset")
    @Operation(summary = "비밀번호 재설정 완료")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        authSupportService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "비밀번호를 변경했습니다."));
    }
}
