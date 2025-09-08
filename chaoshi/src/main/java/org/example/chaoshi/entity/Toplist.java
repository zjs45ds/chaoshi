package org.example.chaoshi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 排行榜实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "toplists")
public class Toplist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "榜单名称不能为空")
    @Size(max = 100, message = "榜单名称长度不能超过100个字符")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 255, message = "封面URL长度不能超过255个字符")
    @Column(length = 255)
    private String cover;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Size(max = 20, message = "更新频率长度不能超过20个字符")
    @Column(name = "update_frequency", length = 20)
    private String updateFrequency;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted")
    private Integer deleted;
}