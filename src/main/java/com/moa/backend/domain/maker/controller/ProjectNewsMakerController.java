package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.ProjectNoticeCreateRequest;
import com.moa.backend.domain.maker.dto.manageproject.ProjectNoticeResponse;
import com.moa.backend.domain.maker.service.ProjectNewsService;
import com.moa.backend.global.dto.PageResponse;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "프로젝트 소식 목록 조회", description = "메이커 콘솔에서 프로젝트 소식 목록을 페이지/키워드/기간 조건으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소식 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터가 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "프로젝트 소유자가 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PageResponse<ProjectNoticeResponse>> getNewsPageForMaker(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "프로젝트 ID", example = "1200") @PathVariable Long projectId,
            @Parameter(description = "페이지 번호(0부터 시작)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기(기본 10)", example = "10") @RequestParam(name = "size", defaultValue = "10") int size,
            @Parameter(description = "검색 키워드(제목/본문)", example = "리워드 배송") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "조회 시작일(yyyy-MM-dd)", example = "2025-11-01") @RequestParam(name = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "조회 종료일(yyyy-MM-dd)", example = "2025-11-30") @RequestParam(name = "to", required = false)
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
    @Operation(summary = "프로젝트 소식 작성", description = "메이커가 프로젝트 소식을 작성합니다. 본인 프로젝트에 대해서만 작성할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소식 작성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 본문이 유효하지 않음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "프로젝트 소유자가 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectNoticeResponse> createNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "프로젝트 ID", example = "1200") @PathVariable Long projectId,
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
    @Operation(summary = "프로젝트 소식 수정", description = "메이커가 기존 프로젝트 소식을 수정합니다. 이미 삭제된 소식은 수정할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소식 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "프로젝트 소유자가 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "소식을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectNoticeResponse> updateNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "프로젝트 ID", example = "1200") @PathVariable Long projectId,
            @Parameter(description = "소식 ID", example = "55") @PathVariable Long newsId,
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
    @Operation(summary = "프로젝트 소식 삭제", description = "메이커가 프로젝트 소식을 삭제합니다. 삭제 후 공개 목록에서 조회되지 않습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "소식 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "프로젝트 소유자가 아님", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "소식을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "프로젝트 ID", example = "1200") @PathVariable Long projectId,
            @Parameter(description = "소식 ID", example = "55") @PathVariable Long newsId
    ) {
        newsService.deleteNews(principal.getId(), projectId, newsId);
        return ResponseEntity.noContent().build();
    }
}
