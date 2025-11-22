package com.moa.backend.domain.admin.dto.statistics.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 프로젝트 활동 통계
 */
@Getter
@Builder
@AllArgsConstructor
public class ProjectActivityDto {

    private Long newProjectCount;        // 신규 프로젝트 생성
    private Long reviewRequestedCount;   // 심사 요청(REVIEW)
    private Long approvedCount;          // 승인 완료(APPROVED)
    private Long closedTodayCount;       // 종료된 프로젝트(END_DATE 기준)
}
