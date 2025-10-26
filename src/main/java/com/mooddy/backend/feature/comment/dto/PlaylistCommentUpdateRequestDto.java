package com.mooddy.backend.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlaylistCommentUpdateRequestDto(
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        @Size(max = 500, message = "댓글은 500자를 넘을 수 없습니다.")
        String content
) {
}

