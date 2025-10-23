package com.example.mooddy.service;

import com.example.mooddy.dto.AuthResponseDto;
import com.example.mooddy.dto.LoginRequestDto;
import com.example.mooddy.dto.SignupRequestDto;
import com.example.mooddy.dto.UserDetailResponseDto;
import com.example.mooddy.domain.User;
import com.example.mooddy.exception.AuthenticationException;
import com.example.mooddy.exception.UserAlreadyExistsException;
import com.example.mooddy.repository.UserRepository;
import com.example.mooddy.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 파일명: AuthServiceImpl.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService; // JWT 서비스가 있다고 가정
    private final AuthenticationManager authenticationManager; // 인증 매니저가 있다고 가정

    @Override
    @Transactional
    public AuthResponseDto signup(SignupRequestDto request) {
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserAlreadyExistsException("nickname already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("email already exists");
        }

        // 🚨 통합된 User 엔티티 생성 (SignupRequestDto + ProfileRequest의 모든 내용 포함)
        User user = User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .birthDate(request.getBirthDate())
                .provider(User.AuthProvider.LOCAL)
                .username(request.getUsername() != null ? request.getUsername() : request.getNickname())
                .bio(request.getBio())
                .location(request.getLocation())
                .favoriteGenres(request.getFavoriteGenres())
                .favoriteArtists(request.getFavoriteArtists())
                .musicStyle(request.getMusicStyle())
                .spotifyLink(request.getSpotifyLink())
                .youtubeMusicLink(request.getYoutubeMusicLink())
                .appleMusicLink(request.getAppleMusicLink())
                .enabled(true)
                .onboardingCompleted(true)
                .build();

        User savedUser = userRepository.save(user);

        // 토큰 발급
        String jwtToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .user(UserDetailResponseDto.fromEntity(savedUser))
                .build();
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            // 토큰 발급
            String jwtToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return AuthResponseDto.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user(UserDetailResponseDto.fromEntity(user))
                    .build();

        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Invalid email or password");
        }
    }
}