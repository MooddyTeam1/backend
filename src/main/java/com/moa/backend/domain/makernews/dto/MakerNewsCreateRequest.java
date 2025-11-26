package com.moa.backend.domain.makernews.dto;

import com.moa.backend.domain.makernews.entity.MakerNewsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한글 설명: 메이커 소식 생성 요청 DTO.
 * - 명세서의 MakerNewsCreateRequestDTO에 대응.
 */
@Getter
@NoArgsConstructor
public class MakerNewsCreateRequest {

    @NotBlank
    @Size(max = 200)
    // 한글 설명: 소식 제목 (1~200자)
    private String title;

    @NotBlank
    // 한글 설명: 마크다운 형식의 소식 내용
    private String contentMarkdown;

    @NotNull
    // 한글 설명: 소식 유형 (EVENT | NOTICE | NEW_PRODUCT)
    private MakerNewsType newsType;
}
