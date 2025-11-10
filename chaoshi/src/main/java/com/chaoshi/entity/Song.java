package com.chaoshi.entity;

import jakarta.persistence.*;
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
    private Long id;
    private String name;
    private Long artistId;
    private Long albumId;
    private String filePath;
    private String lyrics;
    private Integer duration;
    private String cover;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
    

    @Transient
    private String artist;
    
    @Transient
    private String album;
}