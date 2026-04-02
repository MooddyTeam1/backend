package com.moa.backend.domain.project.controller;

import com.moa.backend.domain.project.dto.ProjectListResponse;
import com.moa.backend.domain.project.service.RecommendationService;
import com.moa.backend.global.error.ErrorResponse;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Recommendation", description = "개인화 추천 프로젝트")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    @Operation(summary = "개인화 추천 프로젝트 조회", description = "로그인한 사용자 선호도 및 활동 이력을 기반으로 추천 프로젝트를 반환합니다. 비로그인 사용자는 호출할 수 없습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추천 프로젝트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProjectListResponse>> recommendations(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        List<ProjectListResponse> recommendations =recommendationService.recommend(principal.getId());
        return ResponseEntity.ok(recommendations);
    }
}
