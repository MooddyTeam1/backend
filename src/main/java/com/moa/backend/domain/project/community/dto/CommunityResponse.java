package com.moa.backend.domain.project.community.dto;

import com.moa.backend.domain.project.community.entity.ProjectCommunity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityResponse {

    private Long communityId;
    private Long projectId;
    private Long userId;
    private String userName;
    private String content;
    private List<String> images;
    private LocalDateTime createdAt;

    // 🔥 추가되는 필드
    private long likeCount;
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
