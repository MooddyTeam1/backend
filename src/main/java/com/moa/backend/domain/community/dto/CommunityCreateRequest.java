package com.moa.backend.domain.community.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCreateRequest {
    private String content;
    private List<String> imageUrls;
}
