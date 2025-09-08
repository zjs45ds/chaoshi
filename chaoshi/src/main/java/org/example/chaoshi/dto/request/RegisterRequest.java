package org.example.chaoshi.dto.request;

import lombok.Data;

/**
 * 用户注册请求DTO
 */
@Data
public class RegisterRequest {
    
    private String username;
    
    private String password;
    
    private String email;
    
    private String phone;
    
    private String nickname;
}