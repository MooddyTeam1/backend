package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.dto.AuthRequest;
import com.mooddy.backend.feature.user.dto.AuthResponse;
import com.mooddy.backend.feature.user.dto.SignupRequest;
import com.mooddy.backend.feature.user.dto.UserDetailResponseDto;
import com.mooddy.backend.feature.user.domain.AuthProvider;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.global.exception.AuthenticationException;
import com.mooddy.backend.global.exception.UserAlreadyExistsException;
import com.mooddy.backend.feature.user.repository.UserRepository;
import com.mooddy.backend.global.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // 회원가입
    public AuthResponse signup(SignupRequest signupRequest) {
        // 닉네임 중복 확인
        if(userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new UserAlreadyExistsException("nickname already exists");
        }
        // 이메일 중복 확인
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .nickname(signupRequest.getNickname())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .birthDate(signupRequest.getBirthDate())
                .onboardingCompleted(false)
                .provider(AuthProvider.LOCAL)
                .build();

        // DB에 저장하고 저장된 객체 반환(DB ID 포함)
        user = userRepository.save(user);

        // JWT 발급 (회원가입시 자동 로그인)
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        UserDetailResponseDto userDto = UserDetailResponseDto.fromEntity(user);

        //AuthResponse 반환
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(userDto)     //화면 표시용
                .build();
    }

    // 로그인
    public AuthResponse login(AuthRequest authRequest) {
        try {
            // 이메일로 로그인 시도
            Authentication authentication =authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            // 인증 성공 후 UserDetailsService 에서 반환된 User 객체 추출
            User user = (User) authentication.getPrincipal();

            // 로그인 성공시 토큰 발급
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return AuthResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user(UserDetailResponseDto.fromEntity(user))
                    .build();

        } catch (AuthenticationException e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }
}