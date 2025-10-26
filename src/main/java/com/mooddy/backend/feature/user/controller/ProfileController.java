package com.mooddy.backend.feature.user.controller;

import com.mooddy.backend.feature.user.domain.Profile;
import com.mooddy.backend.feature.user.dto.ProfileRequest;
import com.mooddy.backend.feature.user.dto.ProfileResponse;
import com.mooddy.backend.feature.user.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    //새로운 프로필 생성
    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@RequestBody ProfileRequest request) {
        Profile profile = profileService.createProfile(request);
        return ResponseEntity.ok(ProfileResponse.fromEntity(profile));
    }
    //사용자 프로필 조회
    @GetMapping("/{email}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable String email) {
        Profile profile = profileService.getProfileByEmail(email);
        return ResponseEntity.ok(ProfileResponse.fromEntity(profile));
    }
    //프로필 정보 수정
    @PutMapping("/{email}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable String email,
            @RequestBody ProfileRequest request
    ) {
        Profile updatedProfile = profileService.updateProfile(email, request);
        return ResponseEntity.ok(ProfileResponse.fromEntity(updatedProfile));
    }
    //프로필 이미지 업로드
    @PostMapping("/{email}/image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable String email,
            @RequestParam("file") MultipartFile file
    ) {
        String imageUrl = profileService.uploadProfileImage(email, file);
        return ResponseEntity.ok(imageUrl);
    }
    //프로필 이미지 삭제
    @DeleteMapping("/{email}/image")
    public ResponseEntity<String> deleteProfileImage(@PathVariable String email) {
        profileService.deleteProfileImage(email);
        return ResponseEntity.ok("Profile image deleted successfully.");
    }
    //회원탈퇴
    @DeleteMapping("/{email}")
    public ResponseEntity<String> withdrawAccount(@PathVariable String email) {
        profileService.withdrawAccount(email);
        return ResponseEntity.ok("Account deleted successfully.");
    }
}
