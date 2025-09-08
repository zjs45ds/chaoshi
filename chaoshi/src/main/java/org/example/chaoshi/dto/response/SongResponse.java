package org.example.chaoshi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 歌曲响应DTO
 */
@Data
public class SongResponse {
    
    private Long id;
    private String name;
    private Long albumId;
    private String albumName;
    private Long artistId;
    private String artistName;
    private Integer duration;
    private String filePath;
    private String lyrics;
    private String cover;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}