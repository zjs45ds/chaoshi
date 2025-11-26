package com.chaoshi.dto.request;

import lombok.Data;
/**
 * 用户个人资料更新请求DTO
 */
@Data
public class ProfileRequest {
    
    private String username;
    private String email;
    private String phone;
    private String bio;
    private String avatar;

}