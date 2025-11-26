package com.moa.backend.domain.admin.dto;

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
public class AdminProjectDetailResponse {

    // ============== 기본 프로젝트 정보 ==============

    private Long projectId;
    private Long makerId;
    private String makerName;
    private String title;
    private String summary;
    private Category category;
    private String storyMarkdown;
    private String coverImageUrl;
    private List<String> coverGallery; // 한글 설명: 커버/갤러리 이미지 URL 목록
    private Long goalAmount;
    private LocalDate startDate;
    private LocalDate endDate;

    // ============== 메이커 프로필 ==============

    /**
     * 한글 설명: 관리자용 메이커 프로필.
     * - 메이커의 모든 정보(개인/사업자 공통 + 선택 필드)를 포함.
     */
    private AdminMakerProfileResponse makerProfile;

    // ============== 상태 / 심사 관련 필드 ==============

    private ProjectReviewStatus projectReviewStatus;
    private ProjectLifecycleStatus projectLifecycleStatus;
    private LocalDateTime requestReviewAt;
    private LocalDateTime approvedAt;
    private LocalDateTime rejectedAt;
    private String rejectedReason;

    // ============== 리워드 / 메타 정보 ==============

    private List<RewardResponse> rewards;
    private LocalDateTime createdAt;
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

