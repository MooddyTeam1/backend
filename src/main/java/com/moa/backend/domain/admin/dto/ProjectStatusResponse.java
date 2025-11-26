package com.moa.backend.domain.admin.dto;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "프로젝트 상태 응답 (ADMIN)")
public class ProjectStatusResponse {
    @Schema(description = "프로젝트 ID", example = "1200")
    private Long id;
    @Schema(description = "라이프사이클 상태", example = "LIVE")
    private ProjectLifecycleStatus lifecycleStatus;
    @Schema(description = "심사 상태", example = "APPROVED")
    private ProjectReviewStatus reviewStatus;
    @Schema(description = "승인 시각", example = "2025-11-07T15:00:00")
    private LocalDateTime approvedAt;
    @Schema(description = "반려 시각", example = "2025-11-08T12:00:00")
    private LocalDateTime rejectedAt;
    @Schema(description = "반려 사유", example = "필수 서류 미비")
    private String rejectedReason;

    public static ProjectStatusResponse from(Project project) {
        return ProjectStatusResponse.builder()
                .id(project.getId())
                .lifecycleStatus(project.getLifecycleStatus())
                .reviewStatus(project.getReviewStatus())
                .approvedAt(project.getApprovedAt())
                .rejectedAt(project.getRejectedAt())
                .rejectedReason(project.getRejectedReason())
                .build();
    }
}
