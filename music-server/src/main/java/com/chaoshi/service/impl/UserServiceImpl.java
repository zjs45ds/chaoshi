package com.chaoshi.service.impl;

import cn.hutool.core.util.StrUtil;
import com.chaoshi.util.PasswordEncryptUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.dto.request.LoginRequest;
import com.chaoshi.dto.request.ProfileRequest;
import com.chaoshi.dto.request.RegisterRequest;
import com.chaoshi.dto.response.LoginResponse;
import com.chaoshi.dto.response.UserResponse;
import com.chaoshi.entity.User;
import com.chaoshi.mapper.UserMapper;
import com.chaoshi.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 用户Service实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse register(RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        User existUser = userMapper.selectByUsername(registerRequest.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (StrUtil.isNotBlank(registerRequest.getEmail())) {
            existUser = userMapper.selectByEmail(registerRequest.getEmail());
            if (existUser != null) {
                throw new RuntimeException("邮箱已存在");
            }
        }
        
        // 创建新用户
        User user = new User();
        BeanUtils.copyProperties(registerRequest, user);
        user.setPassword(PasswordEncryptUtil.encryptPassword(registerRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insertUser(user);
        
        // 返回用户信息（不包含密码）
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("[LOGIN SERVICE] 开始处理登录请求: {}", loginRequest.getUsername());
        
        try {
            // 查找用户
            User user = findByUsername(loginRequest.getUsername());
            if (user == null) {
                log.warn("[LOGIN SERVICE] 用户不存在: {}", loginRequest.getUsername());
                throw new RuntimeException("用户不存在");
            }
            
            log.info("[LOGIN SERVICE] 找到用户: {}, ID: {}", user.getUsername(), user.getId());
            
            // 验证密码
            log.info("[LOGIN SERVICE] 开始验证密码");
            log.debug("[LOGIN SERVICE] 输入密码: {}", loginRequest.getPassword());
            log.debug("[LOGIN SERVICE] 存储密码hash: {}", user.getPassword());
            
            if (!PasswordEncryptUtil.verifyPassword(loginRequest.getPassword(), user.getPassword())) {
                log.warn("[LOGIN SERVICE] 密码验证失败: {}", loginRequest.getUsername());
                throw new RuntimeException("密码错误");
            }
            
            log.info("[LOGIN SERVICE] 密码验证成功");
            
            // 生成JWT token
            String token = generateToken(user);
            log.info("[LOGIN SERVICE] 生成Token成功");
            
            // 构造登录响应
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setId(user.getId());
            loginResponse.setUsername(user.getUsername());
            loginResponse.setAvatar(user.getAvatar());
            loginResponse.setToken(token);
            loginResponse.setCreatedAt(user.getCreatedAt());
            
            log.info("[LOGIN SERVICE] 登录成功，返回响应: {}", loginResponse.getUsername());
            
            return loginResponse;
        } catch (Exception e) {
            log.error("[LOGIN SERVICE] 登录过程中发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public User findByUsername(String username) {
        log.info("[USER SERVICE] 根据用户名查找用户: {}", username);
        User user = userMapper.selectByUsername(username);
        log.info("[USER SERVICE] 查找结果: {}", user != null ? "找到用户" : "未找到用户");
        if (user != null) {
            log.debug("[USER SERVICE] 用户详情 - ID: {}, Username: {}, Email: {}", 
                     user.getId(), user.getUsername(), user.getEmail());
        }
        return user;
    }
    
    @Override
    public UserResponse getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }
    
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码
        if (!PasswordEncryptUtil.verifyPassword(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        
        // 更新密码
        user.setPassword(PasswordEncryptUtil.encryptPassword(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        return userMapper.updateUser(user) > 0;
    }
    
    @Override
    public UserResponse getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateUserProfile(Long userId, ProfileRequest profileRequest) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        if (profileRequest.getUsername() != null && !profileRequest.getUsername().trim().isEmpty()) {
            String newUsername = profileRequest.getUsername().trim();
            // 检查用户名是否与当前用户名相同
            if (!newUsername.equals(user.getUsername())) {
                // 检查新用户名是否已被其他用户使用
                User existingUser = userMapper.selectByUsername(newUsername);
                if (existingUser != null && !existingUser.getId().equals(userId)) {
                    throw new RuntimeException("用户名已被使用，请选择其他用户名");
                }
                user.setUsername(newUsername);
            }
        }
        if (profileRequest.getEmail() != null) {
            user.setEmail(profileRequest.getEmail().trim());
        }
        if (profileRequest.getPhone() != null) {
            user.setPhone(profileRequest.getPhone().trim());
        }
        if (profileRequest.getBio() != null) {
            user.setBio(profileRequest.getBio().trim());
        }
        if (profileRequest.getAvatar() != null) {
            user.setAvatar(profileRequest.getAvatar());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.updateUser(user);
        if (result <= 0) {
            throw new RuntimeException("更新个人资料失败");
        }
        
        // 返回更新后的用户信息
        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return userResponse;
    }
    
    @Override
    public boolean isUsernameAvailable(String username, Long currentUserId) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        String trimmedUsername = username.trim();
        User existingUser = userMapper.selectByUsername(trimmedUsername);
        
        // 如果没有找到用户，说明用户名可用
        if (existingUser == null) {
            return true;
        }
        
        // 如果找到了用户，但是是当前用户自己，也认为可用
        if (currentUserId != null && existingUser.getId().equals(currentUserId)) {
            return true;
        }
        
        // 否则用户名已被其他用户使用
        return false;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserAvatar(Long userId, String avatarUrl) {
        log.info("[USER SERVICE] 开始更新用户头像: userId={}", userId);
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("[USER SERVICE] 用户不存在: userId={}", userId);
            throw new RuntimeException("用户不存在");
        }
        
        // 更新头像URL
        user.setAvatar(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.updateUser(user);
        boolean success = result > 0;
        
        if (success) {
            log.info("[USER SERVICE] 用户头像更新成功: userId={}", userId);
        } else {
            log.warn("[USER SERVICE] 用户头像更新失败: userId={}", userId);
        }
        
        return success;
    }
    
    /**
     * 生成JWT token
     */
    private String generateToken(User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                .claim("userId", user.getId())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}