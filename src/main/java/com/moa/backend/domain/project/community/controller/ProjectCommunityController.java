package com.moa.backend.domain.project.community.controller;

import com.moa.backend.domain.project.community.dto.*;
import com.moa.backend.domain.project.community.service.ProjectCommunityService;
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

    @GetMapping
    public ResponseEntity<List<CommunityResponse>> list(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(service.getCommunityList(projectId));
    }

    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityResponse> getOne(
            @PathVariable Long communityId
    ) {
        return ResponseEntity.ok(service.getCommunity(communityId));
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long communityId
    ) {
        service.deleteCommunity(principal.getId(), communityId);
        return ResponseEntity.noContent().build();
    }

    // ==================== 댓글 ========================

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

    @GetMapping("/{communityId}/comment")
    public ResponseEntity<List<CommunityCommentResponse>> commentList(
            @PathVariable Long communityId
    ) {
        return ResponseEntity.ok(service.getComments(communityId));
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long commentId
    ) {
        service.deleteComment(principal.getId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
