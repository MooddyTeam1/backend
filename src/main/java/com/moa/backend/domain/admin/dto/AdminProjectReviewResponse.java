package com.moa.backend.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 한글 설명: 관리자 심사 콘솔 - 심사 대기 프로젝트 목록 항목 DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AdminProjectReviewResponse DTO")
public class AdminProjectReviewResponse {

    // 한글 설명: 프로젝트 ID
    @Schema(description = "projectId", example = "1")
    private Long projectId;

    // 한글 설명: 메이커 이름
    @Schema(description = "maker", example = "maker")
    private String maker;

    // 한글 설명: 프로젝트 제목
    @Schema(description = "title", example = "title")
    private String title;

    // 한글 설명: 심사 요청 시각 (requestAt)
    @Schema(description = "requestAt", example = "2025-11-01T10:00:00")
    private LocalDateTime requestAt;

    // 한글 설명: 심사 상태 (REVIEW 등)
    @Schema(description = "reviewStatus (허용값은 서버 enum 정의 참조)", example = "reviewStatus")
    private ProjectReviewStatus reviewStatus;

    // 한글 설명: 리워드 이름 목록
    @Schema(description = "rewardNames", example = "[]")
    private List<String> rewardNames;

    /**
     * 한글 설명: Project 엔티티에서 목록 항목 DTO로 변환하는 정적 팩토리 메서드.
     */
    public static AdminProjectReviewResponse from(Project project) {
        return AdminProjectReviewResponse.builder()
                .projectId(project.getId())
                .maker(project.getMaker().getName())
                .title(project.getTitle())
                .requestAt(project.getRequestAt())
                .reviewStatus(project.getReviewStatus())
                .rewardNames(
                        project.getRewards()
                                .stream()
                                .map(reward -> reward.getName())
                                .collect(Collectors.toList())
                )
                .build();
    }
}

