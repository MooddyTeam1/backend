package com.moa.backend.domain.project.dto.TempProject;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.reward.dto.RewardRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "프로젝트 임시 저장 요청 DTO")
public class TempProjectRequest {  //임시 저장
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

    @Schema(description = "리워드 생성 요청 목록")
    private List<RewardRequest> rewardRequests;
}
