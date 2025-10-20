package org.example.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 排行榜歌曲关联实体类
 */
@Data
@Entity
public class ToplistSong {
    
    @Id
    private Long id;
    private Long toplistId;
    private Long songId;
    private Integer rank;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
