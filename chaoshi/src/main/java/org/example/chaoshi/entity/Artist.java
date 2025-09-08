package org.example.chaoshi.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "歌手名不能为空")
    @Size(max = 100, message = "歌手名长度不能超过100个字符")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    @Column(length = 255)
    private String avatar;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted")
    private Integer deleted;
}