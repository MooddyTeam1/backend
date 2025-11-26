package com.moa.backend.domain.project.dto.CreateProject;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.reward.dto.RewardRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "프로젝트 생성/심사 요청 DTO")
public class CreateProjectRequest {

    @Schema(description = "프로젝트 제목", example = "친환경 텀블러 프로젝트")
    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @Schema(description = "요약 설명", example = "재활용 소재로 만든 가벼운 텀블러")
    @NotBlank(message = "내용은 필수입니다")
    private String summary;

    @Schema(description = "스토리 마크다운", example = "## 소개\n프로젝트 스토리...")
    private String StoryMarkdown;

    @Schema(description = "목표 금액(원)", example = "5000000")
    @NotNull(message = "목표금액은 필수입니다")
    @Positive(message = "목표 금액은 0보다 커야합니다")
    private Long goalAmount;

    @Schema(description = "펀딩 시작일", example = "2025-01-10")
    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    @Schema(description = "펀딩 종료일", example = "2025-02-10")
    @NotNull(message = "종료일은 필수입니다")
    private LocalDate endDate;

    @Schema(description = "카테고리", example = "TECH")
    @NotNull(message = "카테고리는 필수입니다")
    private Category category;

    @Schema(description = "대표 이미지 URL", example = "https://cdn.moa.com/project/cover.png")
    @NotBlank(message = "대표 이미지는 필수입니다")
    private String coverImageUrl;

    @Schema(description = "갤러리 이미지 URL 목록 (최대 6개)")
    @Size(max = 6, message = "갤러리는 최대 6장까지만 등록 가능합니다")
    private List<String> coverGallery;

    @Schema(description = "태그 목록 (최대 6개)", example = "[\"친환경\",\"텀블러\"]")
    @Size(max = 6, message = "태그는 최대 6개까지만 등록 가능합니다")
    private List<String> tags;

    @Schema(description = "리워드 생성 요청 목록")
    @NotEmpty(message = "최소 1개 이상의 리워드가 필요합니다")
    private List<RewardRequest> rewardRequests;
}
