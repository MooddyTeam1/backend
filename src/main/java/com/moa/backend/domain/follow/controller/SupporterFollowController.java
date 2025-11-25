package com.moa.backend.domain.follow.controller;

import com.moa.backend.domain.follow.service.SupporterFollowService;
import com.moa.backend.domain.follow.service.SupporterProjectBookmarkService;
import com.moa.backend.domain.project.dto.ProjectBookmarkResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Follow", description = "서포터/메이커 팔로우 및 프로젝트 찜")
public class SupporterFollowController {

    private final SupporterFollowService supporterFollowService;
    private final SupporterProjectBookmarkService supporterProjectBookmarkService;
    // ===== 서포터 ↔ 서포터 팔로우 =====

    @PostMapping("/supporters/{targetSupporterUserId}")
    @Operation(summary = "서포터 팔로우")
    public ResponseEntity<Void> followSupporter(@PathVariable Long targetSupporterUserId) {
        supporterFollowService.followSupporter(targetSupporterUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/supporters/{targetSupporterUserId}")
    @Operation(summary = "서포터 언팔로우")
    public ResponseEntity<Void> unfollowSupporter(@PathVariable Long targetSupporterUserId) {
        supporterFollowService.unfollowSupporter(targetSupporterUserId);
        return ResponseEntity.ok().build();
    }

    // ===== 서포터 → 메이커 팔로우 =====

    @PostMapping("/makers/{makerId}")
    @Operation(summary = "메이커 팔로우")
    public ResponseEntity<Void> followMaker(@PathVariable Long makerId) {
        supporterFollowService.followMaker(makerId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/makers/{makerId}")
    @Operation(summary = "메이커 언팔로우")
    public ResponseEntity<Void> unfollowMaker(@PathVariable Long makerId) {
        supporterFollowService.unfollowMaker(makerId);
        return ResponseEntity.ok().build();
    }

    // 한글 설명: 프로젝트 찜하기 (서포터 → 프로젝트).
    @PostMapping("/project/{projectId}/bookmark")
    @Operation(summary = "프로젝트 찜하기(팔로우 도메인)", description = "서포터가 프로젝트를 찜합니다.")
    public ResponseEntity<ProjectBookmarkResponse> bookmarkProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId
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
    @Operation(summary = "프로젝트 찜 해제(팔로우 도메인)", description = "서포터가 프로젝트 찜을 해제합니다.")
    public ResponseEntity<ProjectBookmarkResponse> unbookmarkProject(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId
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
