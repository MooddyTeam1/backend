package com.moa.backend.domain.project.dto.CreateProject;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.reward.entity.Reward;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 생성/심사 요청 응답")
public class CreateProjectResponse {
    @Schema(description = "프로젝트 ID", example = "101")
    private Long projectId;
    @Schema(description = "메이커 명(사업자명)", example = "모아 스튜디오")
    private String maker;
    @Schema(description = "프로젝트 제목", example = "친환경 텀블러 프로젝트")
    private String title;
    @Schema(description = "요청 시각", example = "2025-01-05T12:00:00")
    private LocalDateTime requestAt;
    @Schema(description = "심사 상태", example = "REVIEW")
    private ProjectReviewStatus reviewStatus;

    @Schema(description = "리워드 이름 목록", example = "[\"텀블러 단품\",\"텀블러+파우치\"]")
    private List<String> rewardNames;

    public static CreateProjectResponse from(Project project) {
        return CreateProjectResponse.builder()
                .projectId(project.getId())
                .maker(project.getMaker().getBusinessName())
                .title(project.getTitle())
                .requestAt(project.getRequestAt())
                .reviewStatus(project.getReviewStatus())
                .rewardNames(project.getRewards().stream().map(Reward::getName).toList())
                .build();
    }
}
