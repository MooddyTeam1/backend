package com.example.mooddy.controller;

import com.example.mooddy.dto.AuthResponseDto;
import com.example.mooddy.dto.LoginRequestDto;
import com.example.mooddy.dto.SignupRequestDto;
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

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(
            @Valid @RequestBody SignupRequestDto signupRequest
    ) {
        AuthResponseDto response = authService.signup(signupRequest);
        return ResponseEntity.ok(response);
    }

    // 2. 로그인
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto authRequest
    ) {
        AuthResponseDto response = authService.login(authRequest);
        return ResponseEntity.ok(response);
    }
}