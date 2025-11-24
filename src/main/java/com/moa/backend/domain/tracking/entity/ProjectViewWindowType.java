///////////////////////////////////////////////////////////////////////////////
// 3. 프로젝트 뷰 집계용 Window 타입 Enum
//    파일: com/moa/backend/domain/tracking/entity/ProjectViewWindowType.java
///////////////////////////////////////////////////////////////////////////////

package com.moa.backend.domain.tracking.entity;

/**
 * 한글 설명: 프로젝트 뷰 집계 윈도우 타입
 *
 * HOUR : 1시간 단위 집계
 * DAY  : 1일 단위 집계
 * WEEK : 1주 단위 집계
 *
 * 이건 스케줄러나 배치로 ProjectViewStat 테이블에 집계할 때 사용.
 * (초기에는 안 써도 되고, 나중에 필요해지면 사용해도 됨)
 */
public enum ProjectViewWindowType {
    HOUR,
    DAY,
    WEEK
}