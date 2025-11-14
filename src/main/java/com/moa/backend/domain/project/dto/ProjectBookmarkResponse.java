// 한글 설명: 프로젝트 찜/해제 요청 이후의 상태를 프론트로 내려주는 응답 DTO.
package com.moa.backend.domain.project.dto;

public record ProjectBookmarkResponse(
        Long projectId,
        boolean bookmarked,
        long bookmarkCount
) {
}
