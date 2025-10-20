package org.example.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 排行榜实体类
 */
@Data
@Entity
public class Toplist {
    
    @Id
    private Long id;
    private String name;
    private String cover;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}