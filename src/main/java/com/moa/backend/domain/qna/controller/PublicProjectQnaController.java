package com.moa.backend.domain.qna.controller;

import com.moa.backend.domain.maker.dto.manageproject.ProjectQnaResponse;
import com.moa.backend.domain.qna.service.ProjectQnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/projects/{projectId}/qna")
@Tag(name = "Project-QnA-Public", description = "공개 Q&A 조회")
public class PublicProjectQnaController {

    private final ProjectQnaService projectQnaService;

    @GetMapping
    @Operation(summary = "공개 Q&A 목록 조회")
    public ResponseEntity<List<ProjectQnaResponse>> getPublicQnaList(
            @PathVariable Long projectId
    ) {
        return ResponseEntity.ok(projectQnaService.getPublicQnaList(projectId));
    }
}

