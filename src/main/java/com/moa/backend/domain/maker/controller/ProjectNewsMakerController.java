package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.ProjectNoticeCreateRequest;
import com.moa.backend.domain.maker.dto.manageproject.ProjectNoticeResponse;
import com.moa.backend.domain.maker.service.ProjectNewsService;
import com.moa.backend.global.dto.PageResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maker/projects/{projectId}/news")
@RequiredArgsConstructor
@Tag(name = "Project-News-Maker", description = "메이커 프로젝트 소식 작성/수정/삭제/조회")
public class ProjectNewsMakerController {

    // 한글 설명: 프로젝트 소식 도메인 비즈니스 로직
    private final ProjectNewsService newsService;

    // ========================
    // 0) 소식 목록 조회 (메이커 콘솔용, 페이지네이션)
    // ========================
    @GetMapping
    @Operation(summary = "프로젝트 소식 목록 조회 (메이커 콘솔용, 페이지네이션/검색/필터)")
    public ResponseEntity<PageResponse<ProjectNoticeResponse>> getNewsPageForMaker(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(name = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long makerUserId = principal.getId();

        PageResponse<ProjectNoticeResponse> response =
                newsService.getNewsPageForMaker(makerUserId, projectId, page, size, keyword, from, to);

        return ResponseEntity.ok(response);
    }

    // ========================
    // 1) 소식 작성 (NOT PUBLIC)
    // ========================
    @PostMapping
    @Operation(summary = "프로젝트 소식 작성 (메이커 전용)")
    public ResponseEntity<ProjectNoticeResponse> createNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @RequestBody ProjectNoticeCreateRequest request
    ) {
        return ResponseEntity.ok(
                newsService.createNews(principal.getId(), projectId, request)
        );
    }

    // ========================
    // 2) 소식 수정 (NOT PUBLIC)
    // ========================
    @PutMapping("/{newsId}")
    @Operation(summary = "프로젝트 소식 수정 (메이커 전용)")
    public ResponseEntity<ProjectNoticeResponse> updateNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @PathVariable Long newsId,
            @RequestBody ProjectNoticeCreateRequest request
    ) {
        return ResponseEntity.ok(
                newsService.updateNews(principal.getId(), projectId, newsId, request)
        );
    }

    // ========================
    // 3) 소식 삭제 (NOT PUBLIC)
    // ========================
    @DeleteMapping("/{newsId}")
    @Operation(summary = "프로젝트 소식 삭제 (메이커 전용)")
    public ResponseEntity<Void> deleteNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @PathVariable Long newsId
    ) {
        newsService.deleteNews(principal.getId(), projectId, newsId);
        return ResponseEntity.noContent().build();
    }
}
