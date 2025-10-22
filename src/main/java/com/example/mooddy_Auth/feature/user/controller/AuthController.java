package com.example.mooddy_Auth.feature.user.controller;

import com.example.mooddy_Auth.feature.user.dto.AuthRequest;
import com.example.mooddy_Auth.feature.user.dto.AuthResponse;
import com.example.mooddy_Auth.feature.user.dto.SignupRequest;
import com.example.mooddy_Auth.feature.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(
            @Valid @RequestBody SignupRequest signupRequest
    ) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest authRequest
            ) {
        return ResponseEntity.ok(authService.login(authRequest));
    }
}
