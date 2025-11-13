package com.chaoshi.dto.request;

import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class LoginRequest {
    
    private String username;
    
    private String password;
}