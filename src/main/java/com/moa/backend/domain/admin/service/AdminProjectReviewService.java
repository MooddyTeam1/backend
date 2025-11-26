package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.AdminMakerProfileResponse;
import com.moa.backend.domain.admin.dto.AdminProjectDetailResponse;
import com.moa.backend.domain.admin.dto.AdminProjectReviewResponse;
import com.moa.backend.domain.admin.dto.ProjectStatusResponse;
import com.moa.backend.domain.admin.dto.RejectProjectRequest;
import com.moa.backend.domain.admin.dto.RejectReasonPresetResponse;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.entity.ProjectReviewStatus;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import com.moa.backend.global.security.jwt.JwtUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 한글 설명: 관리자 프로젝트 심사 콘솔 전용 서비스 레이어.
 * - 관리자 권한 검증
 * - 심사 대기 목록/상세 조회
 * - 승인 / 반려 상태 변경
 * - 반려 사유 프리셋 제공
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AdminProjectReviewService {

    private final ProjectRepository projectRepository;
    private final MakerRepository makerRepository;

    /**
     * 한글 설명: 관리자 여부를 확인하는 공통 검증 메서드.
     * - JwtUserPrincipal의 getRole()을 사용하여 "ADMIN" 역할 확인
     */
    private void ensureAdmin(JwtUserPrincipal principal) {
        if (principal == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 한글 설명: 역할이 "ADMIN"이 아니면 FORBIDDEN 예외 발생
        if (!"ADMIN".equals(principal.getRole())) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }
    }

    /**
     * 한글 설명: 심사 대기 프로젝트 목록 조회.
     * - 조건: reviewStatus == REVIEW
     * - 정렬: requestAt DESC (심사 요청 최신 순)
     */
    @Transactional(readOnly = true)
    public List<AdminProjectReviewResponse> getReviewProjects(JwtUserPrincipal principal) {
        ensureAdmin(principal);

        List<Project> projects =
                projectRepository.findByProjectReviewStatusOrderByRequestReviewAtDesc(
                        ProjectReviewStatus.REVIEW
                );

        return projects.stream()
                .map(AdminProjectReviewResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 한글 설명: 특정 프로젝트의 심사 상세 조회.
     * - 없으면 PROJECT_NOT_FOUND 예외
     * - 리워드와 메이커 정보를 함께 로드하기 위해 fetch join 사용
     */
    @Transactional(readOnly = true)
    public AdminProjectDetailResponse getProjectDetail(
            JwtUserPrincipal principal,
            Long projectId
    ) {
        ensureAdmin(principal);

        // 한글 설명: fetch join을 사용하여 리워드와 메이커를 함께 로드
        Project project = projectRepository.findByIdWithRewardsAndMaker(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        return AdminProjectDetailResponse.from(project);
    }

    /**
     * 한글 설명: 프로젝트 승인 처리.
     * - 전제: reviewStatus == REVIEW
     * - Project 엔티티의 approve() 도메인 메서드 호출
     */
    public ProjectStatusResponse approveProject(
            JwtUserPrincipal principal,
            Long projectId
    ) {
        ensureAdmin(principal);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (project.getReviewStatus() != ProjectReviewStatus.REVIEW) {
            // 한글 설명: 이미 승인/반려된 프로젝트는 승인 불가
            throw new AppException(ErrorCode.PROJECT_NOT_IN_REVIEW);
        }

        project.approve(); // Project 엔티티 도메인 메서드
        projectRepository.save(project);

        return ProjectStatusResponse.from(project);
    }

    /**
     * 한글 설명: 프로젝트 반려 처리.
     * - 전제: reviewStatus == REVIEW
     * - Project 엔티티의 reject(reason) 도메인 메서드 호출
     */
    public ProjectStatusResponse rejectProject(
            JwtUserPrincipal principal,
            Long projectId,
            RejectProjectRequest request
    ) {
        ensureAdmin(principal);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        if (project.getReviewStatus() != ProjectReviewStatus.REVIEW) {
            // 한글 설명: 이미 승인/반려된 프로젝트는 반려 불가
            throw new AppException(ErrorCode.PROJECT_NOT_IN_REVIEW);
        }

        project.reject(request.getReason()); // Project 엔티티 도메인 메서드
        projectRepository.save(project);

        return ProjectStatusResponse.from(project);
    }

    /**
     * 한글 설명: 반려 사유 프리셋 목록 조회.
     * - 프론트에서 드롭다운 등으로 사용
     */
    @Transactional(readOnly = true)
    public RejectReasonPresetResponse getRejectReasonPresets(JwtUserPrincipal principal) {
        ensureAdmin(principal);
        return RejectReasonPresetResponse.defaultPresets();
    }

    /**
     * 한글 설명: 특정 메이커의 프로필 조회.
     * - 관리자 권한 필요
     * - 메이커 ID로 조회하며 owner 정보도 함께 로드 (fetch join)
     */
    @Transactional(readOnly = true)
    public AdminMakerProfileResponse getMakerProfile(
            JwtUserPrincipal principal,
            Long makerId
    ) {
        ensureAdmin(principal);

        // 한글 설명: fetch join을 사용하여 owner를 함께 로드
        Maker maker = makerRepository.findByIdWithOwner(makerId)
                .orElseThrow(() -> new AppException(ErrorCode.MAKER_NOT_FOUND));

        return AdminMakerProfileResponse.from(maker);
    }
}

