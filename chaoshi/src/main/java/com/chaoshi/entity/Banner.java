package com.chaoshi.entity;


import lombok.Data;
import java.time.LocalDateTime;

/**
 * 轮播图实体类
 */
@Data
public class Banner {

    private Long id;

    private String title;

    private String imgUrl;

    private String link;

    private Integer sort;

    private Boolean enabled;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer deleted;
}