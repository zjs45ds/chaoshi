package org.example.chaoshi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 歌曲实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "songs")
public class Song {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "歌曲名不能为空")
    @Size(max = 100, message = "歌曲名长度不能超过100个字符")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotNull(message = "歌手ID不能为空")
    @Column(name = "artist_id", nullable = false)
    private Long artistId;
    
    @Column(name = "album_id")
    private Long albumId;
    
    @Size(max = 255, message = "音频文件路径长度不能超过255个字符")
    @Column(name = "file_path", length = 255)
    private String filePath;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String lyrics;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "cover", length = 255)
    private String cover;
    
    @Column(name = "play_count")
    private Long playCount;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted")
    private Integer deleted;
}