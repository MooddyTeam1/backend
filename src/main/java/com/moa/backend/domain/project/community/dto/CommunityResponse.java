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
                .build();
    }
}
