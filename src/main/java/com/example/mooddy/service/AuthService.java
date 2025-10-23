package com.example.mooddy.service;

import com.example.mooddy.dto.AuthResponseDto;
import com.example.mooddy.dto.LoginRequestDto;
import com.example.mooddy.dto.SignupRequestDto;

// 파일명: AuthService.java
public interface AuthService {

    // 회원가입 및 초기 프로필 생성
    AuthResponseDto signup(SignupRequestDto request);

    // 로그인
    AuthResponseDto login(LoginRequestDto request);
}