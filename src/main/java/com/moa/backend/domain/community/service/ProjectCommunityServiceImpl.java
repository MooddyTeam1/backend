package com.moa.backend.domain.community.service;

import com.moa.backend.domain.community.dto.*;
import com.moa.backend.domain.community.entity.*;
import com.moa.backend.domain.community.repository.*;

import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectCommunityServiceImpl implements ProjectCommunityService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectCommunityRepository communityRepository;
    private final ProjectCommunityCommentRepository commentRepository;
    private final CommunityCommentLikeRepository commentLikeRepository;
    private final ProjectCommunityLikeRepository communityLikeRepository;

    // ===================== 커뮤니티 생성 ======================
    @Override
    @Transactional
    public CommunityResponse createCommunity(Long userId, Long projectId, CommunityCreateRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        ProjectCommunity community = ProjectCommunity.builder()
                .project(project)
                .user(user)
                .content(request.getContent())
                .build();

        if (request.getImageUrls() != null) {
            request.getImageUrls().forEach(url ->
                    community.addImage(ProjectCommunityImage.builder()
                            .imageUrl(url)
                            .build())
            );
        }

        // 기본값으로 likeCount 0, liked false
        return CommunityResponse.from(communityRepository.save(community), 0, false);
    }

    // ===================== 커뮤니티 목록 조회 ======================
    @Override
    public List<CommunityResponse> getCommunityList(Long projectId, Long userId) {
        return communityRepository.findByProject_IdOrderByCreatedAtDesc(projectId)
                .stream()
                .map(c -> {
                    long likeCount = communityLikeRepository.countByCommunity_Id(c.getId());
                    boolean liked = communityLikeRepository.existsByCommunity_IdAndUser_Id(c.getId(), userId);
                    return CommunityResponse.from(c, likeCount, liked);
                })
                .toList();
    }

    // ===================== 커뮤니티 단건 조회 ======================
    @Override
    public CommunityResponse getCommunity(Long communityId, Long userId) {
        ProjectCommunity c = communityRepository.findById(communityId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        long likeCount = communityLikeRepository.countByCommunity_Id(communityId);
        boolean liked = communityLikeRepository.existsByCommunity_IdAndUser_Id(communityId, userId);

        return CommunityResponse.from(c, likeCount, liked);
    }

    // ===================== 커뮤니티 삭제 ======================
    @Override
    @Transactional
    public void deleteCommunity(Long userId, Long communityId) {
        ProjectCommunity c = communityRepository.findById(communityId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!c.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        communityRepository.delete(c);
    }

    // ===================== 댓글 생성 (일반 댓글 + 대댓글) ======================
    @Override
    @Transactional
    public CommunityCommentResponse addComment(Long userId, Long communityId, CommunityCommentRequest request) {
        ProjectCommunity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        ProjectCommunityComment parent = null;
        if (request.getParentCommentId() != null) {
            parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

            // 부모 댓글이 다른 커뮤니티에 속해 있으면 방어
            if (!parent.getCommunity().getId().equals(communityId)) {
                throw new AppException(ErrorCode.BUSINESS_CONFLICT, "부모 댓글이 이 게시글에 속해 있지 않습니다.");
            }
        }

        ProjectCommunityComment comment = ProjectCommunityComment.builder()
                .community(community)
                .user(user)
                .parent(parent)
                .content(request.getContent())
                .build();

        ProjectCommunityComment saved = commentRepository.save(comment);

        return CommunityCommentResponse.from(
                saved,
                0,
                false
        );
    }

    // ===================== 댓글 목록 조회 (트리 구조) ======================
    @Override
    public List<CommunityCommentResponse> getComments(Long communityId, Long userId) {

        // 이 커뮤니티의 모든 댓글 (일반 + 대댓글 포함)
        List<ProjectCommunityComment> comments =
                commentRepository.findByCommunity_IdOrderByCreatedAtAsc(communityId);

        if (comments.isEmpty()) {
            return List.of();
        }

        // 1차: 모든 댓글을 DTO로 변환 + map에 저장
        Map<Long, CommunityCommentResponse> dtoMap = new HashMap<>();

        for (ProjectCommunityComment c : comments) {
            long likeCount = commentLikeRepository.countByComment_Id(c.getId());
            boolean liked = commentLikeRepository.existsByComment_IdAndUser_Id(c.getId(), userId);

            CommunityCommentResponse dto = CommunityCommentResponse.from(c, likeCount, liked);
            dto.setReplies(new java.util.ArrayList<>()); // 자식 리스트 초기화

            dtoMap.put(c.getId(), dto);
        }

        // 2차: parentCommentId 기준으로 트리 구성
        List<CommunityCommentResponse> topLevel = new java.util.ArrayList<>();

        for (ProjectCommunityComment c : comments) {
            CommunityCommentResponse current = dtoMap.get(c.getId());

            if (c.getParent() == null) {
                // 최상위 댓글
                topLevel.add(current);
            } else {
                // 대댓글 -> 부모 DTO 찾아서 replies에 추가
                CommunityCommentResponse parentDto = dtoMap.get(c.getParent().getId());
                if (parentDto != null) {
                    parentDto.getReplies().add(current);
                }
            }
        }

        // createdAt 기준 정렬 (원하면)
        topLevel = topLevel.stream()
                .sorted(Comparator.comparing(CommunityCommentResponse::getCreatedAt))
                .collect(Collectors.toList());

        return topLevel;
    }

    // ===================== 댓글 수정 ======================
    @Override
    @Transactional
    public CommunityCommentResponse updateComment(Long userId, Long commentId, CommunityCommentUpdateRequest request) {
        ProjectCommunityComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!c.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        c.setContent(request.getContent());

        long likeCount = commentLikeRepository.countByComment_Id(c.getId());
        boolean liked = commentLikeRepository.existsByComment_IdAndUser_Id(c.getId(), userId);

        return CommunityCommentResponse.from(c, likeCount, liked);
    }

    // ===================== 댓글 삭제 ======================
    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        ProjectCommunityComment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        if (!c.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        commentRepository.delete(c);
    }

    // ===================== 댓글 좋아요 ======================
    @Override
    @Transactional
    public void likeComment(Long userId, Long commentId) {
        ProjectCommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        boolean alreadyLiked = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);

        if (alreadyLiked) {
            throw new AppException(ErrorCode.BUSINESS_CONFLICT, "이미 좋아요를 누르셨습니다.");
        }

        commentLikeRepository.save(
                CommunityCommentLike.builder()
                        .comment(comment)
                        .user(userRepository.getReferenceById(userId))
                        .build()
        );
    }

    @Override
    @Transactional
    public void unLikeComment(Long userId, Long commentId) {
        commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
    }

    @Override
    public long getLikeCount(Long commentId) {
        return commentLikeRepository.countByComment_Id(commentId);
    }

    // ===================== 커뮤니티 좋아요 ======================
    @Override
    @Transactional
    public void likeCommunity(Long userId, Long communityId) {
        ProjectCommunity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        boolean alreadyLiked = communityLikeRepository.existsByCommunity_IdAndUser_Id(communityId, userId);

        if (alreadyLiked) {
            throw new AppException(ErrorCode.BUSINESS_CONFLICT, "이미 좋아요했습니다.");
        }

        communityLikeRepository.save(
                ProjectCommunityLike.builder()
                        .community(community)
                        .user(userRepository.getReferenceById(userId))
                        .build()
        );
    }

    @Override
    @Transactional
    public void unLikeCommunity(Long userId, Long communityId) {
        communityLikeRepository.deleteByCommunity_IdAndUser_Id(communityId, userId);
    }

    @Override
    public long getCommunityLikeCount(Long communityId) {
        return communityLikeRepository.countByCommunity_Id(communityId);
    }
}
