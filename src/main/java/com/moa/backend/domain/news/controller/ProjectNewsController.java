package com.moa.backend.domain.news.controller;

import com.moa.backend.domain.news.dto.NewsCreateRequest;
import com.moa.backend.domain.news.dto.NewsResponse;
import com.moa.backend.domain.news.service.ProjectNewsService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project/{projectId}/news")
@RequiredArgsConstructor
@Tag(name = "Project-News", description = "프로젝트 소식 작성/조회/삭제")
public class ProjectNewsController {

    private final ProjectNewsService newsService;

    @PostMapping
    @Operation(summary = "프로젝트 소식 작성")
    public ResponseEntity<NewsResponse> createNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @RequestBody NewsCreateRequest request
    ) {
        return ResponseEntity.ok(
                newsService.createNews(principal.getId(), projectId, request)
        );
    }

    @GetMapping
    @Operation(summary = "프로젝트 소식 목록 조회")
    public ResponseEntity<List<NewsResponse>> getNewsList(
            @Parameter(example = "1200") @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(newsService.getNewsList(projectId));
    }

    @GetMapping("/{newsId}")
    @Operation(summary = "프로젝트 소식 단건 조회")
    public ResponseEntity<NewsResponse> getNews(
            @PathVariable Long newsId
    ) {
        return ResponseEntity.ok(newsService.getNews(newsId));
    }

    @DeleteMapping("/{newsId}")
    @Operation(summary = "프로젝트 소식 삭제")
    public ResponseEntity<Void> deleteNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long newsId
    ) {
        newsService.deleteNews(principal.getId(), newsId);
        return ResponseEntity.noContent().build();
    }
}
