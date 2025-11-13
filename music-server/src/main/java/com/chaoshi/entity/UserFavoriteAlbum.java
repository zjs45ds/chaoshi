package com.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户收藏专辑实体类
 */
@Data
@Entity
public class UserFavoriteAlbum {
    
    @Id
    private Long id;
    private Long userId;
    private Long albumId;
    private LocalDateTime createdAt;
    private Integer deleted = 0;
}
