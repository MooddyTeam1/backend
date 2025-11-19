package com.moa.backend.domain.project.community.service;

import com.moa.backend.domain.project.community.dto.*;
import java.util.List;

public interface ProjectCommunityService {

    CommunityResponse createCommunity(Long userId, Long projectId, CommunityCreateRequest request);

    List<CommunityResponse> getCommunityList(Long projectId);

    CommunityResponse getCommunity(Long communityId);

    void deleteCommunity(Long userId, Long communityId);

    CommunityCommentResponse addComment(Long userId, Long communityId, CommunityCommentRequest request);

    List<CommunityCommentResponse> getComments(Long communityId);

    void deleteComment(Long userId, Long commentId);
}
