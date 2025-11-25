package com.moa.backend.domain.maker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moa.backend.domain.project.entity.Category;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.entity.ProjectResultStatus;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 한글 설명: 메이커 마이페이지 > 내 프로젝트 목록 카드 한 개에 해당하는 응답 DTO.
 * - 프론트에서 /api/maker/projects 리스트를 렌더링할 때 사용한다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MakerProjectListItemResponse {

    // ================== 기본 정보 ==================
    private Long id;                    // 프로젝트 ID
    private String title;               // 프로젝트 제목
    private Category category;          // 카테고리(enum 그대로, 프론트에서 한글 매핑 가능)

    /**
     * 한글 설명:
     *  - 메이커 마이페이지 전용 상태 코드.
     *  - 명세서 기준:
     *    - DRAFT
     *    - REVIEW
     *    - LIVE
     *    - ENDED_SUCCESS
     *    - ENDED_FAILED
     *    - REJECTED
     *    - ALL 은 필터에서만 사용하고, 아이템에는 들어가지 않는다.
     */
    private String status;

    // 한글 설명: 원본 상태도 같이 내려주면, 추후 세부 UI 로직에서 활용 가능.
    private ProjectLifecycleStatus lifecycleStatus;
    private ProjectReviewStatus reviewStatus;
    private ProjectResultStatus resultStatus;

    // ================== 썸네일/금액/진행률 ==================
    private String thumbnailUrl;        // 썸네일 URL (Project.coverImageUrl)

    private Long goalAmount;            // 목표 금액 (원)
    private Long currentAmount;         // 현재 모금액 (원, PAID 기준 합계)
    private Double progressPercent;     // 진행률 (%) = currentAmount / goalAmount * 100.0

    private Long supporterCount;        // 고유 서포터 수 (PAID 주문 기준)

    // ================== 기간/정렬용 정보 ==================
    /**
     * 한글 설명:
     *  - 종료일까지 남은 일수.
     *  - null 이면 종료되었거나 종료일이 없는 경우.
     */
    private Integer daysLeft;

    /**
     * 한글 설명:
     *  - 마지막 수정일시.
     *  - 내부적으로 Project.updatedAt을 사용한다.
     */
    private LocalDateTime lastModifiedAt;
}
