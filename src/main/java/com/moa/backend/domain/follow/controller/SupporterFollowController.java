package com.moa.backend.domain.follow.controller;

import com.moa.backend.domain.follow.service.SupporterFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 서포터 팔로우 API
 * - 서포터 ↔ 서포터
 * - 서포터 → 메이커
 */
@RestController
@RequestMapping("/api/supporter-follows")
@RequiredArgsConstructor
public class SupporterFollowController {

    private final SupporterFollowService supporterFollowService;

    // ===== 서포터 ↔ 서포터 팔로우 =====

    @PostMapping("/supporters/{targetSupporterUserId}")
    public ResponseEntity<Void> followSupporter(@PathVariable Long targetSupporterUserId) {
        supporterFollowService.followSupporter(targetSupporterUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/supporters/{targetSupporterUserId}")
    public ResponseEntity<Void> unfollowSupporter(@PathVariable Long targetSupporterUserId) {
        supporterFollowService.unfollowSupporter(targetSupporterUserId);
        return ResponseEntity.ok().build();
    }

    // ===== 서포터 → 메이커 팔로우 =====

    @PostMapping("/makers/{makerId}")
    public ResponseEntity<Void> followMaker(@PathVariable Long makerId) {
        supporterFollowService.followMaker(makerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/makers/{makerId}")
    public ResponseEntity<Void> unfollowMaker(@PathVariable Long makerId) {
        supporterFollowService.unfollowMaker(makerId);
        return ResponseEntity.ok().build();
    }
}
