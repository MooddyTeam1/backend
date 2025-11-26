package com.moa.backend.domain.admin.dto;

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
public class AdminProjectReviewResponse {

    // 한글 설명: 프로젝트 ID
    private Long projectId;

    // 한글 설명: 메이커 이름
    private String maker;

    // 한글 설명: 프로젝트 제목
    private String title;

    // 한글 설명: 심사 요청 시각 (requestAt)
    private LocalDateTime requestAt;

    // 한글 설명: 심사 상태 (REVIEW 등)
    private ProjectReviewStatus reviewStatus;

    // 한글 설명: 리워드 이름 목록
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

