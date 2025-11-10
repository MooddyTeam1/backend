package com.moa.backend.domain.project.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class    StatusSummaryResponse {
    private long draftCount;        //작성 중(DRAFT, NONE)
    private long reviewCount;       //심사 중(DRAFT, REVIEW)
    private long approvedCount;     //승인 됨(DRAFT, APPROVED)
    private long scheduledCount;    //공개 예정(SCHEDULED, APPROVED)
    private long liveCount;         //진행 중 (LIVE, APPROVED)
    private long endCount;          //종료    (ENDED, APPROVED)
    private long rejectedCount;     //반려 됨  (DRAFT, REJECTED)
}
