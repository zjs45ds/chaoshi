package org.example.chaoshi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 专辑实体类
 */
@Data
@Entity
public class Album {

    @Id
    private Long id;
    private String name;
    private String cover;
    private Long artistId;
    private LocalDate releaseDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer deleted;

}