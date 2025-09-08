package org.example.chaoshi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * MV响应DTO
 */
@Data
public class MvResponse {
    
    private Long id;
    private String name;
    private Long artistId;
    private String artistName;
    private String cover;
    private String videoPath;
    private Integer duration;
    private String description;
    private Long playCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}