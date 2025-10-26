package com.mooddy.backend.external.itunes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * iTunes API에서 반환하는 Album(Collection) 정보를 받는 DTO
 * wrapperType이 "collection"이고 collectionType이 "Album"인 경우 사용
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItunesAlbumDto {
    /**
     * 응답 타입 (항상 "collection")
     */
    private String wrapperType;

    /**
     * 컬렉션 타입 (보통 "Album")
     */
    private String collectionType;

    /**
     * 앨범 고유 ID
     */
    private Long collectionId;

    /**
     * 앨범명
     */
    private String collectionName;

    /**
     * 아티스트 고유 ID
     */
    private Long artistId;

    /**
     * 아티스트 이름
     */
    private String artistName;

    /**
     * 앨범 커버 이미지 60x60
     */
    private String artworkUrl60;

    /**
     * 앨범 커버 이미지 100x100
     */
    private String artworkUrl100;

    /**
     * 앨범 내 트랙 수
     */
    private Integer trackCount;

    /**
     * 발매일
     */
    private String releaseDate;

    /**
     * 주요 장르명
     */
    private String primaryGenreName;

}
