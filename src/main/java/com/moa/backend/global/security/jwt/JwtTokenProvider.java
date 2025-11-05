package com.moa.backend.global.security.jwt;

import com.moa.backend.global.error.AppException;
import com.moa.backend.global.error.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";

    private final SecretKey secretKey;
    private final long accessTokenValidityInMillis;
    private final long refreshTokenValidityInMillis;
    private final String issuer;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-validity-in-seconds:3600}") long accessTokenValidityInSeconds,
        @Value("${jwt.refresh-token-validity-in-seconds:1209600}") long refreshTokenValidityInSeconds,
        @Value("${jwt.issuer:moa-backend}") String issuer
    ) {
        this.secretKey = resolveSecretKey(secret);
        this.accessTokenValidityInMillis = accessTokenValidityInSeconds * 1000L;
        this.refreshTokenValidityInMillis = refreshTokenValidityInSeconds * 1000L;
        this.issuer = issuer;
    }

    public String generateAccessToken(Long userId, String email, String role) {
        return generateToken(userId, email, role, TokenType.ACCESS, accessTokenValidityInMillis);
    }

    public String generateRefreshToken(Long userId, String email, String role) {
        return generateToken(userId, email, role, TokenType.REFRESH, refreshTokenValidityInMillis);
    }

    private String generateToken(Long userId, String email, String role, TokenType tokenType, long validity) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(validity);

        return Jwts.builder()
            .setIssuer(issuer)
            .setSubject(String.valueOf(userId))
            .claim(CLAIM_USER_ID, userId)
            .claim(CLAIM_EMAIL, email)
            .claim(CLAIM_ROLE, role)
            .claim(CLAIM_TYPE, tokenType.name())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiry))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean validateToken(String token) {
        return validateToken(token, TokenType.ACCESS);
    }

    public boolean validateToken(String token, TokenType expectedType) {
        try {
            parseClaims(token, expectedType);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token, TokenType.ACCESS);

        Long userId = extractLongClaim(claims, CLAIM_USER_ID);
        String email = claims.get(CLAIM_EMAIL, String.class);
        String role = claims.get(CLAIM_ROLE, String.class);

        if (userId == null || email == null || role == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        JwtUserPrincipal principal = new JwtUserPrincipal(userId, email, role);

        return new UsernamePasswordAuthenticationToken(
            principal,
            token,
            List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }

    public long getAccessTokenValidityInSeconds() {
        return accessTokenValidityInMillis / 1000L;
    }

    public long getRefreshTokenValidityInSeconds() {
        return refreshTokenValidityInMillis / 1000L;
    }

    public JwtUserPrincipal getPrincipalFromRefreshToken(String token) {
        Claims claims = parseClaims(token, TokenType.REFRESH);

        Long userId = extractLongClaim(claims, CLAIM_USER_ID);
        String email = claims.get(CLAIM_EMAIL, String.class);
        String role = claims.get(CLAIM_ROLE, String.class);

        if (userId == null || email == null || role == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        return new JwtUserPrincipal(userId, email, role);
    }

    private Claims parseClaims(String token, TokenType expectedType) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
            String type = claims.get(CLAIM_TYPE, String.class);
            if (type == null || !expectedType.name().equals(type)) {
                throw new AppException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
            }
            return claims;
        } catch (Exception ex) {
            throw new AppException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }
    }

    private Long extractLongClaim(Claims claims, String name) {
        Object value = claims.get(name);
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private SecretKey resolveSecretKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT 시크릿 키가 설정되어 있지 않습니다.");
        }
        try {
            byte[] keyBytes;
            if (secret.length() % 4 == 0 && secret.matches("^[A-Za-z0-9+/=]+$")) {
                keyBytes = Decoders.BASE64.decode(secret);
            } else {
                keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            }
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("JWT 시크릿 키 형식이 올바르지 않습니다.", ex);
        }
    }

    public enum TokenType {
        ACCESS,
        REFRESH
    }
}
