// 한글 설명: 서포터가 프로젝트 상세 페이지에서 Q&A를 남기고,
//           본인이 남긴 Q&A만 조회하는 컨트롤러
package com.moa.backend.domain.qna.controller;

import com.moa.backend.domain.maker.dto.manageproject.ProjectQnaResponse;
import com.moa.backend.domain.qna.dto.ProjectQnaCreateRequest;
import com.moa.backend.domain.qna.service.ProjectQnaService;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/supporter/projects/{projectId}/qna")
@RequiredArgsConstructor
@Tag(name = "Project-QnA-Supporter", description = "서포터 프로젝트 Q&A 작성/조회")
public class ProjectQnaSupporterController {

    private final ProjectQnaService projectQnaService;

    @PostMapping
    @Operation(summary = "프로젝트 Q&A 질문 작성 (서포터 전용)")
    public ResponseEntity<ProjectQnaResponse> createQuestion(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @RequestBody ProjectQnaCreateRequest request
    ) {
        return ResponseEntity.ok(
                projectQnaService.createQuestion(principal.getId(), projectId, request)
        );
    }

    @GetMapping
    @Operation(summary = "내가 남긴 프로젝트 Q&A 목록 조회 (서포터 전용)")
    public ResponseEntity<List<ProjectQnaResponse>> getMyQnaList(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(
                projectQnaService.getMyQnaList(principal.getId(), projectId)
        );
    }

    @GetMapping("/{qnaId}")
    @Operation(summary = "내가 남긴 프로젝트 Q&A 단건 조회 (서포터 전용)")
    public ResponseEntity<ProjectQnaResponse> getMyQna(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @PathVariable Long qnaId
    ) {
        return ResponseEntity.ok(
                projectQnaService.getMyQna(principal.getId(), projectId, qnaId)
        );
    }
}
