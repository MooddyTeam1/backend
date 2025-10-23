package com.example.mooddy.controller;


import com.example.mooddy.dto.AuthRequest;
import com.example.mooddy.dto.AuthResponse;
import com.example.mooddy.dto.SignupRequest;
import com.example.mooddy.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        AuthResponse response = authService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest
    ) {
        AuthResponse response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }
}
