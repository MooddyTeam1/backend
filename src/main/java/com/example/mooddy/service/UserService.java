package com.example.mooddy.service;

import com.example.mooddy.entity.UserProfile;
import com.example.mooddy.exception.UserNotFoundException;

import com.example.mooddy.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository UserProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfile registerUser(String email, String password, String username, String birthday) {
        UserProfile userProfile = UserProfile.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .username(username)
                .build();
        return UserProfileRepository.save(userProfile);
    }

    public UserProfile getUserByEmail(String email) {
        return UserProfileRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void changePassword(String email, String newPassword) {
        UserProfile userProfile = getUserByEmail(email);
        userProfile.setPassword(passwordEncoder.encode(newPassword));
        UserProfileRepository.save(userProfile);
    }

    public void deleteUser(String email) {
        UserProfile userProfile = getUserByEmail(email);
        UserProfileRepository.delete(userProfile);
    }
}
