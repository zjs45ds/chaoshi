package org.example.chaoshi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 歌手实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "artists")
public class Artist {
    
    @Id
    private Long id;
    private String name;
    private String avatar;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}