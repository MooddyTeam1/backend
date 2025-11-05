package com.moa.backend.domain.admin.controller;

import com.moa.backend.domain.admin.dto.UserResponse;
import com.moa.backend.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    //판매자 승인
    @PatchMapping("/{userId}/approve")
    public ResponseEntity<String> approveCreator(@PathVariable Long userId) {
        adminService.approveCreator(userId);
        return ResponseEntity.ok("판매자 승인 완료");
    }

    //판매자 승인 반려
    @PatchMapping("/{userId}/reject")
    public ResponseEntity<String> rejectCreator(@PathVariable Long userId) {
        adminService.rejectCreator(userId);
        return ResponseEntity.ok("판매자 신청이 반려되었습니다.");
    }

    //승인대기 조회
    @GetMapping("/pending")
    public ResponseEntity<List<UserResponse>> getPendingCreators() {
        return ResponseEntity.ok(adminService.getPendingCreators());
    }
}
