package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 한글 설명: 메이커 홈 "프로젝트" 탭에서 사용하는 단일 프로젝트 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MakerProjectResponse {

    // 프로젝트 기본 정보
    private Long id;                    // 프로젝트 ID
    private String title;               // 프로젝트 제목
    private String summary;             // 프로젝트 요약 설명 (nullable)
    private String coverImageUrl;       // 커버 이미지 URL (nullable)

    // 분류/상태
    private Category category;                   // 카테고리
    private ProjectLifecycleStatus lifecycleStatus; // 라이프사이클 상태
    private ProjectResultStatus resultStatus;       // 결과 상태 (ENDED일 때 의미 있음, 그 외 null 허용)

    // 일정
    private LocalDate startDate;         // 시작일 (YYYY-MM-DD)
    private LocalDate endDate;           // 종료일 (YYYY-MM-DD)
    private Integer daysLeft;            // 남은 일수 (LIVE일 때만 유효, 그 외 null)

    // 금액/진행률
    private Long goalAmount;             // 목표 금액
    private Long raisedAmount;           // 모금 금액
    private Double progressPercentage;   // 모금 진행률 (0.0 ~ 100.0 이상도 가능)

    // 지표
    private Integer supporterCount;      // 서포터 수
    private Integer bookmarkCount;       // 북마크 수

    // 메이커 정보
    private Long makerId;                // 메이커 ID
    private String makerName;            // 메이커 이름

    // 생성/수정 시각
    private LocalDateTime createdAt;     // 생성일시
    private LocalDateTime updatedAt;     // 수정일시
}
