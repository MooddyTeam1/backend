// 한글 설명: 프로젝트 Q&A 서비스 구현체
package com.moa.backend.domain.qna.service;

import com.moa.backend.domain.follow.repository.SupporterBookmarkProjectRepository;
import com.moa.backend.domain.maker.dto.manageproject.ProjectQnaResponse;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.notification.entity.NotificationTargetType;
import com.moa.backend.domain.notification.entity.NotificationType;
import com.moa.backend.domain.notification.service.NotificationService;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.qna.dto.ProjectQnaAnswerRequest;
import com.moa.backend.domain.qna.dto.ProjectQnaCreateRequest;
import com.moa.backend.domain.qna.entity.ProjectQna;
import com.moa.backend.domain.qna.entity.ProjectQnaStatus;
import com.moa.backend.domain.qna.repository.ProjectQnaRepository;
import com.moa.backend.domain.user.entity.SupporterProfile;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.SupporterProfileRepository;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.dto.PageResponse;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQnaServiceImpl implements ProjectQnaService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectQnaRepository projectQnaRepository;
    private final SupporterProfileRepository supporterProfileRepository; // 한글 설명: 서포터 닉네임 조회용
    private final SupporterBookmarkProjectRepository supporterBookmarkProjectRepository;
    private final NotificationService notificationService;

    // ==========================
    // 1) 서포터: 질문 생성
    // ==========================
    @Override
    @Transactional
    public ProjectQnaResponse createQuestion(Long supporterUserId,
                                             Long projectId,
                                             ProjectQnaCreateRequest request) {

        User supporter = userRepository.findById(supporterUserId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        // 한글 설명: 기본값은 비공개(true)
        boolean isPrivate = request.getIsPrivate() == null || Boolean.TRUE.equals(request.getIsPrivate());

        ProjectQna qna = ProjectQna.builder()
                .project(project)
                .questioner(supporter)
                .question(request.getQuestion())
                .status(ProjectQnaStatus.PENDING)
                .isPrivate(isPrivate)
                .build();

        ProjectQna saved = projectQnaRepository.save(qna);

        // QnA 등록 시 메이커에게 알림
        Maker maker = project.getMaker();

        notificationService.send(
                maker.getId(),                          // 메이커에게 발송
                "새 Q&A 질문이 등록되었습니다",
                "[" + project.getTitle() + "] 새로운 질문이 등록되었습니다: \"" + request.getQuestion() + "\"",
                NotificationType.MAKER,                 // 메이커 알림 타입
                NotificationTargetType.QNA,             // QNA 타겟
                qna.getId()                          // 저장된 QNA ID로 상세 페이지 이동
        );

        return toResponse(saved);
    }

    // ==========================
    // 2) 서포터: 내가 남긴 Q&A 목록 (비페이징)
    // ==========================
    @Override
    public List<ProjectQnaResponse> getMyQnaList(Long supporterUserId, Long projectId) {
        return projectQnaRepository
                .findByProject_IdAndQuestioner_IdOrderByCreatedAtDesc(projectId, supporterUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================
    // 3) 서포터: 내가 남긴 Q&A 단건
    // ==========================
    @Override
    public ProjectQnaResponse getMyQna(Long supporterUserId, Long projectId, Long qnaId) {
        ProjectQna qna = projectQnaRepository.findById(qnaId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Q&A를 찾을 수 없습니다."));

        // URL상의 projectId와 실제 Q&A의 projectId 매칭
        if (!qna.getProject().getId().equals(projectId)) {
            throw new AppException(ErrorCode.NOT_FOUND, "해당 프로젝트의 Q&A가 아닙니다.");
        }

        // 본인 것만 조회 가능
        if (!qna.getQuestioner().getId().equals(supporterUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "본인의 Q&A만 조회할 수 있습니다.");
        }

        return toResponse(qna);
    }

    // ==========================
    // 4) 메이커: 답변 등록/수정
    // ==========================
    @Override
    @Transactional
    public ProjectQnaResponse answerQuestion(Long makerUserId,
                                             Long projectId,
                                             Long qnaId,
                                             ProjectQnaAnswerRequest request) {

        ProjectQna qna = projectQnaRepository.findById(qnaId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, "Q&A를 찾을 수 없습니다."));

        // 프로젝트 일치 여부 검증
        if (!qna.getProject().getId().equals(projectId)) {
            throw new AppException(ErrorCode.NOT_FOUND, "해당 프로젝트의 Q&A가 아닙니다.");
        }

        // 프로젝트 소유자(메이커 owner) 검증
        Long ownerId = qna.getProject().getMaker().getOwner().getId();
        if (!ownerId.equals(makerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 Q&A에 답변할 권한이 없습니다.");
        }

        // 답변 내용 세팅
        qna.setAnswer(request.getAnswer());
        qna.setStatus(ProjectQnaStatus.ANSWERED);
        qna.setAnsweredAt(LocalDateTime.now());

        // QnA 답변 시 질문자(서포터)에게 알림
        User questioner = qna.getQuestioner();
        Project project = qna.getProject();

        notificationService.send(
                questioner.getId(),                     // 질문을 작성한 서포터
                "Q&A 답변이 등록되었습니다",
                "[" + project.getTitle() + "] 질문에 대한 답변이 등록되었습니다.",
                NotificationType.SUPPORTER,             // 서포터 알림
                NotificationTargetType.QNA,             // QNA 상세 페이지로 이동
                qna.getId()                             // QnA ID
        );

        return toResponse(qna);
    }

    // ==========================
    // 5) 메이커: Q&A 목록 조회 (비페이징 – 요약용)
    // ==========================
    @Override
    public List<ProjectQnaResponse> getQnaListForMaker(Long makerUserId,
                                                       Long projectId,
                                                       boolean unansweredOnly) {

        // 1) 프로젝트 조회 + 소유권 검증
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        Long ownerId = project.getMaker().getOwner().getId();
        if (!ownerId.equals(makerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트 Q&A를 조회할 권한이 없습니다.");
        }

        // 2) Q&A 조회 (필터링 여부에 따라 분기)
        List<ProjectQna> qnas;

        if (unansweredOnly) {
            // 미답변(PENDING)만
            qnas = projectQnaRepository.findByProject_IdAndStatusOrderByCreatedAtDesc(
                    projectId,
                    ProjectQnaStatus.PENDING
            );
        } else {
            // 전체
            qnas = projectQnaRepository.findByProject_IdOrderByCreatedAtDesc(projectId);
        }

        // 3) DTO 변환 (서포터 닉네임 포함)
        return qnas.stream()
                .map(this::toResponse)
                .toList();
    }

    // ==========================
    // 6) 메이커: Q&A 목록 조회 (페이징)
    // ==========================
    @Override
    public PageResponse<ProjectQnaResponse> getQnaPageForMaker(Long makerUserId,
                                                               Long projectId,
                                                               boolean unansweredOnly,
                                                               int page,
                                                               int size) {

        // 1) 프로젝트 조회 + 소유권 검증 (중복 로직이지만 명확하게 분리)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND, "프로젝트를 찾을 수 없습니다."));

        Long ownerId = project.getMaker().getOwner().getId();
        if (!ownerId.equals(makerUserId)) {
            throw new AppException(ErrorCode.FORBIDDEN, "해당 프로젝트 Q&A를 조회할 권한이 없습니다.");
        }

        // 2) Pageable 생성
        PageRequest pageable = PageRequest.of(page, size); // 정렬은 메서드 이름 OrderByCreatedAtDesc 에서 처리

        // 3) JPA Page 조회
        Page<ProjectQna> qnaPage;
        if (unansweredOnly) {
            qnaPage = projectQnaRepository.findByProject_IdAndStatusOrderByCreatedAtDesc(
                    projectId,
                    ProjectQnaStatus.PENDING,
                    pageable
            );
        } else {
            qnaPage = projectQnaRepository.findByProject_IdOrderByCreatedAtDesc(
                    projectId,
                    pageable
            );
        }

        // 4) 엔티티 Page -> DTO Page 매핑
        Page<ProjectQnaResponse> dtoPage = qnaPage.map(this::toResponse);

        // 5) 공통 PageResponse로 변환
        return PageResponse.of(dtoPage);
    }

    // ==========================
    // 내부 공통 변환 메서드
    // ==========================

    /**
     * 한글 설명:
     *  - SupporterProfile.displayName 이 있으면 그걸 사용하고,
     *  - 없으면 User.name 을 fallback 으로 사용한다.
     */
    private String resolveQuestionerName(ProjectQna qna) {
        Long userId = qna.getQuestioner().getId();

        return supporterProfileRepository.findByUserId(userId)
                .map(SupporterProfile::getDisplayName)
                .filter(name -> name != null && !name.isBlank())
                .orElseGet(() -> qna.getQuestioner().getName());
    }

    private ProjectQnaResponse toResponse(ProjectQna qna) {
        String questionerName = resolveQuestionerName(qna);

        return ProjectQnaResponse.builder()
                .id(qna.getId())
                .questionerName(questionerName)
                .questionerId(qna.getQuestioner().getId())
                .question(qna.getQuestion())
                .answer(qna.getAnswer())
                .status(qna.getStatus() != null ? qna.getStatus().name() : null)
                .createdAt(qna.getCreatedAt())
                .answeredAt(qna.getAnsweredAt())
                .build();
    }
}
