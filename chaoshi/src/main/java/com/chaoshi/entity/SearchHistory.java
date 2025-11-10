package com.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 搜索历史实体类
 */
@Data
@Entity
public class SearchHistory {
    
    @Id
    private Long id;
    private Long userId;
    private String keyword;
    private Integer searchCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;
}
