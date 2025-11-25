// 한글 설명: 프로젝트 찜/해제 요청 이후의 상태를 프론트로 내려주는 응답 DTO.
package com.moa.backend.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "프로젝트 찜 상태 응답")
public record ProjectBookmarkResponse(
        @Schema(description = "프로젝트 ID", example = "101")
        Long projectId,
        @Schema(description = "현재 유저가 찜했는지 여부", example = "true")
        boolean bookmarked,
        @Schema(description = "총 찜 수", example = "123")
        long bookmarkCount
) {
}
