package com.mooddy.backend.feature.playlist.service;

import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.domain.Visibility;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.global.exception.UnauthorizedException;
import org.springframework.stereotype.Component;

/**
 * 플레이리스트 권한 검증 유틸리티
 * - 소유자/협업자 확인
 * - 조회/수정/삭제 권한 검증
 */
@Component
public class PlaylistPermissionValidator {

    /**
     * 소유자인지 확인
     *
     * @param playlist 플레이리스트
     * @param user     사용자
     * @return 소유자이면 true
     */
    public boolean isOwner(Playlist playlist, User user) {
        if (user == null) return false;
        return playlist.getUser().getId().equals(user.getId());
    }

    /**
     * 협업자인지 확인 (PlaylistVisibility에 포함된 사용자)
     *
     * @param playlist 플레이리스트
     * @param user     사용자
     * @return 협업자이면 true
     */
    public boolean isCollaborator(Playlist playlist, User user) {
        if (user == null) return false;
        return playlist.getPlaylistVisibilities().stream()
                .anyMatch(pv -> pv.getUser().getId().equals(user.getId()));
    }

    /**
     * 플레이리스트 조회 가능한지 검증
     * - PUBLIC: 모두 가능
     * - PRIVATE: 소유자만
     * - SHARED: 소유자 + 협업자
     *
     * @param playlist 플레이리스트
     * @param user     사용자
     * @throws UnauthorizedException 권한이 없는 경우
     */
    public void validateCanView(Playlist playlist, User user) {
        Visibility visibility = playlist.getVisibility();

        // PUBLIC: 누구나 가능
        if (visibility == Visibility.PUBLIC) {
            return;
        }

        // 인증 필요
        if (user == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        // PRIVATE: 소유자만
        if (visibility == Visibility.PRIVATE) {
            if (!isOwner(playlist, user)) {
                throw new UnauthorizedException("비공개 플레이리스트는 소유자만 볼 수 있습니다.");
            }
            return;
        }

        // SHARED: 소유자 + 협업자
        if (visibility == Visibility.SHARED) {
            if (!isOwner(playlist, user) && !isCollaborator(playlist, user)) {
                throw new UnauthorizedException("이 플레이리스트에 접근할 권한이 없습니다.");
            }
        }
    }

    /**
     * 플레이리스트 정보 수정 가능한지 검증 (소유자만 가능)
     * - 제목, 설명, 커버 이미지 수정
     * - 공유 설정 변경
     *
     * @param playlist 플레이리스트
     * @param user     사용자
     * @throws UnauthorizedException 권한이 없는 경우
     */
    public void validateCanModifyInfo(Playlist playlist, User user) {
        if (!isOwner(playlist, user)) {
            throw new UnauthorizedException("플레이리스트 정보를 수정할 권한이 없습니다.");
        }
    }

    /**
     * 곡 편집 가능한지 검증 (소유자 + 협업자 가능)
     * - 곡 추가
     * - 곡 삭제
     * - 곡 순서 변경
     *
     * @param playlist 플레이리스트
     * @param user     사용자
     * @throws UnauthorizedException 권한이 없는 경우
     */
    public void validateCanModifyTracks(Playlist playlist, User user) {
        if (user == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        if (!isOwner(playlist, user) && !isCollaborator(playlist, user)) {
            throw new UnauthorizedException("곡을 수정할 권한이 없습니다.");
        }
    }

    /**
     * 플레이리스트 삭제 가능한지 검증 (소유자만 가능)
     *
     * @param playlist 플레이리스트
     * @param user     사용자
     * @throws UnauthorizedException 권한이 없는 경우
     */
    public void validateCanDelete(Playlist playlist, User user) {
        if (!isOwner(playlist, user)) {
            throw new UnauthorizedException("플레이리스트를 삭제할 권한이 없습니다.");
        }
    }
}
