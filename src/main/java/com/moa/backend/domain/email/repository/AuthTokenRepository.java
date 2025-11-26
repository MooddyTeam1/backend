package com.moa.backend.domain.email.repository;

import com.moa.backend.domain.email.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 한글 설명:
 *  - 이메일/토큰 기반으로 인증/비밀번호 재설정 토큰을 조회/삭제하는 리포지토리
 */
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    // 한글 설명: 이메일 + 토큰 + 타입으로 토큰 조회 (인증번호/코드 검증용)
    Optional<AuthToken> findByEmailAndTokenAndType(String email, String token, AuthToken.TokenType type);

    // 한글 설명: 토큰 값 + 타입으로 토큰 조회 (비밀번호 재설정 링크용)
    Optional<AuthToken> findByTokenAndType(String token, AuthToken.TokenType type);

    // 한글 설명: 이메일 + 타입 기준으로 기존 토큰 일괄 삭제 (새 토큰 발급 전에 호출)
    void deleteByEmailAndType(String email, AuthToken.TokenType type);
}
