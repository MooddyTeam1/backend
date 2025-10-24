package com.mooddy.backend.feature.user.repository;

import com.mooddy.backend.feature.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
    Optional<User> findByEmail(String email);
    boolean existsByNickname(String username);
    boolean existsByEmail(String email);

    /**
     * 닉네임으로 사용자 검색 (앞부분 일치, 대소문자 무시, 본인 제외)
     * @param nickname 검색할 닉네임 (앞부분 일치)
     * @param userId 제외할 사용자 ID (본인)
     * @return 최대 10개의 검색 결과
     */
    List<User> findTop10ByNicknameStartingWithIgnoreCaseAndIdNot(String nickname, Long userId);
}
