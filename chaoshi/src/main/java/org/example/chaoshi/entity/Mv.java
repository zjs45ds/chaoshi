package org.example.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * MV实体类
 */
@Data
@Entity
public class Mv {
    
    @Id
    private Long id;
    private String name;
    private Long artistId;
    private String videoPath;
    private String cover;
    private String artist;
    private Integer duration;
    private String description;
    private Long playCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}