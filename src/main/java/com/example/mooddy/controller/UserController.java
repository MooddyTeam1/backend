package com.example.mooddy.controller;

import com.example.mooddy.dto.ApiResponse;

import com.example.mooddy.domain.ListeningHistory;
import com.example.mooddy.service.ListeningService;
import com.example.mooddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ListeningService listeningService;

    // ... (getUserDetails 메서드)

    // 3. 비밀번호 변경 (PATCH /api/users/{email}/password)
    @PatchMapping("/{email}/password")
    public ResponseEntity<ApiResponse> changePassword( // 🟢 ApiResponse 사용
                                                       @PathVariable String email,
                                                       @RequestParam String newPassword
    ) {
        userService.changePassword(email, newPassword);
        return ResponseEntity.ok(new ApiResponse(true, "Password updated successfully."));
    }

    // 5. 프로필 이미지 삭제 (DELETE /api/users/{email}/image)
    @DeleteMapping("/{email}/image")
    public ResponseEntity<ApiResponse> deleteProfileImage(@PathVariable String email) { // 🟢 ApiResponse 사용
        userService.deleteProfileImage(email);
        return ResponseEntity.ok(new ApiResponse(true, "Profile image deleted successfully."));
    }

    // 6. 사용자/계정 탈퇴 (DELETE /api/users/{email})
    @DeleteMapping("/{email}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable String email) { // 🟢 ApiResponse 사용
        userService.deleteUser(email);
        return ResponseEntity.ok(new ApiResponse(true, "User account deleted successfully."));
    }

    // --- B. 리스닝 기록 관리 (ListeningController 통합) ---

    // 7. 청취 기록 저장 (POST /api/users/{email}/listening)
    // 기존 ListeningController.saveListening 통합 (경로 변경)
    @PostMapping("/{email}/listening")
    public ResponseEntity<ListeningHistory> saveListening(
            @PathVariable String email, // 경로 변수로 email 사용
            @RequestParam String trackName,
            @RequestParam String artist,
            @RequestParam String genre
    ) {
        ListeningHistory history = listeningService.saveListening(email, trackName, artist, genre);
        return ResponseEntity.ok(history);
    }

    // 8. 최근 청취 목록 조회 (GET /api/users/{email}/listening/recent)
    // 기존 ListeningController.getRecentListening 통합 (경로 변경)
    @GetMapping("/{email}/listening/recent")
    public ResponseEntity<List<ListeningHistory>> getRecentListening(@PathVariable String email) {
        List<ListeningHistory> history = listeningService.getRecentListening(email);
        return ResponseEntity.ok(history);
    }

    // 9. 장르별 통계 조회 (GET /api/users/{email}/listening/stats)
    // 기존 ListeningController.getGenreStats 통합 (경로 변경)
    @GetMapping("/{email}/listening/stats")
    public ResponseEntity<Map<String, Long>> getGenreStats(@PathVariable String email) {
        Map<String, Long> stats = listeningService.getGenreStats(email);
        return ResponseEntity.ok(stats);
    }
}