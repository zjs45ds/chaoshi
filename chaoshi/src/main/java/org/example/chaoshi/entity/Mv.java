package org.example.chaoshi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * MV实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "mvs")
public class Mv {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "MV名称不能为空")
    @Size(max = 100, message = "MV名称长度不能超过100个字符")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "歌手ID不能为空")
    @Column(name = "artist_id", nullable = false)
    private Long artistId;
    
    @Size(max = 255, message = "视频路径长度不能超过255个字符")
    @Column(name = "video_path", length = 255)
    private String videoPath;
    
    @Size(max = 255, message = "封面URL长度不能超过255个字符")
    @Column(name = "cover", length = 255)
    private String cover;
    
    // 用于前端显示的歌手名称（非数据库字段）
    @Transient
    private String artist;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "play_count")
    private Long playCount;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted")
    private Integer deleted;
}