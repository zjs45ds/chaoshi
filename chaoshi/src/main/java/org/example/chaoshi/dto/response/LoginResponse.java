package org.example.chaoshi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {
    
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String token;
    private LocalDateTime createdAt;
}