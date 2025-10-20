package org.example.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户收藏MV实体类
 */
@Data
@Entity
public class UserFavoriteMv {
    
    @Id
    private Long id;
    private Long userId;
    private Long mvId;
    private LocalDateTime createdAt;
    private Integer deleted = 0;
}