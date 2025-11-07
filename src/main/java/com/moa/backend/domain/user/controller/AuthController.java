package com.moa.backend.domain.user.controller;

import com.moa.backend.domain.user.dto.*;
import com.moa.backend.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ AuthController (통합 버전)
 *
 * 1️⃣ 일반 로그인 / 회원가입 / 토큰 갱신 (JWT 기반 REST API)
 * 2️⃣ 소셜 로그인 후 대시보드 표시 (OAuth2)
 */
@Slf4j
@Controller
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /* -----------------------------------------------------
     * ✅ [1] 일반 로그인 / 회원가입 (JWT)
     * ----------------------------------------------------- */

    @PostMapping("/api/auth/signup")
    @ResponseBody // <-- JSON 응답
    public ResponseEntity<SignUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("📝 회원가입 요청: {}", request.getEmail());
        SignUpResponse response = authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("🔐 로그인 요청: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/auth/refresh")
    @ResponseBody
    public ResponseEntity<LoginResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("♻️ 토큰 재발급 요청");
        LoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    /* -----------------------------------------------------
     * ✅ [2] 소셜 로그인 (OAuth2) + View 렌더링
     * ----------------------------------------------------- */

    // 홈 화면 (로그인 상태 여부 표시)
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isLoggedIn", true);
            if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
                model.addAttribute("name", oauth2User.getAttribute("name"));
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "home"; // e.g. templates/home.html
    }

    // 로그인 페이지 (OAuth2 로그인 버튼 노출)
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // e.g. templates/login.html
    }

    // OAuth2 로그인 성공 후 대시보드 표시
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            model.addAttribute("user", oauth2User.getAttributes());
            model.addAttribute("name", oauth2User.getAttribute("name"));
            model.addAttribute("email", oauth2User.getAttribute("email"));
        }
        return "dashboard"; // e.g. templates/dashboard.html
    }
}
