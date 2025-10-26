package com.mooddy.backend.feature.comment.service;

import com.mooddy.backend.feature.comment.domain.PlaylistComment;
import com.mooddy.backend.feature.comment.dto.PlaylistCommentRequestDto;
import com.mooddy.backend.feature.comment.dto.PlaylistCommentResponseDto;
import com.mooddy.backend.feature.comment.dto.PlaylistCommentUpdateRequestDto;
import com.mooddy.backend.feature.comment.repository.PlaylistCommentRepository;
import com.mooddy.backend.feature.playlist.domain.Playlist;
import com.mooddy.backend.feature.playlist.repository.PlaylistRepository;
import com.mooddy.backend.feature.playlist.service.PlaylistPermissionValidator;
import com.mooddy.backend.feature.user.domain.User;
import com.mooddy.backend.global.exception.BadRequestException;
import com.mooddy.backend.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistCommentServiceImpl implements PlaylistCommentService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistCommentRepository playlistCommentRepository;
    private final PlaylistPermissionValidator permissionValidator;

    @Override
    @Transactional(readOnly = true)
    public Page<PlaylistCommentResponseDto> getComments(Long playlistId, Pageable pageable, User requester) {
        Playlist playlist = getPlaylist(playlistId);
        permissionValidator.validateCanView(playlist, requester);

        Page<PlaylistComment> rootComments =
                playlistCommentRepository.findByPlaylistIdAndParentIsNull(playlistId, pageable);

        if (rootComments.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        Map<Long, List<PlaylistCommentResponseDto>> repliesMap = loadReplies(rootComments.getContent());

        List<PlaylistCommentResponseDto> content = rootComments.getContent().stream()
                .map(comment -> PlaylistCommentResponseDto.from(
                        comment,
                        repliesMap.getOrDefault(comment.getId(), Collections.emptyList())))
                .toList();

        return new PageImpl<>(content, pageable, rootComments.getTotalElements());
    }

    @Override
    @Transactional
    public PlaylistCommentResponseDto createComment(Long playlistId, PlaylistCommentRequestDto request, User requester) {
        User user = validateRequester(requester);
        Playlist playlist = getPlaylist(playlistId);
        permissionValidator.validateCanView(playlist, user);

        String trimmedContent = trimContent(request.content());

        PlaylistComment parent = null;
        if (request.parentId() != null) {
            parent = playlistCommentRepository.findByIdAndPlaylistId(request.parentId(), playlistId)
                    .orElseThrow(() -> new BadRequestException("부모 댓글을 찾을 수 없습니다."));

            if (parent.getParent() != null) {
                throw new BadRequestException("대댓글에는 답글을 달 수 없습니다.");
            }
        }

        PlaylistComment comment = PlaylistComment.builder()
                .playlist(playlist)
                .user(user)
                .content(trimmedContent)
                .parent(parent)
                .build();

        if (parent != null) {
            parent.getChildren().add(comment);
        }

        PlaylistComment saved = playlistCommentRepository.save(comment);
        return PlaylistCommentResponseDto.from(saved, Collections.emptyList());
    }

    @Override
    @Transactional
    public PlaylistCommentResponseDto updateComment(Long playlistId, Long commentId,
                                                    PlaylistCommentUpdateRequestDto request, User requester) {
        User user = validateRequester(requester);

        PlaylistComment comment = playlistCommentRepository.findByIdAndPlaylistId(commentId, playlistId)
                .orElseThrow(() -> new BadRequestException("댓글을 찾을 수 없습니다."));

        validateCommentOwner(comment, user);

        String trimmedContent = trimContent(request.content());
        comment.changeContent(trimmedContent);

        List<PlaylistCommentResponseDto> replies = loadReplies(List.of(comment))
                .getOrDefault(comment.getId(), Collections.emptyList());

        return PlaylistCommentResponseDto.from(comment, replies);
    }

    @Override
    @Transactional
    public void deleteComment(Long playlistId, Long commentId, User requester) {
        User user = validateRequester(requester);

        PlaylistComment comment = playlistCommentRepository.findByIdAndPlaylistId(commentId, playlistId)
                .orElseThrow(() -> new BadRequestException("댓글을 찾을 수 없습니다."));

        validateCommentOwner(comment, user);

        playlistCommentRepository.delete(comment);
    }

    private Map<Long, List<PlaylistCommentResponseDto>> loadReplies(List<PlaylistComment> parents) {
        List<Long> parentIds = parents.stream()
                .map(PlaylistComment::getId)
                .toList();

        if (parentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<PlaylistComment> replies =
                playlistCommentRepository.findByParentIdInOrderByCreatedAtAsc(parentIds);

        return replies.stream()
                .collect(Collectors.groupingBy(reply -> reply.getParent().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(reply -> PlaylistCommentResponseDto.from(reply, Collections.emptyList()),
                                Collectors.toList())));
    }

    private Playlist getPlaylist(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BadRequestException("플레이리스트를 찾을 수 없습니다."));
    }

    private User validateRequester(User requester) {
        if (requester == null) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        return requester;
    }

    private void validateCommentOwner(PlaylistComment comment, User requester) {
        if (!Objects.equals(comment.getUser().getId(), requester.getId())) {
            throw new UnauthorizedException("댓글을 수정하거나 삭제할 권한이 없습니다.");
        }
    }

    private String trimContent(String content) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.isEmpty()) {
            throw new BadRequestException("댓글 내용을 입력해주세요.");
        }
        return trimmed;
    }
}

