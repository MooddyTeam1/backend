package com.moa.backend.domain.user.controller;

import com.moa.backend.domain.user.dto.*;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.service.AuthService;
import com.moa.backend.domain.user.service.UserService;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
/**
 * ✅ AuthController (통합 버전)
 *
 * 1️⃣ 일반 로그인 / 회원가입 / 토큰 갱신 (JWT 기반 REST API)
 * 2️⃣ 소셜 로그인 후 대시보드 표시 (OAuth2)
 */
@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Auth", description = "회원가입/로그인/JWT 갱신/내 프로필 조회")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /* -----------------------------------------------------
     * ✅ [1] 일반 로그인 / 회원가입 (JWT)
     * ----------------------------------------------------- */

    @PostMapping("/auth/signup")
    @ResponseBody // <-- JSON 응답
    @Operation(summary = "회원가입", description = "이메일/패스워드 기반 회원을 생성하고 JWT를 반환합니다.")
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("📝 회원가입 요청: {}", request.getEmail());
        SignUpResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/auth/login")
    @ResponseBody
    @Operation(summary = "로그인", description = "이메일/패스워드로 로그인하고 액세스/리프레시 토큰을 발급합니다.")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔐 로그인 요청: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/refresh")
    @ResponseBody
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스/리프레시 토큰을 재발급합니다.")
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("♻️ 토큰 재발급 요청");
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/me")
    @ResponseBody
    @Operation(summary = "내 프로필 조회", description = "JWT로 인증된 사용자의 프로필 정보를 조회합니다.")
    public UserProfileResponse getMyProfile(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null) {
            // JWT 없거나 잘못된 경우
            throw new AppException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        // UserDetailsService에서 username = email 로 세팅해 둠
        String email = principal.getUsername();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED, "유저 정보를 찾을 수 없습니다."));

        // ✅ 여기서 방금 보여준 getProfile 재사용
        return userService.getProfile(user.getId());
    }

}
