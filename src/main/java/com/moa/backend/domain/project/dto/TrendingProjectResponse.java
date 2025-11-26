package com.moa.backend.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// 한글 설명: 홈 화면/공개용 트렌딩 프로젝트 카드에 사용하는 응답 DTO.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "트렌딩 프로젝트 카드 응답")
public class TrendingProjectResponse {

    // 기본 카드 정보 -----------------------------
    @Schema(description = "프로젝트 ID", example = "1201")
    private Long id;                            // 프로젝트 ID
    @Schema(description = "제목", example = "펄스핏 모듈 밴드")
    private String title;                       // 제목
    @Schema(description = "요약", example = "센서를 교체하며 데이터를 맞춤 수집하는 피트니스 밴드")
    private String summary;                     // 요약 설명
    @Schema(description = "커버 이미지 URL", example = "https://cdn.moa.dev/projects/pulsefit/cover.png")
    private String coverImageUrl;               // 커버 이미지
    @Schema(description = "카테고리", example = "TECH")
    private Category category;                  // 카테고리
    @Schema(description = "라이프사이클 상태", example = "LIVE")
    private ProjectLifecycleStatus lifecycleStatus; // 라이프사이클 상태

    @Schema(description = "북마크 수", example = "123")
    private long bookmarkCount;                 // 북마크 수 (COUNT)

    // 배지/상태 계산용 플래그 -------------------
    @Schema(description = "진행중 여부", example = "true")
    private boolean live;                       // 진행중 여부
    @Schema(description = "공개 예정 여부", example = "false")
    private boolean scheduled;                  // 공개 예정 여부
    @Schema(description = "종료까지 남은 일수", example = "20")
    private long daysLeft;                      // 종료까지 남은 일수

    @Schema(description = "펀딩 시작일", example = "2025-11-01")
    private LocalDate startDate;                // 펀딩 시작일 (필요 시 프론트에서 쓸 수도 있음)
    @Schema(description = "펀딩 종료일", example = "2025-12-15")
    private LocalDate endDate;                  // 펀딩 종료일

    // 🔥 JPQL constructor expression에서 사용하는 전용 생성자
    // SELECT new TrendingProjectResponse(p.id, p.title, ..., COUNT(sb.id)) 에 매칭
    public TrendingProjectResponse(
            Long id,
            String title,
            String summary,
            String coverImageUrl,
            Category category,
            ProjectLifecycleStatus lifecycleStatus,
            Long bookmarkCount
    ) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.coverImageUrl = coverImageUrl;
        this.category = category;
        this.lifecycleStatus = lifecycleStatus;
        this.bookmarkCount = (bookmarkCount != null) ? bookmarkCount : 0L;
    }

    // 한글 설명: 서비스 단에서 start/end 기준으로 배지용 플래그/남은 일수 세팅하는 유틸 메서드.
    public TrendingProjectResponse applyScheduleInfo(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

        this.scheduled = (this.lifecycleStatus == ProjectLifecycleStatus.SCHEDULED);
        this.live = (this.lifecycleStatus == ProjectLifecycleStatus.LIVE);

        if (endDate != null) {
            long diff = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
            this.daysLeft = Math.max(diff, 0);
        } else {
            this.daysLeft = 0;
        }

        return this;
    }
}
