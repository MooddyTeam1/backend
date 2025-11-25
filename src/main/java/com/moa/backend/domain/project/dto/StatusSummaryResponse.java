package com.moa.backend.domain.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메이커 프로젝트 상태 요약 카운트")
public class    StatusSummaryResponse {
    @Schema(description = "작성 중(DRAFT,NONE) 수", example = "2")
    private long draftCount;        //작성 중(DRAFT, NONE)
    @Schema(description = "심사 중(DRAFT,REVIEW) 수", example = "1")
    private long reviewCount;       //심사 중(DRAFT, REVIEW)
    @Schema(description = "승인됨(DRAFT,APPROVED) 수", example = "3")
    private long approvedCount;     //승인 됨(DRAFT, APPROVED)
    @Schema(description = "공개 예정(SCHEDULED,APPROVED) 수", example = "1")
    private long scheduledCount;    //공개 예정(SCHEDULED, APPROVED)
    @Schema(description = "진행 중(LIVE,APPROVED) 수", example = "4")
    private long liveCount;         //진행 중 (LIVE, APPROVED)
    @Schema(description = "종료(ENDED,APPROVED) 수", example = "5")
    private long endCount;          //종료    (ENDED, APPROVED)
    @Schema(description = "반려(DRAFT,REJECTED) 수", example = "0")
    private long rejectedCount;     //반려 됨  (DRAFT, REJECTED)
}
