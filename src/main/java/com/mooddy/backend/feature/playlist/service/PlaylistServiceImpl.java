package com.mooddy.backend.feature.playlist.service;

import com.mooddy.backend.external.itunes.service.ItunesService;
import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.PlaylistTrack;
import com.mooddy.backend.feature.playlist.domain.PlaylistVisibility;
import com.mooddy.backend.feature.playlist.domain.Visibility;
import com.mooddy.backend.feature.playlist.dto.PlaylistRequestDto;
import com.mooddy.backend.feature.playlist.dto.PlaylistResponseDto;
import com.mooddy.backend.feature.playlist.repository.PlaylistRepository;
import com.mooddy.backend.feature.playlist.repository.PlaylistTrackRepository;
import com.mooddy.backend.feature.playlist.repository.PlaylistVisibilityRepository;
import com.mooddy.backend.feature.track.domain.Track;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final PlaylistVisibilityRepository playlistVisibilityRepository;
    private final UserRepository userRepository;
    private final ItunesService itunesService;

    /**
     * 플레이리스트 생성
     */
    @Override
    @Transactional
    public PlaylistResponseDto createPlaylist(User user, PlaylistRequestDto request) {
        log.info("플레이리스트 생성 - userId: {}, title: {}", user.getId(), request.title());

        Playlist playlist = Playlist.builder()
                .title(request.title())
                .description(request.description())
                .coverImageUrl(request.coverImageUrl())
                .visibility(request.visibility() != null ? request.visibility() : Visibility.PUBLIC)
                .user(user)
                .build();

        Playlist savedPlaylist = playlistRepository.save(playlist);
        syncSharedUsers(savedPlaylist, savedPlaylist.getVisibility(), request.sharedUserIds(), true);
        log.info("플레이리스트 생성 완료 - id: {}", savedPlaylist.getId());

        Playlist reloaded = playlistRepository.findById(savedPlaylist.getId())
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));
        return PlaylistResponseDto.from(reloaded, user);
    }

    /**
     * 특정 사용자의 플레이리스트 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlaylistResponseDto> getUserPlaylists(Long userId, User requester) {

        List<Playlist> allPlaylists = playlistRepository.findByUserId(userId);

        boolean isOwner = requester != null && requester.getId().equals(userId);

        return allPlaylists.stream()
                .filter(playlist -> {
                    Visibility visibility = playlist.getVisibility();

                    if (visibility == Visibility.PUBLIC) return true;

                    if (isOwner) return true;

                    if (visibility == Visibility.SHARED && requester != null) {
                        return playlist.getPlaylistVisibilities().stream()
                                .anyMatch(pv -> pv.getUser().getId().equals(requester.getId()));
                    }
                    return false;
                })
                .map(playlist -> PlaylistResponseDto.from(playlist, requester))
                .collect(Collectors.toList());
    }

    /**
     * 공개 플레이리스트 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<PlaylistResponseDto> getPublicPlaylists() {
        return playlistRepository.findByVisibility(Visibility.PUBLIC).stream()
                .map(playlist -> PlaylistResponseDto.from(playlist, null))
                .collect(Collectors.toList());
    }

    /**
     * 특정 플레이리스트 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PlaylistResponseDto getPlaylist(Long playlistId, User user) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));

        // ===== 권한 검사 로직 추가 =====
        Visibility visibility = playlist.getVisibility();
        Long ownerId = playlist.getUser().getId();
        Long requesterId = (user != null) ? user.getId() : null;

        // 1. PUBLIC: 모두 접근 가능 → 아무 검사 없이 통과
        if (visibility == Visibility.PUBLIC) {
            return PlaylistResponseDto.from(playlist, user);
        }

        // 인증되지 않은 사용자는 PUBLIC이 아닌 경우 접근 불가
        if (requesterId == null) {
            throw new RuntimeException("이 플레이리스트에 접근할 권한이 없습니다.");
        }

        // 2. PRIVATE: 소유자만 접근 가능
        if (visibility == Visibility.PRIVATE) {
            if (!ownerId.equals(requesterId)) {
                throw new RuntimeException("비공개 플레이리스트는 작성자만 볼 수 있습니다.");
            }
            return PlaylistResponseDto.from(playlist, user);
        }

        // 3. SHARED: 소유자 또는 공유받은 사람만 접근 가능
        if (visibility == Visibility.SHARED) {
            if (ownerId.equals(requesterId)) {
                return PlaylistResponseDto.from(playlist, user);
            }

            boolean isSharedUser = playlist.getPlaylistVisibilities().stream()
                    .anyMatch(pv -> pv.getUser().getId().equals(requesterId));

            if (!isSharedUser) {
                throw new RuntimeException("이 플레이리스트에 접근할 권한이 없습니다.");
            }

            return PlaylistResponseDto.from(playlist, user);
        }

        return PlaylistResponseDto.from(playlist, user);
    }

    /**
     * 플레이리스트 정보 수정
     */
    @Override
    @Transactional
    public PlaylistResponseDto updatePlaylist(Long playlistId, User user, PlaylistRequestDto request) {
        log.info("✏플레이리스트 수정 - playlistId: {}, userId: {}", playlistId, user.getId());

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("플레이리스트를 수정할 권한이 없습니다.");
        }

        if (request.title() != null) {
            playlist.setTitle(request.title());
        }
        if (request.description() != null) {
            playlist.setDescription(request.description());
        }
        if (request.coverImageUrl() != null) {
            playlist.setCoverImageUrl(request.coverImageUrl());
        }
        if (request.visibility() != null) {
            playlist.setVisibility(request.visibility());
        }

        playlist.touch();

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        boolean shouldUpdateSharedUsers = updatedPlaylist.getVisibility() != Visibility.SHARED
                || request.sharedUserIds() != null;
        syncSharedUsers(updatedPlaylist, updatedPlaylist.getVisibility(), request.sharedUserIds(), shouldUpdateSharedUsers);
        log.info("플레이리스트 수정 완료");

        Playlist reloaded = playlistRepository.findById(updatedPlaylist.getId())
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));
        return PlaylistResponseDto.from(reloaded, user);
    }

    /**
     * 플레이리스트 삭제
     */
    @Override
    @Transactional
    public void deletePlaylist(Long playlistId, User user) {
        log.info("🗑플레이리스트 삭제 - playlistId: {}, userId: {}", playlistId, user.getId());

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("플레이리스트를 삭제할 권한이 없습니다.");
        }

        playlistRepository.delete(playlist);
        log.info("플레이리스트 삭제 완료");
    }

    /**
     * 플레이리스트에 곡 추가
     */
    @Override
    @Transactional
    public PlaylistResponseDto addTrackToPlaylist(Long playlistId, User user, Long trackId) {
        log.info("트랙 추가 - playlistId: {}, trackId: {}", playlistId, trackId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("플레이리스트를 수정할 권한이 없습니다.");
        }

        Track track = itunesService.getOrCreateTrackEntity(trackId);

        boolean alreadyExists = playlist.getPlaylistTracks().stream()
                .anyMatch(pt -> pt.getTrack().getId().equals(track.getId()));

        if (alreadyExists) {
            throw new IllegalArgumentException("이미 플레이리스트에 추가된 곡입니다.");
        }

        int nextPosition = playlist.getPlaylistTracks().size();

        PlaylistTrack playlistTrack = PlaylistTrack.builder()
                .playlist(playlist)
                .track(track)
                .position(nextPosition)
                .build();

        playlist.getPlaylistTracks().add(playlistTrack);
        playlistTrackRepository.save(playlistTrack);
        log.info("곡 추가 완료");

        playlist.touch();
        playlistRepository.save(playlist);
        log.info("플레이리스트 갱신 완료");

        return PlaylistResponseDto.from(playlist, user);
    }

    /**
     * 플레이리스트에서 곡 제거
     */
    @Override
    @Transactional
    public void removeTrackFromPlaylist(Long playlistId, User user, Long trackId) {
        log.info("트랙 제거 - playlistId: {}, trackId: {}", playlistId, trackId);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("플레이리스트를 수정할 권한이 없습니다.");
        }

        PlaylistTrack playlistTrack = playlistTrackRepository
                .findByPlaylistIdAndTrackId(playlistId, trackId)
                .orElseThrow(() -> new RuntimeException("플레이리스트에 해당 곡이 없습니다."));

        Integer deletedPosition = playlistTrack.getPosition();
        log.info("삭제할 곡의 position: {}", deletedPosition);

        playlistTrackRepository.deleteByPlaylistIdAndTrackId(playlistId, trackId);
        log.info("곡 삭제 완료");

        playlistTrackRepository.decrementPositionsAfter(playlistId, deletedPosition);
        log.info("순서 재정렬 완료 (position > {} 인 곡들 -1)", deletedPosition);

        playlist.touch();
        playlistRepository.save(playlist);
        log.info("플레이리스트 갱신 완료");
    }

    /**
     * 플레이리스트 내 곡 순서 변경
     */
    @Override
    @Transactional
    public PlaylistResponseDto updateTrackPosition(Long playlistId, User user, Long trackId, Integer newPosition) {
        log.info("트랙 순서 변경 - playlistId: {}, trackId: {}, newPosition: {}", playlistId, trackId, newPosition);

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));

        if (!playlist.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("플레이리스트를 수정할 권한이 없습니다.");
        }

        int playlistSize = playlist.getPlaylistTracks().size();
        if (newPosition < 0 || newPosition >= playlistSize) {
            throw new IllegalArgumentException("요청한 position이 유효한 범위를 벗어났습니다. (유효 범위: 0 ~ " + (playlistSize - 1) + ")");
        }

        PlaylistTrack playlistTrack = playlistTrackRepository.findByPlaylistIdAndTrackId(playlistId, trackId)
                .orElseThrow(() -> new RuntimeException("플레이리스트에 해당 곡이 없습니다."));

        int oldPosition = playlistTrack.getPosition();

        if (oldPosition == newPosition) {
            return PlaylistResponseDto.from(playlist, user);
        }

        playlistTrack.setPosition(-1);
        playlistTrackRepository.save(playlistTrack);
        playlistTrackRepository.flush();

        if (newPosition < oldPosition) {
            List<PlaylistTrack> tracksToShift = playlist.getPlaylistTracks().stream()
                    .filter(ps -> ps.getPosition() >= newPosition && ps.getPosition() < oldPosition)
                    .collect(Collectors.toList());

            for (PlaylistTrack ps : tracksToShift) {
                ps.setPosition(ps.getPosition() + 1);
            }

            playlistTrackRepository.saveAll(tracksToShift);
            playlistTrackRepository.flush();

        } else {
            List<PlaylistTrack> tracksToShift = playlist.getPlaylistTracks().stream()
                    .filter(ps -> ps.getPosition() > oldPosition && ps.getPosition() <= newPosition)
                    .collect(Collectors.toList());

            for (PlaylistTrack ps : tracksToShift) {
                ps.setPosition(ps.getPosition() - 1);
            }

            playlistTrackRepository.saveAll(tracksToShift);
            playlistTrackRepository.flush();
        }

        playlistTrack.setPosition(newPosition);
        playlistTrackRepository.save(playlistTrack);
        log.info("곡 순서 변경 완료");

        playlist.touch();
        playlistRepository.save(playlist);
        log.info("플레이리스트 갱신 완료");

        playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("플레이리스트를 찾을 수 없습니다."));
        return PlaylistResponseDto.from(playlist, user);
    }

    /**
     * 공유 유저 목록 관리
     */
    private void syncSharedUsers(Playlist playlist, Visibility visibility, List<Long> sharedUserIds, boolean shouldUpdateList) {
        List<PlaylistVisibility> current = new ArrayList<>(playlist.getPlaylistVisibilities());

        if (visibility != Visibility.SHARED) {
            if (!current.isEmpty()) {
                playlistVisibilityRepository.deleteAll(current);
                playlist.getPlaylistVisibilities().clear();
            }
            return;
        }

        if (!shouldUpdateList) {
            return;
        }

        Set<Long> desired = new HashSet<>(sharedUserIds != null ? sharedUserIds : Collections.emptyList());
        desired.remove(playlist.getUser().getId());

        for (PlaylistVisibility pv : current) {
            Long userId = pv.getUser().getId();
            if (!desired.contains(userId)) {
                playlistVisibilityRepository.delete(pv);
                playlist.getPlaylistVisibilities().remove(pv);
            } else {
                desired.remove(userId);
            }
        }

        for (Long userId : desired) {
            User sharedUser = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("공유 대상 사용자를 찾을 수 없습니다."));
            PlaylistVisibility visibilityEntry = PlaylistVisibility.builder()
                    .playlist(playlist)
                    .user(sharedUser)
                    .build();
            playlist.getPlaylistVisibilities().add(visibilityEntry);
            playlistVisibilityRepository.save(visibilityEntry);
        }
    }
}