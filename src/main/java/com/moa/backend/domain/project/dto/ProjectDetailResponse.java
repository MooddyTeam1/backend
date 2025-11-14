package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.*;
import com.moa.backend.domain.reward.dto.RewardResponse;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 한글 설명: 프로젝트 상세 정보를 담는 응답 DTO.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetailResponse {

    private Long id;
    private String maker;                 // 메이커 이름(사업자명)
    private String title;                 // 프로젝트 제목
    private String summary;               // 요약 설명
    private String storyMarkdown;         // 상세 스토리(마크다운)

    private Long goalAmount;              // 목표 금액
    private LocalDate startDate;          // 펀딩 시작일
    private LocalDate endDate;            // 펀딩 종료일

    private Category category;            // 카테고리
    private ProjectLifecycleStatus lifecycleStatus; // 진행 상태
    private ProjectReviewStatus reviewStatus;       // 심사 상태
    private ProjectResultStatus resultStatus;       // 결과 상태

    private String coverImageUrl;         // 대표 이미지
    private List<String> coverGallery;    // 갤러리 이미지 목록
    private List<String> tags;            // 태그 목록

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime requestAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectedReason;
    private LocalDateTime liveStartAt;
    private LocalDateTime liveEndAt;

    private List<RewardResponse> rewards; // 리워드 목록

    // 👇 여기부터 북마크 관련 필드 추가

    // 한글 설명: 현재 로그인한 서포터 기준으로 이 프로젝트를 찜했는지 여부.
    private boolean bookmarked;

    // 한글 설명: 이 프로젝트를 찜한 전체 서포터 수.
    private long bookmarkCount;

    // 한글 설명: Project 엔티티로부터 기본 상세 DTO를 생성한다.
    public static ProjectDetailResponse from(Project project) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .maker(project.getMaker().getBusinessName())
                .title(project.getTitle())
                .summary(project.getSummary())
                .storyMarkdown(project.getStoryMarkdown())
                .goalAmount(project.getGoalAmount())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .category(project.getCategory())
                .lifecycleStatus(project.getLifecycleStatus())
                .reviewStatus(project.getReviewStatus())
                .resultStatus(project.getResultStatus())
                .coverImageUrl(project.getCoverImageUrl())
                .coverGallery(project.getCoverGallery())
                .tags(project.getTags())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .requestAt(project.getRequestAt())
                .approvedAt(project.getApprovedAt())
                .rejectedAt(project.getRejectedAt())
                .rejectedReason(project.getRejectedReason())
                .liveStartAt(project.getLiveStartAt())
                .liveEndAt(project.getLiveEndAt())
                .rewards(project.getRewards().stream()
                        .map(RewardResponse::from)
                        .toList())
                // 한글 설명: 북마크 정보는 기본값으로 채워두고,
                // 실제 로그인 유저 정보가 있을 때 컨트롤러/서비스에서 덮어쓴다.
                .bookmarked(false)
                .bookmarkCount(0L)
                .build();
    }
}
