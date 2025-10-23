package com.example.mooddy.service;

import com.example.mooddy.dto.SignupRequestDto;
import com.example.mooddy.dto.UserDetailResponseDto;
import com.example.mooddy.domain.User;
import com.example.mooddy.exception.UserNotFoundException;
import com.example.mooddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// UserService와 UserDetailsService 두 인터페이스를 구현합니다.
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
    }


    @Override
    public UserDetailResponseDto getUserDetailsByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return UserDetailResponseDto.fromEntity(user);
    }

    @Override
    @Transactional
    public UserDetailResponseDto updateProfile(String email, SignupRequestDto updateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setNickname(updateRequest.getNickname());
        // 필요한 다른 업데이트 로직 추가 가능

        userRepository.save(user);
        return UserDetailResponseDto.fromEntity(user);
    }

    @Override
    @Transactional
    public void changePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
    }

    @Override
    @Transactional
    public String uploadProfileImage(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.getProfileImageUrl() != null) {
            imageService.delete(user.getProfileImageUrl());
        }

        String imageUrl = imageService.upload(file);
        user.setProfileImageUrl(imageUrl);
        userRepository.save(user);
        return imageUrl;
    }

    @Override
    @Transactional
    public void deleteProfileImage(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        imageService.delete(user.getProfileImageUrl());
        user.setProfileImageUrl(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (user.getProfileImageUrl() != null) {
            imageService.delete(user.getProfileImageUrl());
        }
        userRepository.delete(user);
    }
}