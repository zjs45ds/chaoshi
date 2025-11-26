package com.chaoshi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 */
@Data
public class UserResponse {
    
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String avatar;
    private String bio;
    private String backgroundUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}