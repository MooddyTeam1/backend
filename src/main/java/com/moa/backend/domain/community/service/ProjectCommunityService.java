package com.moa.backend.domain.community.service;

import com.moa.backend.domain.community.dto.*;
import java.util.List;

public interface ProjectCommunityService {
    CommunityResponse createCommunity(Long userId, Long projectId, CommunityCreateRequest request);

    List<CommunityResponse> getCommunityList(Long projectId, Long userId);

    CommunityResponse getCommunity(Long communityId, Long userId);

    void deleteCommunity(Long userId, Long communityId);

    CommunityCommentResponse addComment(Long userId, Long communityId, CommunityCommentRequest request);

    List<CommunityCommentResponse> getComments(Long communityId, Long userId);

    void deleteComment(Long userId, Long commentId);

    CommunityCommentResponse updateComment(Long userId, Long commentId, CommunityCommentUpdateRequest request);

    void likeComment(Long userId, Long commentId);

    void unLikeComment(Long userId, Long commentId);

    long getLikeCount(Long commentId);

    void likeCommunity(Long userId, Long communityId);

    void unLikeCommunity(Long userId, Long communityId);

    long getCommunityLikeCount(Long communityId);
}
