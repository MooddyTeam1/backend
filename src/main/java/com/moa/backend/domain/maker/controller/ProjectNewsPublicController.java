// 한글 설명: 누구나 볼 수 있는 프로젝트 소식 공개 조회용 컨트롤러
package com.moa.backend.domain.maker.controller;

import com.moa.backend.domain.maker.dto.manageproject.ProjectNoticeResponse;
import com.moa.backend.domain.maker.service.ProjectNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public ResponseEntity<List<ProjectNoticeResponse>> getNewsList(
            @Parameter(example = "1200") @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(newsService.getNewsList(projectId));
    }

    // ========================
    // 2) 소식 단건 조회 (PUBLIC)
    // ========================
    @GetMapping("/{newsId}")
    @Operation(summary = "프로젝트 소식 단건 조회 (공개)")
    public ResponseEntity<ProjectNoticeResponse> getNews(
            @Parameter(example = "1200") @PathVariable Long projectId,
            @PathVariable Long newsId
    ) {
        return ResponseEntity.ok(newsService.getNews(projectId, newsId));
    }
}
