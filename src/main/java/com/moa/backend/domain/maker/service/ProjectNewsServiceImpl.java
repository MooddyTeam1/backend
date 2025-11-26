package com.moa.backend.domain.maker.service;

import com.moa.backend.domain.maker.dto.ProjectNoticeCreateRequest;
import com.moa.backend.domain.maker.dto.manageproject.ProjectNoticeResponse;
import com.moa.backend.domain.maker.entity.ProjectNews;
import com.moa.backend.domain.maker.repository.ProjectNewsRepository;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.dto.PageResponse;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 한글 설명: 프로젝트 소식(새소식) 서비스 구현체.
 * - 메이커 전용 작성/수정/삭제
 * - 공개용 목록/단건 조회
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectNewsServiceImpl implements ProjectNewsService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectNewsRepository projectNewsRepository;

    // ====================================================
    // 1) 소식 생성 (메이커 전용)
    // ====================================================
    @Override
    @Transactional
    public ProjectNoticeResponse createNews(Long creatorId, Long projectId, ProjectNoticeCreateRequest request) {

        // 한글 설명: 작성자(유저) 조회
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 한글 설명: 프로젝트 조회
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        // 한글 설명: maker 권한 체크 (프로젝트 소유자만 작성 가능)
        if (!project.getMaker().getOwner().getId().equals(creatorId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트 소식을 작성할 권한이 없습니다.");
        }

        boolean isPublic = (request.getIsPublic() == null) || request.getIsPublic();
        boolean notifySupporters = Boolean.TRUE.equals(request.getNotifySupporters());

        // 한글 설명: ProjectNews 엔티티 생성
        ProjectNews news = ProjectNews.builder()
                .project(project)
                .creator(creator)
                .title(request.getTitle())
                .content(request.getContent())
                .isPublic(isPublic)
                .notifySupporters(notifySupporters)
                .build();

        ProjectNews saved = projectNewsRepository.save(news);
        return ProjectNoticeResponse.from(saved);
    }

    // ====================================================
    // 2) 소식 목록 조회 (공개)
    // ====================================================
    @Override
    public List<ProjectNoticeResponse> getNewsList(Long projectId) {
        return projectNewsRepository
                .findByProject_IdOrderByPinnedDescCreatedAtDesc(projectId)
                .stream()
                .map(ProjectNoticeResponse::from)
                .toList();
    }

    // ====================================================
    // 3) 소식 단건 조회 (공개)
    // ====================================================
    @Override
    public ProjectNoticeResponse getNews(Long projectId, Long newsId) {
        ProjectNews news = projectNewsRepository.findById(newsId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "소식을 찾을 수 없습니다."));

        // 한글 설명: URL 상의 projectId와 실제 소식의 projectId 일치 여부 체크
        if (!news.getProject().getId().equals(projectId)) {
            // 굳이 권한 이슈로 보이게 하기보단, 없는 리소스처럼 처리
            throw new AppException(ErrorCode.NOT_FOUND, "해당 프로젝트에 속한 소식이 아닙니다.");
        }

        return ProjectNoticeResponse.from(news);
    }

    // ====================================================
    // 4) 소식 수정 (메이커 전용)
    // ====================================================
    @Override
    @Transactional
    public ProjectNoticeResponse updateNews(Long creatorId, Long projectId, Long newsId, ProjectNoticeCreateRequest request) {

        ProjectNews news = projectNewsRepository.findById(newsId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "소식을 찾을 수 없습니다."));

        // 한글 설명: 프로젝트 일치 여부 검증
        if (!news.getProject().getId().equals(projectId)) {
            throw new AppException(ErrorCode.NOT_FOUND, "해당 프로젝트에 속한 소식이 아닙니다.");
        }

        // 한글 설명: 프로젝트 소유자(메이커 owner) 검증
        Long ownerId = news.getProject().getMaker().getOwner().getId();
        if (!ownerId.equals(creatorId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트 소식을 수정할 권한이 없습니다.");
        }

        // 한글 설명: 제목/내용 수정
        news.setTitle(request.getTitle());
        news.setContent(request.getContent());

        // 한글 설명: 공개/알림 여부 수정 (null이면 기존 값 유지)
        if (request.getIsPublic() != null) {
            news.setIsPublic(request.getIsPublic());
        }
        if (request.getNotifySupporters() != null) {
            news.setNotifySupporters(request.getNotifySupporters());
        }

        return ProjectNoticeResponse.from(news);
    }

    // ====================================================
    // 5) 소식 삭제 (메이커 전용)
    // ====================================================
    @Override
    @Transactional
    public void deleteNews(Long creatorId, Long projectId, Long newsId) {

        ProjectNews news = projectNewsRepository.findById(newsId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "소식을 찾을 수 없습니다."));

        // 한글 설명: 프로젝트 일치 여부 검증
        if (!news.getProject().getId().equals(projectId)) {
            throw new AppException(ErrorCode.NOT_FOUND, "해당 프로젝트에 속한 소식이 아닙니다.");
        }

        // 한글 설명: 프로젝트 소유자(메이커 owner) 검증
        Long ownerId = news.getProject().getMaker().getOwner().getId();
        if (!ownerId.equals(creatorId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트 소식을 삭제할 권한이 없습니다.");
        }

        projectNewsRepository.delete(news);
    }


    // ====================================================
    // 6) 메이커 콘솔: 페이지네이션 목록
    // ====================================================
    @Override
    public PageResponse<ProjectNoticeResponse> getNewsPageForMaker(
            Long makerUserId,
            Long projectId,
            int page,
            int size,
            String keyword,
            LocalDate from,
            LocalDate to
    ) {
        // 1) 프로젝트 + 소유권 검증
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        Long ownerId = project.getMaker().getOwner().getId();
        if (!ownerId.equals(makerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트 소식을 조회할 권한이 없습니다.");
        }

        // 2) 날짜 필터 LocalDate -> LocalDateTime 변환
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (from != null) {
            fromDateTime = from.atStartOfDay();
        }
        if (to != null) {
            // 한글 설명: to 날짜의 23:59:59까지 포함되도록 처리
            toDateTime = to.atTime(LocalTime.MAX);
        }

        // 3) PageRequest 생성
        PageRequest pageable = PageRequest.of(page, size);

        // 4) 레포지토리 호출 + DTO 변환
        Page<ProjectNews> newsPage = projectNewsRepository.searchProjectNewsForMaker(
                projectId,
                (keyword != null && !keyword.isBlank()) ? keyword : null,
                fromDateTime,
                toDateTime,
                pageable
        );

        Page<ProjectNoticeResponse> dtoPage = newsPage.map(ProjectNoticeResponse::from);

        return PageResponse.from(dtoPage);
    }
}
