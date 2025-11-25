package com.moa.backend.domain.project.news.controller;

import com.moa.backend.domain.project.news.dto.NewsCreateRequest;
import com.moa.backend.domain.project.news.dto.NewsResponse;
import com.moa.backend.domain.project.news.service.ProjectNewsService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project/{projectId}/news")
@RequiredArgsConstructor
public class ProjectNewsController {

    private final ProjectNewsService newsService;

    @PostMapping
    public ResponseEntity<NewsResponse> createNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId,
            @RequestBody NewsCreateRequest request
    ) {
        return ResponseEntity.ok(
                newsService.createNews(principal.getId(), projectId, request)
        );
    }

    @GetMapping
    public ResponseEntity<List<NewsResponse>> getNewsList(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(newsService.getNewsList(projectId));
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<NewsResponse> getNews(
            @PathVariable Long newsId
    ) {
        return ResponseEntity.ok(newsService.getNews(newsId));
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> deleteNews(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long newsId
    ) {
        newsService.deleteNews(principal.getId(), newsId);
        return ResponseEntity.noContent().build();
    }
}
