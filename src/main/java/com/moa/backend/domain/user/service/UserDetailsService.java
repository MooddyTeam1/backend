package com.moa.backend.domain.user.service;

import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ✅ UserDetailsService
 * - Spring Security가 인증 시 자동으로 호출하는 클래스
 * - JwtAuthenticationFilter에서도 의존성 주입받아 사용됨
 * - DB에서 유저(email 기반) 정보를 조회하여 인증 객체를 생성
 */
@Slf4j
@Service("userDetailsService") // ⚙️ 이름 명시 (SecurityConfig 자동 연결)
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    /**
     * ✅ username(email)으로 유저를 조회해 UserDetails 객체로 변환
     * - JwtAuthenticationFilter에서 토큰 검증 후 이 메서드 호출됨
     * - 이메일 기준으로 DB 조회, 없으면 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("🔍 UserDetailsService.loadUserByUsername() 실행 - email: {}", email);

        // ✅ 이메일 기준 DB 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저를 찾을 수 없습니다: " + email));

        // ✅ 스프링 시큐리티에서 사용하는 UserDetails 객체 반환
        // User 엔티티의 role(Enum)을 Security 권한으로 매핑
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())      // principal (email)
                .password(user.getPassword())    // 암호화된 비밀번호
                .roles(user.getRole().name())    // 예: USER, ADMIN
                .build();
    }
}
