package com.moa.backend.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// 한글 설명: 홈 화면/공개용 트렌딩 프로젝트 카드에 사용하는 응답 DTO.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrendingProjectResponse {

    // 기본 카드 정보 -----------------------------
    private Long id;                            // 프로젝트 ID
    private String title;                       // 제목
    private String summary;                     // 요약 설명
    private String coverImageUrl;               // 커버 이미지
    private Category category;                  // 카테고리
    private ProjectLifecycleStatus lifecycleStatus; // 라이프사이클 상태

    private long bookmarkCount;                 // 북마크 수 (COUNT)

    // 배지/상태 계산용 플래그 -------------------
    private boolean live;                       // 진행중 여부
    private boolean scheduled;                  // 공개 예정 여부
    private long daysLeft;                      // 종료까지 남은 일수

    private LocalDate startDate;                // 펀딩 시작일 (필요 시 프론트에서 쓸 수도 있음)
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
