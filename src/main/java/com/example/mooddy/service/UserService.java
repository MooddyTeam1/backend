package com.example.mooddy.service;


import com.example.mooddy.dto.SignupRequestDto;
import com.example.mooddy.dto.UserDetailResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService { // UserDetailsService의 역할도 수행하도록 확장

    UserDetailResponseDto getUserDetailsByEmail(String email);
    UserDetailResponseDto updateProfile(String email, SignupRequestDto updateRequest);
    void changePassword(String email, String newPassword);
    String uploadProfileImage(String email, MultipartFile file);
    void deleteProfileImage(String email);
    void deleteUser(String email);
}