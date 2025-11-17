package com.moa.backend.domain.follow.controller;

import com.moa.backend.domain.follow.service.SupporterFollowService;
import com.moa.backend.domain.follow.service.SupporterProjectBookmarkService;
import com.moa.backend.domain.project.dto.ProjectBookmarkResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final SupporterProjectBookmarkService supporterProjectBookmarkService;
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

    // 한글 설명: 프로젝트 찜하기 (서포터 → 프로젝트).
    @PostMapping("/project/{projectId}/bookmark")
    public ResponseEntity<ProjectBookmarkResponse> bookmarkProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId
    ) {
        Long userId = principal.getId();
        var status = supporterProjectBookmarkService.bookmark(userId, projectId);

        ProjectBookmarkResponse response = new ProjectBookmarkResponse(
                projectId,
                status.bookmarked(),
                status.bookmarkCount()
        );
        return ResponseEntity.ok(response);
    }

    // 한글 설명: 프로젝트 찜 해제.
    @DeleteMapping("/project/{projectId}/bookmark")
    public ResponseEntity<ProjectBookmarkResponse> unbookmarkProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId
    ) {
        Long userId = principal.getId();
        var status = supporterProjectBookmarkService.unbookmark(userId, projectId);

        ProjectBookmarkResponse response = new ProjectBookmarkResponse(
                projectId,
                status.bookmarked(),
                status.bookmarkCount()
        );
        return ResponseEntity.ok(response);
    }
}
