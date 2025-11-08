package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.reward.dto.RewardRequest;
import jakarta.validation.constraints.NotBlank;
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
public class CreateProjectRequest {

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String summary;

    private String StoryMarkdown;

    @NotNull(message = "목표금액은 필수입니다")
    @Positive(message = "목표 금액은 0보다 커야합니다")
    private Long goalAmount;

    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다")
    private LocalDate endDate;

    @NotNull(message = "카테고리는 필수입니다")
    private Category category;

    @NotBlank(message = "대표 이미지는 필수입니다")
    private String coverImageUrl;

    @Size(max = 6, message = "갤러리는 최대 6장까지만 등록 가능합니다")
    private List<String> coverGallery;

    @Size(max = 6, message = "태그는 최대 6개까지만 등록 가능합니다")
    private List<String> tags;

    private List<RewardRequest> rewardRequests;
}