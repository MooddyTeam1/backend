package com.example.mooddy.controller;

import com.example.mooddy.entity.UserProfile;
import com.example.mooddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
//새 사용자 회원가입
    @PostMapping("/register")
    public ResponseEntity<UserProfile> registerUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String username,
            @RequestParam String birthday
    ) {
        UserProfile userProfile = userService.registerUser(email, password, username, birthday);
        return ResponseEntity.ok(userProfile);
    }
//이메일 기준으로 사용자 정보조회
    @GetMapping("/{email}")
    public ResponseEntity<UserProfile> getUser(@PathVariable String email) {
        UserProfile userProfile = userService.getUserByEmail(email);
        return ResponseEntity.ok(userProfile);
    }
// 특정 사용자의 비밀번호 변경
    @PatchMapping("/{email}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable String email,
            @RequestParam String newPassword
    ) {
        userService.changePassword(email, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }
//사용자 계정 삭제
    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("User account deleted successfully.");
    }
}
