package com.moa.backend.domain.project.community.service;

import com.moa.backend.domain.project.community.dto.*;
import com.moa.backend.domain.project.community.entity.*;
import com.moa.backend.domain.project.community.repository.*;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectCommunityServiceImpl implements ProjectCommunityService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectCommunityRepository communityRepository;
    private final ProjectCommunityCommentRepository commentRepository;

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
            request.getImageUrls().forEach(url -> {
                community.addImage(
                        ProjectCommunityImage.builder()
                                .imageUrl(url)
                                .build()
                );
            });
        }

        return CommunityResponse.from(communityRepository.save(community));
    }

    @Override
    public List<CommunityResponse> getCommunityList(Long projectId) {
        return communityRepository.findByProject_IdOrderByCreatedAtDesc(projectId)
                .stream().map(CommunityResponse::from)
                .toList();
    }

    @Override
    public CommunityResponse getCommunity(Long communityId) {
        ProjectCommunity c = communityRepository.findById(communityId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        return CommunityResponse.from(c);
    }

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

    @Override
    @Transactional
    public CommunityCommentResponse addComment(Long userId, Long communityId, CommunityCommentRequest request) {

        ProjectCommunity community = communityRepository.findById(communityId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        ProjectCommunityComment comment = ProjectCommunityComment.builder()
                .community(community)
                .user(user)
                .content(request.getContent())
                .build();

        return CommunityCommentResponse.from(commentRepository.save(comment));
    }

    @Override
    public List<CommunityCommentResponse> getComments(Long communityId) {
        return commentRepository.findByCommunity_IdOrderByCreatedAtAsc(communityId)
                .stream()
                .map(CommunityCommentResponse::from)
                .toList();
    }

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
}
