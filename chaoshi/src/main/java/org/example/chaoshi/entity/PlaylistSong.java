package org.example.chaoshi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 播放列表歌曲关联实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "playlist_songs")
public class PlaylistSong {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "播放列表ID不能为空")
    @Column(name = "playlist_id", nullable = false)
    private Long playlistId;
    
    @NotNull(message = "歌曲ID不能为空")
    @Column(name = "song_id", nullable = false)
    private Long songId;
    
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "deleted")
    private Integer deleted;
}