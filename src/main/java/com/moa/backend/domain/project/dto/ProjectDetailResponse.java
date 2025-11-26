package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.*;
import com.moa.backend.domain.reward.dto.RewardResponse;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "프로젝트 상세 응답")
public class ProjectDetailResponse {

    @Schema(description = "프로젝트 ID", example = "101")
    private Long id;

    // 한글 설명: 메이커 ID (프로필 페이지 이동 등에 사용)
    @Schema(description = "메이커 ID", example = "1003")
    private Long makerId;

    @Schema(description = "메이커 이름(사업자명)", example = "모아 스튜디오")
    private String maker;                 // 메이커 이름(사업자명)

    @Schema(description = "프로젝트 제목", example = "친환경 텀블러 프로젝트")
    private String title;                 // 프로젝트 제목
    @Schema(description = "요약 설명", example = "재활용 소재로 만든 가벼운 텀블러")
    private String summary;               // 요약 설명
    @Schema(description = "스토리 마크다운", example = "## 소개\n프로젝트 스토리...")
    private String storyMarkdown;         // 상세 스토리(마크다운)

    @Schema(description = "목표 금액(원)", example = "5000000")
    private Long goalAmount;              // 목표 금액
    @Schema(description = "펀딩 시작일", example = "2025-01-10")
    private LocalDate startDate;          // 펀딩 시작일
    @Schema(description = "펀딩 종료일", example = "2025-02-10")
    private LocalDate endDate;            // 펀딩 종료일

    @Schema(description = "카테고리", example = "TECH")
    private Category category;            // 카테고리
    @Schema(description = "진행 상태", example = "LIVE")
    private ProjectLifecycleStatus lifecycleStatus; // 진행 상태
    @Schema(description = "심사 상태", example = "APPROVED")
    private ProjectReviewStatus reviewStatus;       // 심사 상태
    @Schema(description = "결과 상태", example = "SUCCESS")
    private ProjectResultStatus resultStatus;       // 결과 상태

    @Schema(description = "대표 이미지 URL", example = "https://cdn.moa.com/project/cover.png")
    private String coverImageUrl;         // 대표 이미지
    @Schema(description = "갤러리 이미지 목록")
    private List<String> coverGallery;    // 갤러리 이미지 목록
    @Schema(description = "태그 목록", example = "[\"친환경\",\"텀블러\"]")
    private List<String> tags;            // 태그 목록

    @Schema(description = "생성 시각", example = "2025-01-01T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "수정 시각", example = "2025-01-02T10:00:00")
    private LocalDateTime updatedAt;
    @Schema(description = "심사 요청 시각", example = "2025-01-03T10:00:00")
    private LocalDateTime requestAt;
    @Schema(description = "승인 시각", example = "2025-01-04T10:00:00")
    private LocalDateTime approvedAt;
    @Schema(description = "반려 시각", example = "2025-01-04T12:00:00")
    private LocalDateTime rejectedAt;
    @Schema(description = "반려 사유", example = "필수 서류 미비")
    private String rejectedReason;
    @Schema(description = "공개 시작 시각", example = "2025-01-10T00:00:00")
    private LocalDateTime liveStartAt;
    @Schema(description = "공개 종료 시각", example = "2025-02-10T00:00:00")
    private LocalDateTime liveEndAt;

    @Schema(description = "리워드 목록")
    private List<RewardResponse> rewards; // 리워드 목록

    // 👇 여기부터 북마크 관련 필드

    // 한글 설명: 현재 로그인한 서포터 기준으로 이 프로젝트를 찜했는지 여부.
    @Schema(description = "내가 찜했는지 여부", example = "false")
    private boolean bookmarked;

    // 한글 설명: 이 프로젝트를 찜한 전체 서포터 수.
    @Schema(description = "총 찜 수", example = "123")
    private long bookmarkCount;

    // 한글 설명: Project 엔티티로부터 기본 상세 DTO를 생성한다.
    public static ProjectDetailResponse from(Project project) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                // ✅ 메이커 ID 매핑
                .makerId(project.getMaker().getId())
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
