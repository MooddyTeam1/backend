package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.ProjectListResponse;
import com.moa.backend.domain.project.service.RecommendationService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<ProjectListResponse>> recommendations(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        List<ProjectListResponse> recommendations =recommendationService.recommend(principal.getId());
        return ResponseEntity.ok(recommendations);
    }
}
