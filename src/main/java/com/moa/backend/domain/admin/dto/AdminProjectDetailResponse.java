package com.moa.backend.domain.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 한글 설명: 관리자 프로젝트 심사 상세 DTO.
 * - 공개 화면용 ProjectDetailResponse 와 비슷하지만,
 *   심사 상태/히스토리 및 메이커 프로필 필드 포함.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AdminProjectDetailResponse DTO")
public class AdminProjectDetailResponse {

    // ============== 기본 프로젝트 정보 ==============

    @Schema(description = "projectId", example = "1")

    private Long projectId;
    @Schema(description = "makerId", example = "1")
    private Long makerId;
    @Schema(description = "makerName", example = "makerName")
    private String makerName;
    @Schema(description = "title", example = "title")
    private String title;
    @Schema(description = "summary", example = "summary")
    private String summary;
    @Schema(description = "category (허용값은 서버 enum 정의 참조)", example = "category")
    private Category category;
    @Schema(description = "storyMarkdown", example = "storyMarkdown")
    private String storyMarkdown;
    @Schema(description = "coverImageUrl", example = "coverImageUrl")
    private String coverImageUrl;
    @Schema(description = "coverGallery", example = "[]")
    private List<String> coverGallery; // 한글 설명: 커버/갤러리 이미지 URL 목록
    @Schema(description = "goalAmount", example = "1")
    private Long goalAmount;
    @Schema(description = "startDate", example = "2025-11-01")
    private LocalDate startDate;
    @Schema(description = "endDate", example = "2025-11-01")
    private LocalDate endDate;

    // ============== 메이커 프로필 ==============

    /**
     * 한글 설명: 관리자용 메이커 프로필.
     * - 메이커의 모든 정보(개인/사업자 공통 + 선택 필드)를 포함.
     */
    @Schema(description = "makerProfile", example = "makerProfile")
    private AdminMakerProfileResponse makerProfile;

    // ============== 상태 / 심사 관련 필드 ==============

    @Schema(description = "projectReviewStatus (허용값은 서버 enum 정의 참조)", example = "projectReviewStatus")

    private ProjectReviewStatus projectReviewStatus;
    @Schema(description = "projectLifecycleStatus (허용값은 서버 enum 정의 참조)", example = "projectLifecycleStatus")
    private ProjectLifecycleStatus projectLifecycleStatus;
    @Schema(description = "requestReviewAt", example = "2025-11-01T10:00:00")
    private LocalDateTime requestReviewAt;
    @Schema(description = "approvedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime approvedAt;
    @Schema(description = "rejectedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime rejectedAt;
    @Schema(description = "rejectedReason", example = "rejectedReason")
    private String rejectedReason;

    // ============== 리워드 / 메타 정보 ==============

    @Schema(description = "rewards", example = "[]")

    private List<RewardResponse> rewards;
    @Schema(description = "createdAt", example = "2025-11-01T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "updatedAt", example = "2025-11-01T10:00:00")
    private LocalDateTime updatedAt;

    /**
     * 한글 설명: Project 엔티티에서 AdminProjectDetailResponse 로 변환하는 정적 메서드.
     * - coverGallery는 이미 List<String>으로 변환되어 있음.
     */
    public static AdminProjectDetailResponse from(Project project) {
        return AdminProjectDetailResponse.builder()
                .projectId(project.getId())
                .makerId(project.getMaker().getId())
                .makerName(project.getMaker().getName())
                .title(project.getTitle())
                .summary(project.getSummary())
                .category(project.getCategory())
                .storyMarkdown(project.getStoryMarkdown())
                .coverImageUrl(project.getCoverImageUrl())
                .coverGallery(project.getCoverGallery() != null ? project.getCoverGallery() : List.of())
                .goalAmount(project.getGoalAmount())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                // 메이커 풀 프로필 포함
                .makerProfile(AdminMakerProfileResponse.from(project.getMaker()))
                .projectReviewStatus(project.getReviewStatus())
                .projectLifecycleStatus(project.getLifecycleStatus())
                .requestReviewAt(project.getRequestAt())
                .approvedAt(project.getApprovedAt())
                .rejectedAt(project.getRejectedAt())
                .rejectedReason(project.getRejectedReason())
                .rewards(
                        project.getRewards()
                                .stream()
                                .map(RewardResponse::from)
                                .collect(Collectors.toList())
                )
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}

