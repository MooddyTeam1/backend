package com.moa.backend.domain.project.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 게시글 생성 요청")
public class CommunityCreateRequest {
    @Schema(description = "내용", example = "프로젝트 진행 상황을 공유합니다.")
    private String content;
    @Schema(description = "이미지 URL 목록", example = "[\"https://cdn.moa.com/img1.png\"]")
    private List<String> imageUrls; // S3 URL 리스트
}
