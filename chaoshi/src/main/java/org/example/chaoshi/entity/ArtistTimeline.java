package org.example.chaoshi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 歌手时间线实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "artist_timelines")
public class ArtistTimeline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "歌手ID不能为空")
    @Column(name = "artist_id", nullable = false)
    private Long artistId;
    
    @Column(name = "event_type", length = 50)
    private String eventType;
    
    @Column(name = "title", length = 100)
    private String title;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted")
    private Integer deleted;
}