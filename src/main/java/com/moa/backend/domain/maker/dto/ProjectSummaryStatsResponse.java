package com.moa.backend.domain.maker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 메이커 프로젝트 통계 요약 응답 DTO.
 * - GET /api/maker/projects/stats/summary 응답 형식.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryStatsResponse {

    private Integer totalProjects;        // 전체 프로젝트 수
    private Integer liveProjects;         // 진행중(LIVE + APPROVED) 프로젝트 수
    private Long totalRaised;             // 총 모금액 합계 (PAID 기준)
    private Integer newProjectsThisMonth; // 이번 달 신규 프로젝트 수
}
