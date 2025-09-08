package org.example.chaoshi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "播放列表名称不能为空")
    @Size(max = 100, message = "播放列表名称长度不能超过100个字符")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "创建者ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Size(max = 255, message = "封面URL长度不能超过255个字符")
    @Column(name = "cover_url", length = 255)
    private String coverUrl;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_public")
    private Boolean isPublic;
    
    @Column(name = "song_count")
    private Integer songCount;
    
    @Column(name = "play_count")
    private Long playCount;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted")
    private Integer deleted;
}