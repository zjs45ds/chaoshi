package org.example.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 播放列表实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "playlists")
public class Playlist {
    
    @Id
    private Long id;
    private String name;
    private Long userId;
    private String coverUrl;
    private String description;
    private Boolean isPublic;
    private Integer songCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}