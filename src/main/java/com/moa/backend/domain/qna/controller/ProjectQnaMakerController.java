// 한글 설명: 메이커가 자신의 프로젝트 Q&A에 답변을 등록/수정하는 컨트롤러
package com.moa.backend.domain.qna.controller;

import com.moa.backend.domain.maker.dto.manageproject.ProjectQnaResponse;
import com.moa.backend.domain.qna.dto.ProjectQnaAnswerRequest;
import com.moa.backend.domain.qna.service.ProjectQnaService;
import com.moa.backend.global.dto.PageResponse;
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
@RequestMapping("/api/maker/projects/{projectId}/qna")
@RequiredArgsConstructor
@Tag(name = "Project-QnA-Maker", description = "메이커 프로젝트 Q&A 답변")
public class ProjectQnaMakerController {

    private final ProjectQnaService projectQnaService;

    @PutMapping("/{qnaId}/answer")
    @Operation(summary = "프로젝트 Q&A 답변 등록/수정 (메이커 전용)")
    public ResponseEntity<ProjectQnaResponse> answerQna(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(example = "1200") @PathVariable Long projectId,
            @PathVariable Long qnaId,
            @RequestBody ProjectQnaAnswerRequest request
    ) {
        return ResponseEntity.ok(
                projectQnaService.answerQuestion(principal.getId(), projectId, qnaId, request)
        );
    }
    // ==============================
    // 1) Q&A 목록 조회 + 미답변 필터
    // ==============================
    @GetMapping
    public ResponseEntity<PageResponse<ProjectQnaResponse>> getQnaListForMaker(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PathVariable Long projectId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "unansweredOnly", defaultValue = "false") boolean unansweredOnly
    ) {
        Long makerUserId = principal.getId();
        return ResponseEntity.ok(
                projectQnaService.getQnaPageForMaker(makerUserId, projectId, unansweredOnly, page, size)
        );
    }
}
