package com.moa.backend.domain.project.dto;

import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.reward.dto.RewardRequest;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TempProjectRequest {  //임시 저장
    private String title;
    private String summary;
    private String storyMarkdown;
    private Long goalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Category category;
    private String coverImageUrl;
    private List<String> coverGallery;
    private List<String> tags;

    private List<RewardRequest> rewardRequests;
}
