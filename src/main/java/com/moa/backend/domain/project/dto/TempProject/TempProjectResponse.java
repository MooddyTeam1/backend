package com.moa.backend.domain.project.dto.TempProject;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.reward.dto.RewardResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 임시 저장/조회 응답")
public class TempProjectResponse {
    @Schema(description = "프로젝트 ID", example = "101")
    private Long projectId;
    @Schema(description = "메이커 명(사업자명)", example = "모아 스튜디오")
    private String maker;
    @Schema(description = "프로젝트 제목", example = "친환경 텀블러 프로젝트")
    private String title;
    @Schema(description = "요약 설명", example = "재활용 소재로 만든 가벼운 텀블러")
    private String summary;
    @Schema(description = "스토리 마크다운", example = "## 소개\n프로젝트 스토리...")
    private String storyMarkdown;
    @Schema(description = "목표 금액(원)", example = "5000000")
    private Long goalAmount;
    @Schema(description = "펀딩 시작일", example = "2025-01-10")
    private LocalDate startDate;
    @Schema(description = "펀딩 종료일", example = "2025-02-10")
    private LocalDate endDate;
    @Schema(description = "카테고리", example = "TECH")
    private Category category;
    @Schema(description = "대표 이미지 URL", example = "https://cdn.moa.com/project/cover.png")
    private String coverImageUrl;
    @Schema(description = "갤러리 이미지 URL 목록", example = "[\"https://cdn.moa.com/img1.png\"]")
    private List<String> coverGallery;
    @Schema(description = "태그 목록", example = "[\"친환경\",\"텀블러\"]")
    private List<String> tags;

    @Schema(description = "리워드 응답 목록")
    private List<RewardResponse> rewards;

    public static TempProjectResponse from(Project project) {
        return TempProjectResponse.builder()
                .projectId(project.getId())
                .maker(project.getMaker().getBusinessName())
                .title(project.getTitle())
                .summary(project.getSummary())
                .storyMarkdown(project.getStoryMarkdown())
                .goalAmount(project.getGoalAmount())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .category(project.getCategory())
                .coverImageUrl(project.getCoverImageUrl())
                .coverGallery(project.getCoverGallery())
                .tags(project.getTags())
                // 한글 설명: disclosure 정보를 포함하여 리워드 응답 생성
                .rewards(project.getRewards().stream().map(RewardResponse::fromWithDisclosure).toList())
                .build();
    }
}
