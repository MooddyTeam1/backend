package com.mooddy.backend.feature.user.service;

import com.mooddy.backend.feature.user.domain.Profile;
import com.mooddy.backend.feature.user.dto.ProfileRequest;
import com.mooddy.backend.feature.user.repository.ProfileRepository;
import com.mooddy.backend.global.exception.ProfileNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ImageService imageService;

    public Profile createProfile(ProfileRequest request) {
        Profile profile = Profile.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .bio(request.getBio())
                .location(request.getLocation())
                .favoriteGenres(request.getFavoriteGenres())
                .favoriteArtists(request.getFavoriteArtists())
                .musicStyle(request.getMusicStyle())
                .spotifyLink(request.getSpotifyLink())
                .youtubeMusicLink(request.getYoutubeMusicLink())
                .appleMusicLink(request.getAppleMusicLink())
                .build();
        return profileRepository.save(profile);
    }

    public Profile updateProfile(String email, ProfileRequest request) {
        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException(email));

        profile.setUsername(request.getUsername());
        profile.setBio(request.getBio());
        profile.setLocation(request.getLocation());
        profile.setFavoriteGenres(request.getFavoriteGenres());
        profile.setFavoriteArtists(request.getFavoriteArtists());
        profile.setMusicStyle(request.getMusicStyle());
        profile.setSpotifyLink(request.getSpotifyLink());
        profile.setYoutubeMusicLink(request.getYoutubeMusicLink());
        profile.setAppleMusicLink(request.getAppleMusicLink());

        return profileRepository.save(profile);
    }

    public Profile getProfileByEmail(String email) {
        return profileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException(email));
    }

    public String uploadProfileImage(String email, MultipartFile file) {
        Profile profile = getProfileByEmail(email);
        String imageUrl = imageService.upload(file);
        profile.setProfileImageUrl(imageUrl);
        profileRepository.save(profile);
        return imageUrl;
    }

    public void deleteProfileImage(String email) {
        Profile profile = getProfileByEmail(email);
        imageService.delete(profile.getProfileImageUrl());
        profile.setProfileImageUrl(null);
        profileRepository.save(profile);
    }

    public void withdrawAccount(String email) {
        Profile profile = getProfileByEmail(email);
        profileRepository.delete(profile);
    }
}
