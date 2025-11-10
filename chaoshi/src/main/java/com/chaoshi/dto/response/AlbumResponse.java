package com.chaoshi.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 专辑响应DTO
 */
@Data
public class AlbumResponse {
    
    private Long id;
    private String name;
    private String cover;
    private Long artistId;
    private String artistName;
    private LocalDate releaseDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SongResponse> songs; // 专辑中的歌曲列表
}