package com.moa.backend.domain.community.controller;

import com.moa.backend.domain.community.dto.*;
import com.moa.backend.domain.community.service.ProjectCommunityService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project/{projectId}/community")
@RequiredArgsConstructor
public class ProjectCommunityController {

    private final ProjectCommunityService service;

    // ==================== 커뮤니티 생성 ========================
    @PostMapping
    public ResponseEntity<CommunityResponse> createCommunity(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId,
            @RequestBody CommunityCreateRequest request
    ) {
        return ResponseEntity.ok(
                service.createCommunity(principal.getId(), projectId, request)
        );
    }

    // ==================== 커뮤니티 목록 조회 ========================
    @GetMapping
    public ResponseEntity<List<CommunityResponse>> getCommunityList(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                service.getCommunityList(projectId, principal.getId())
        );
    }

    // ==================== 커뮤니티 단건 조회 ========================
    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityResponse> getCommunity(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long communityId
    ) {
        return ResponseEntity.ok(
                service.getCommunity(communityId, principal.getId())
        );
    }

    // ==================== 커뮤니티 삭제 ========================
    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> deleteCommunity(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long communityId
    ) {
        service.deleteCommunity(principal.getId(), communityId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 댓글 생성 ========================
    @PostMapping("/{communityId}/comment")
    public ResponseEntity<CommunityCommentResponse> addComment(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long communityId,
            @RequestBody CommunityCommentRequest request
    ) {
        return ResponseEntity.ok(
                service.addComment(principal.getId(), communityId, request)
        );
    }

    // ==================== 댓글 목록 조회 ========================
    @GetMapping("/{communityId}/comment")
    public ResponseEntity<List<CommunityCommentResponse>> getComments(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long communityId
    ) {
        return ResponseEntity.ok(
                service.getComments(communityId, principal.getId())
        );
    }

    // ==================== 댓글 수정 ========================
    @PatchMapping("/comment/{commentId}")
    public ResponseEntity<CommunityCommentResponse> updateComment(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long commentId,
            @RequestBody CommunityCommentUpdateRequest request
    ) {
        return ResponseEntity.ok(
                service.updateComment(principal.getId(), commentId, request)
        );
    }

    // ==================== 댓글 삭제 ========================
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long commentId
    ) {
        service.deleteComment(principal.getId(), commentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 댓글 좋아요 ========================
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long commentId
    ) {
        service.likeComment(principal.getId(), commentId);
        return ResponseEntity.ok().build();
    }

    // ==================== 댓글 좋아요 취소 ========================
    @DeleteMapping("/comment/{commentId}/like")
    public ResponseEntity<Void> unLikeComment(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long commentId
    ) {
        service.unLikeComment(principal.getId(), commentId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 댓글 좋아요 개수 ========================
    @GetMapping("/comment/{commentId}/like/count")
    public ResponseEntity<Long> getCommentLikeCount(
            @PathVariable Long commentId
    ) {
        return ResponseEntity.ok(service.getLikeCount(commentId));
    }

    // ==================== 커뮤니티 좋아요 ========================
    @PostMapping("/{communityId}/like")
    public ResponseEntity<Void> likeCommunity(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long communityId
    ) {
        service.likeCommunity(principal.getId(), communityId);
        return ResponseEntity.ok().build();
    }

    // ==================== 커뮤니티 좋아요 취소 ========================
    @DeleteMapping("/{communityId}/like")
    public ResponseEntity<Void> unLikeCommunity(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long communityId
    ) {
        service.unLikeCommunity(principal.getId(), communityId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 커뮤니티 좋아요 개수 조회 ========================
    @GetMapping("/{communityId}/like/count")
    public ResponseEntity<Long> getCommunityLikeCount(
            @PathVariable Long communityId
    ) {
        return ResponseEntity.ok(service.getCommunityLikeCount(communityId));
    }
}
