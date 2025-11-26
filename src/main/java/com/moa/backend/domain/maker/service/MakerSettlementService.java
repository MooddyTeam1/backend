package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.MakerSettlementRequest;
import com.moa.backend.domain.maker.dto.MakerSettlementResponse;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.entity.MakerSettlementProfile;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.maker.repository.MakerSettlementProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 한글 설명: 메이커 정산 계좌 정보를 조회/등록/수정/삭제하는 도메인 서비스.
 * - 메이커당 1개의 정산 계좌만 허용 (Upsert 패턴).
 */
@Service
@RequiredArgsConstructor
public class MakerSettlementService {

    private final MakerRepository makerRepository;
    private final MakerSettlementProfileRepository settlementProfileRepository;

    /**
     * 한글 설명: 로그인한 유저 기준으로, 자신의 메이커 정산 계좌 정보를 조회.
     *
     * @param userId 한글 설명: JWT에서 꺼낸 현재 로그인 유저 ID.
     * @return MakerSettlementResponse 또는 정산 계좌가 없으면 null.
     */
    @Transactional(readOnly = true)
    public MakerSettlementResponse getSettlementAccount(Long userId) {
        // 1) 유저가 소유한 메이커 프로필 조회
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 프로필을 찾을 수 없습니다."));

        // 2) 메이커의 정산 계좌 정보 조회 (없으면 null 리턴)
        return settlementProfileRepository.findByMaker_Id(maker.getId())
                .map(MakerSettlementResponse::of)
                .orElse(null);
    }

    /**
     * 한글 설명: 로그인한 유저 기준으로, 자신의 메이커 정산 계좌 정보를 등록 또는 수정 (Upsert).
     *
     * @param userId  한글 설명: JWT 사용자 ID.
     * @param request 한글 설명: 은행명/계좌번호/예금주명이 담긴 요청 DTO.
     * @return 저장/수정된 정산 계좌 정보 응답 DTO.
     */
    @Transactional
    public MakerSettlementResponse upsertSettlementAccount(Long userId, MakerSettlementRequest request) {
        // 1) 유저가 소유한 메이커 프로필 조회
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 프로필을 찾을 수 없습니다."));

        // 2) 해당 메이커의 기존 정산 계좌 정보 조회
        MakerSettlementProfile profile = settlementProfileRepository.findByMaker_Id(maker.getId())
                .orElse(null);

        if (profile == null) {
            // 신규 등록
            profile = MakerSettlementProfile.builder()
                    .maker(maker)
                    .bankName(request.bankName())
                    .accountNumber(request.accountNumber())
                    .accountHolder(request.accountHolder())
                    .build();

            settlementProfileRepository.save(profile);
        } else {
            // 수정
            profile.updateBankName(request.bankName());
            profile.updateAccountNumber(request.accountNumber());
            profile.updateAccountHolder(request.accountHolder());
            // BaseTimeEntity 의 updatedAt 은 JPA Auditing 으로 자동 업데이트 가정
        }

        return MakerSettlementResponse.of(profile);
    }

    /**
     * 한글 설명: 로그인한 유저 기준으로, 자신의 메이커 정산 계좌 정보를 삭제.
     * - 향후 "정산 진행 중인 프로젝트가 있으면 삭제 불가" 등 비즈니스 룰을 추가할 수 있음.
     */
    @Transactional
    public void deleteSettlementAccount(Long userId) {
        // 1) 유저가 소유한 메이커 프로필 조회
        Maker maker = makerRepository.findByOwner_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("메이커 프로필을 찾을 수 없습니다."));

        // 2) 해당 메이커의 정산 계좌 정보 조회
        MakerSettlementProfile profile = settlementProfileRepository.findByMaker_Id(maker.getId())
                .orElseThrow(() -> new IllegalArgumentException("정산 계좌 정보를 찾을 수 없습니다."));

        // TODO: 정산 진행 중인 프로젝트 존재 여부 검증 로직 추가 예정
        // 예시:
        // if (projectSettlementQueryService.hasOnGoingSettlement(maker.getId())) {
        //     throw new IllegalStateException("정산 진행 중인 프로젝트가 있어 정산 계좌를 삭제할 수 없습니다.");
        // }

        // 3) 삭제
        settlementProfileRepository.delete(profile);
    }
}
