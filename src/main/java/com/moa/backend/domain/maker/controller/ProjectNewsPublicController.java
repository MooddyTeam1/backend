// 한글 설명: 누구나 볼 수 있는 프로젝트 소식 공개 조회용 컨트롤러
package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.manageproject.ProjectNoticeResponse;
import com.moa.backend.domain.maker.service.ProjectNewsService;
import com.moa.backend.global.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/projects/{projectId}/news")
@RequiredArgsConstructor
@Tag(name = "Project-News-Public", description = "프로젝트 소식 공개 조회")
public class ProjectNewsPublicController {

    private final ProjectNewsService newsService;

    // ========================
    // 1) 소식 목록 조회 (PUBLIC)
    // ========================
    @GetMapping
    @Operation(summary = "프로젝트 소식 목록 조회 (공개)")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소식 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProjectNoticeResponse>> getNewsList(
            @Parameter(description = "조회할 프로젝트 ID", example = "1200") @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(newsService.getNewsList(projectId));
    }

    // ========================
    // 2) 소식 단건 조회 (PUBLIC)
    // ========================
    @GetMapping("/{newsId}")
    @Operation(summary = "프로젝트 소식 단건 조회 (공개)")
    @SecurityRequirements()
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소식 단건 조회 성공"),
            @ApiResponse(responseCode = "404", description = "프로젝트 또는 소식을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectNoticeResponse> getNews(
            @Parameter(description = "조회할 프로젝트 ID", example = "1200") @PathVariable Long projectId,
            @Parameter(description = "조회할 소식 ID", example = "55") @PathVariable Long newsId
    ) {
        return ResponseEntity.ok(newsService.getNews(projectId, newsId));
    }
}
