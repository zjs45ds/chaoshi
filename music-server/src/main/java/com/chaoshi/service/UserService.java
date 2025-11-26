package com.chaoshi.service;

import com.chaoshi.dto.request.LoginRequest;
import com.chaoshi.dto.request.ProfileRequest;
import com.chaoshi.dto.request.RegisterRequest;
import com.chaoshi.dto.response.LoginResponse;
import com.chaoshi.dto.response.UserResponse;
import com.chaoshi.entity.User;

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
     * 修改密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 获取用户个人资料
     * @param userId 用户ID
     * @return 用户个人资料
     */
    UserResponse getUserProfile(Long userId);
    
    /**
     * 更新用户个人资料
     * @param userId 用户ID
     * @param profileRequest 个人资料请求
     * @return 更新后的用户信息
     */
    UserResponse updateUserProfile(Long userId, ProfileRequest profileRequest);
    
    /**
     * 检查用户名是否可用
     * @param username 用户名
     * @param currentUserId 当前用户ID（可选，用于排除自己）
     * @return 用户名是否可用
     */
    boolean isUsernameAvailable(String username, Long currentUserId);
    
    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 是否更新成功
     */
    boolean updateUserAvatar(Long userId, String avatarUrl);

    /**
     * 更新用户背景
     * @param userId 用户ID
     * @param backgroundUrl 背景URL
     * @return 是否更新成功
     */
    boolean updateUserBackground(Long userId, String backgroundUrl);
}