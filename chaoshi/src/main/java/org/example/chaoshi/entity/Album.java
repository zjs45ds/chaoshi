package org.example.chaoshi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 专辑实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "albums")
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "专辑名不能为空")
    @Size(max = 100, message = "专辑名长度不能超过100个字符")
    @Column(nullable = false, length = 100)
    private String name;
    
    @Size(max = 255, message = "封面URL长度不能超过255个字符")
    @Column(length = 255)
    private String cover;
    
    @NotNull(message = "歌手ID不能为空")
    @Column(name = "artist_id", nullable = false)
    private Long artistId;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
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