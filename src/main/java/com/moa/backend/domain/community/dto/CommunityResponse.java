package com.moa.backend.domain.community.dto;

import com.moa.backend.domain.community.entity.ProjectCommunity;
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

    private long likeCount;
    private boolean liked;

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
                .likeCount(0)
                .liked(false)
                .build();
    }

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