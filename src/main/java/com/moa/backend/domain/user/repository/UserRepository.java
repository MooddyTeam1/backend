package com.moa.backend.domain.user.repository;

import com.moa.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * ✅ UserRepository
 * - 일반 로그인 & 소셜 로그인 유저 공용 Repository
 * - 이메일 / provider 기반 조회 모두 지원
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ✅ 이메일 존재 여부 확인
     * - 회원가입 시 중복 체크 등에 사용
     */
    boolean existsByEmail(String email);

    /**
     * ✅ 이메일 기반 조회
     * - 기본 로그인 및 JWT 인증 시 사용
     */
    Optional<User> findByEmail(String email);

    /**
     * ✅ 소셜 로그인 사용자 조회
     * - provider + providerId 로 User 찾기
     * - 내부적으로 socialConnections 엔티티 조인
     */
    @Query("SELECT u FROM User u JOIN u.socialConnections sc " +
            "WHERE sc.provider = :provider AND sc.providerId = :providerId")
    Optional<User> findByProviderAndProviderId(
            @Param("provider") String provider,
            @Param("providerId") String providerId
    );

    // 버그 수정: role 컬럼이 UserRole(enum)이므로 파라미터도 enum으로 맞춤 (기존 String → UserRole)
    List<User> findByRole(com.moa.backend.domain.user.entity.UserRole role); // 역할로 유저 조회

    // ========== 통계 API용 메서드 ==========

    /**
     * 기간별 신규 가입자 수
     */
    Long countByCreatedAtBetween(
            java.time.LocalDateTime startDateTime,
            java.time.LocalDateTime endDateTime
    );
}
