package org.example.chaoshi.service;

import org.example.chaoshi.dto.request.LoginRequest;
import org.example.chaoshi.dto.request.RegisterRequest;
import org.example.chaoshi.dto.response.LoginResponse;
import org.example.chaoshi.dto.response.UserResponse;
import org.example.chaoshi.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户Service接口
 */
public interface UserService {
    
    /**
     * 用户注册
     * @param registerRequest 注册请求
     * @return 用户信息
     */
    UserResponse register(RegisterRequest registerRequest);
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应（包含token）
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体
     */
    User findByUsername(String username);
    
    /**
     * 根据ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserResponse getUserInfo(Long userId);
    
    /**
     * 上传用户头像
     * @param userId 用户ID
     * @param file 头像文件
     * @return 头像URL
     */
    String uploadAvatar(Long userId, MultipartFile file);
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}