package com.moa.backend.domain.maker.dto;

import java.util.List;

/**
 * 한글 설명: 메이커 공개 프로필 조회 응답 DTO.
 * - /public/makers/{makerId} 에서 사용한다.
 */
public record MakerPublicProfileResponse(
        String makerId,                     // 한글 설명: 메이커 ID ("1000")
        String ownerUserId,                 // 한글 설명: 메이커 소유자 유저 ID
        String name,                        // 한글 설명: 메이커 이름
        String imageUrl,                    // 한글 설명: 커버/브랜드 이미지 URL (nullable)
        String productIntro,                // 한글 설명: 제품/서비스 소개 (nullable)
        String coreCompetencies,            // 한글 설명: 핵심 역량 (nullable)
        List<Long> keywordIds,              // 한글 설명: 키워드 ID 목록 (현재는 null/빈 리스트 사용 가능)
        List<MakerKeywordDTO> keywords,     // 한글 설명: 키워드 상세 정보 목록
        Long totalRaised,                   // 한글 설명: 누적 모금액
        Integer totalSupporters,            // 한글 설명: 누적 서포터 수
        Double satisfactionRate,            // 한글 설명: 만족도 평점 (리뷰 없으면 null)
        Boolean isFollowing,                // 한글 설명: 현재 로그인 유저 기준 팔로우 여부 (비로그인 시 null)
        Boolean isOwner                     // 한글 설명: 현재 로그인 유저가 이 메이커의 소유자인지 여부
) {
}
