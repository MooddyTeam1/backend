package com.moa.backend.domain.project.community.dto;

import com.moa.backend.domain.project.community.entity.ProjectCommunity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "커뮤니티 게시글 응답")
public class CommunityResponse {

    @Schema(description = "게시글 ID", example = "3001")
    private Long communityId;
    @Schema(description = "프로젝트 ID", example = "101")
    private Long projectId;
    @Schema(description = "작성자 유저 ID", example = "1")
    private Long userId;
    @Schema(description = "작성자 이름", example = "홍길동")
    private String userName;
    @Schema(description = "내용", example = "응원합니다!")
    private String content;
    @Schema(description = "이미지 URL 목록", example = "[\"https://cdn.moa.com/community1.png\"]")
    private List<String> images;
    @Schema(description = "작성 시각", example = "2025-01-05T12:00:00")
    private LocalDateTime createdAt;

    // 🔥 추가되는 필드
    @Schema(description = "좋아요 수", example = "12")
    private long likeCount;
    @Schema(description = "내가 좋아요 눌렀는지", example = "false")
    private boolean liked;

    // ------------------------------
    // 기존 from() → 좋아요 정보 없는 기본 버전
    // ------------------------------
    public static CommunityResponse from(ProjectCommunity entity) {
        return CommunityResponse.builder()
                .communityId(entity.getId())
                .projectId(entity.getProject().getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .images(entity.getImages().stream()
                        .map(i -> i.getImageUrl())
                        .toList())
                .likeCount(0)       // 기본값
                .liked(false)       // 기본값
                .build();
    }

    // ------------------------------
    // 좋아요 & 내가 누른 여부 포함 버전
    // ------------------------------
    public static CommunityResponse from(
            ProjectCommunity entity,
            long likeCount,
            boolean liked
    ) {
        return CommunityResponse.builder()
                .communityId(entity.getId())
                .projectId(entity.getProject().getId())
                .userId(entity.getUser().getId())
                .userName(entity.getUser().getName())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .images(entity.getImages().stream()
                        .map(i -> i.getImageUrl())
                        .toList())
                .likeCount(likeCount)
                .liked(liked)
                .build();
    }
}
