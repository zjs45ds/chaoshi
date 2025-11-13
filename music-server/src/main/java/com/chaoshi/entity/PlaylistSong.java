package com.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 播放列表歌曲关联实体类
 */
@Data
@Entity
public class PlaylistSong {
    
    @Id
    private Long id;
    private Long playlistId;
    private Long songId;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private Integer deleted;
}