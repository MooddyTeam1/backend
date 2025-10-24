package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.dto.UserSearchResponseDto;
import com.mooddy.backend.feature.user.repository.UserRepository;
import com.mooddy.backend.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserSearchResponseDto> searchUsersByNickname(String nickname, Long currentUserId) {
        // 1. 검색어 validation
        if (nickname == null || nickname.trim().isEmpty()) {
            log.warn("검색어가 비어있습니다.");
            throw new BadRequestException("검색어를 입력해주세요.");
        }

        // 2. 검색어 trim 처리 (앞뒤 공백 제거)
        String trimmedNickname = nickname.trim();
        log.info("사용자 검색 - 검색어: {}, 현재 사용자 ID: {}", trimmedNickname, currentUserId);

        // 3. Repository를 통한 검색
        // - StartingWith: 앞부분 일치 검색
        // - IgnoreCase: 대소문자 구분 없음
        // - AndIdNot: 본인(currentUserId) 제외
        // - Top10: 최대 10개 결과만 반환
        List<User> users = userRepository
                .findTop10ByNicknameStartingWithIgnoreCaseAndIdNot(trimmedNickname, currentUserId);

        log.info("검색 결과: {}개", users.size());

        // 4. Entity를 DTO로 변환
        return users.stream()
                .map(UserSearchResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
