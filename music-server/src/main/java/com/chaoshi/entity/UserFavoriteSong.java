package com.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户收藏歌曲实体类
 */
@Data
@Entity
public class UserFavoriteSong {
    
    @Id
    private Long id;
    private Long userId;
    private Long songId;
    private LocalDateTime createdAt;
    private Integer deleted;
}