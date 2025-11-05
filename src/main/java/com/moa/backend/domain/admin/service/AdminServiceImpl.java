package com.moa.backend.domain.admin.service;

import com.moa.backend.domain.admin.dto.UserResponse;
import com.moa.backend.domain.user.entity.CreatorStatus;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    // 판매자 승인
    @Override
    public void approveCreator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.approveCreator();
    }

    // 판매자 승인 반려
    @Override
    public void rejectCreator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.rejectCreator();
    }

    // 승인 대기목록 조회
    @Override
    public List<UserResponse> getPendingCreators() {
        return userRepository.findByCreatorStatus(CreatorStatus.PENDING)
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }
}
