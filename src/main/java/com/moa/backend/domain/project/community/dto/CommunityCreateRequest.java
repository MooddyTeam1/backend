package com.moa.backend.domain.project.community.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCreateRequest {
    private String content;
    private List<String> imageUrls; // S3 URL 리스트
}
