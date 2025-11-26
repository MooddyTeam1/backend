package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.MakerProjectPageResponse;
import com.moa.backend.domain.maker.dto.MakerProjectResponse;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectLifecycleStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 한글 설명: 메이커 홈(공개)에서 사용하는 프로젝트 목록 조회 서비스.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MakerProjectQueryService {

    private final MakerRepository makerRepository;
    private final ProjectRepository projectRepository;

    /**
     * 한글 설명:
     * 메이커 프로필 페이지의 "프로젝트" 탭 목록 조회.
     *
     * - 공개 API (인증 불필요)
     * - 포함되는 프로젝트:
     *   * SCHEDULED (공개 예정)
     *   * LIVE (진행 중)
     *   * ENDED + SUCCESS (성공 종료)
     *
     * @param makerId 메이커 ID
     * @param page    요청 페이지 (1부터 시작)
     * @param size    페이지 크기 (기본 12, 최대 50)
     * @param sortBy  createdAt | startDate | endDate | raisedAmount
     * @param order   asc | desc
     */
    public MakerProjectPageResponse getMakerProjects(
            Long makerId,
            int page,
            int size,
            String sortBy,
            String order
    ) {
        // 1) 메이커 존재 여부 검증
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("메이커를 찾을 수 없습니다. (id=" + makerId + ")"));

        // 2) 페이지/사이즈 검증
        if (page < 1) {
            throw new IllegalArgumentException("잘못된 페이지 번호입니다. page는 1 이상이어야 합니다.");
        }
        if (size < 1 || size > 50) {
            throw new IllegalArgumentException("페이지 크기는 1 이상 50 이하여야 합니다.");
        }

        // 3) 정렬 기준/방향 검증 및 매핑
        Sort.Direction direction = "asc".equalsIgnoreCase(order)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        String sortProperty;
// 한글 설명: sortBy가 null, 빈 문자열, "createdAt" 이면 기본값(createdAt) 사용
        if (sortBy == null || sortBy.isBlank() || "createdAt".equals(sortBy)) {
            sortProperty = "createdAt"; // Project.createdAt
        } else {
            switch (sortBy) {
                case "startDate" -> sortProperty = "startDate";   // Project.startDate
                case "endDate" -> sortProperty = "endDate";       // Project.endDate
                case "raisedAmount" -> {
                    // 한글 설명: 아직 모금액 필드가 없으므로 임시로 goalAmount 기준 정렬
                    sortProperty = "goalAmount";
                }
                default -> throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다. sort=" + sortBy);
            }
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortProperty));

        // 4) 프로젝트 조회 (비즈니스 필터는 Repository 쿼리에서 처리)
        Page<Project> projectPage = projectRepository.findMakerPublicProjects(makerId, pageable);

        // 5) Project → DTO 변환 + daysLeft, progressPercentage 계산
        Page<MakerProjectResponse> dtoPage = projectPage.map(project -> {
            // ⚠ 현재 Project 엔티티에는 모금액/서포터수/북마크수 필드가 없으므로 0으로 처리
            Long goalAmount = project.getGoalAmount();
            Long raisedAmount = 0L;          // TODO: 주문/결제 통계 테이블 연동 후 실제 값 매핑
            int supporterCount = 0;          // TODO: 후원자 수 집계 컬럼/쿼리 연동
            int bookmarkCount = 0;           // TODO: 북마크 수 집계 컬럼/쿼리 연동

            // 진행률 계산
            double progressPercentage = 0.0;
            if (goalAmount != null && goalAmount > 0 && raisedAmount != null) {
                progressPercentage = (raisedAmount * 100.0) / goalAmount;
            }

            // 남은 일수 계산 (LIVE + endDate 있을 때만)
            Integer daysLeft = null;
            if (project.getLifecycleStatus() == ProjectLifecycleStatus.LIVE && project.getEndDate() != null) {
                LocalDate today = LocalDate.now();
                long diff = ChronoUnit.DAYS.between(today, project.getEndDate());
                if (diff < 0) diff = 0;
                daysLeft = (int) diff;
            }

            return MakerProjectResponse.builder()
                    .id(project.getId())
                    .title(project.getTitle())
                    .summary(project.getSummary())
                    .coverImageUrl(project.getCoverImageUrl())
                    .category(project.getCategory())
                    .lifecycleStatus(project.getLifecycleStatus())
                    .resultStatus(project.getResultStatus())
                    .startDate(project.getStartDate())
                    .endDate(project.getEndDate())
                    .daysLeft(daysLeft)
                    .goalAmount(goalAmount)
                    .raisedAmount(raisedAmount)
                    .progressPercentage(progressPercentage)
                    .supporterCount(supporterCount)
                    .bookmarkCount(bookmarkCount)
                    .makerId(maker.getId())
                    .makerName(maker.getName())
                    .createdAt(project.getCreatedAt())
                    .updatedAt(project.getUpdatedAt())
                    .build();
        });

        // 6) Page → 명세서 형식의 응답 DTO로 변환
        return MakerProjectPageResponse.from(dtoPage);
    }
}
