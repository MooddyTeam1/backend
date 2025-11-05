package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotNull(message = "목표금액은 필수입니다")
    @Positive(message = "목표 금액은 0보다 커야합니다")
    private Long goalAmount;

    @NotNull(message = "시작일은 필수입니다")
    private LocalDateTime startAt;

    @NotNull(message = "종료일은 필수입니다")
    private LocalDateTime endAt;

    @NotNull(message = "카테고리는 필수입니다")
    private Category category;

    private String thumbnailUrl;
}