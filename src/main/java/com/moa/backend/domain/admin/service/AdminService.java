package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.UserResponse;

import java.util.List;

public interface AdminService {

    //판매자 승인
    void approveCreator(Long userId);

    //판매자 반려
    void rejectCreator(Long userId);

    //승인 대기목록
    List<UserResponse> getPendingCreators();
}
