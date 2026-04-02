package com.moa.backend.domain.maker.dto;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "메이커 공개 프로젝트 단건 응답")
public class MakerProjectResponse {

    // 프로젝트 기본 정보
    @Schema(description = "프로젝트 ID", example = "1200")
    private Long id;                    // 프로젝트 ID
    @Schema(description = "프로젝트 제목", example = "친환경 텀블러 프로젝트")
    private String title;               // 프로젝트 제목
    @Schema(description = "프로젝트 요약 설명", example = "24시간 보온/보냉이 가능한 친환경 텀블러")
    private String summary;             // 프로젝트 요약 설명 (nullable)
    @Schema(description = "커버 이미지 URL", example = "https://cdn.moa.com/projects/1200/cover.jpg")
    private String coverImageUrl;       // 커버 이미지 URL (nullable)

    // 분류/상태
    @Schema(description = "카테고리 (예: TECH, FASHION, FOOD, LIVING 등)", example = "TECH")
    private Category category;                   // 카테고리
    @Schema(description = "라이프사이클 상태 (DRAFT, SCHEDULED, LIVE, ENDED)", example = "LIVE")
    private ProjectLifecycleStatus lifecycleStatus; // 라이프사이클 상태
    @Schema(description = "결과 상태 (SUCCESS, FAILED) - ENDED에서만 의미", example = "SUCCESS")
    private ProjectResultStatus resultStatus;       // 결과 상태 (ENDED일 때 의미 있음, 그 외 null 허용)

    // 일정
    @Schema(description = "펀딩 시작일", example = "2025-11-01")
    private LocalDate startDate;         // 시작일 (YYYY-MM-DD)
    @Schema(description = "펀딩 종료일", example = "2025-11-30")
    private LocalDate endDate;           // 종료일 (YYYY-MM-DD)
    @Schema(description = "남은 일수(LIVE일 때 유효)", example = "12")
    private Integer daysLeft;            // 남은 일수 (LIVE일 때만 유효, 그 외 null)

    // 금액/진행률
    @Schema(description = "목표 금액(원)", example = "5000000")
    private Long goalAmount;             // 목표 금액
    @Schema(description = "현재 모금액(원)", example = "3720000")
    private Long raisedAmount;           // 모금 금액
    @Schema(description = "모금 진행률(%)", example = "74.4")
    private Double progressPercentage;   // 모금 진행률 (0.0 ~ 100.0 이상도 가능)

    // 지표
    @Schema(description = "서포터 수", example = "186")
    private Integer supporterCount;      // 서포터 수
    @Schema(description = "북마크 수", example = "429")
    private Integer bookmarkCount;       // 북마크 수

    // 메이커 정보
    @Schema(description = "메이커 ID", example = "310")
    private Long makerId;                // 메이커 ID
    @Schema(description = "메이커명", example = "그린랩 스튜디오")
    private String makerName;            // 메이커 이름

    // 생성/수정 시각
    @Schema(description = "프로젝트 생성 시각", example = "2025-10-15T13:20:45")
    private LocalDateTime createdAt;     // 생성일시
    @Schema(description = "프로젝트 수정 시각", example = "2025-10-20T09:11:02")
    private LocalDateTime updatedAt;     // 수정일시
}
