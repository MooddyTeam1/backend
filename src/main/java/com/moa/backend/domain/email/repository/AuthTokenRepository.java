package com.moa.backend.domain.email.repository;

import com.moa.backend.domain.email.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByEmailAndTokenAndType(String email, String token, AuthToken.TokenType type);

    Optional<AuthToken> findByTokenAndType(String token, AuthToken.TokenType type);

    void deleteByEmailAndType(String email, AuthToken.TokenType type);
}