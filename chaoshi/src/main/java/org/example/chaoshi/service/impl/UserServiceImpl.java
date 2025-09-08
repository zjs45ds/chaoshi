package org.example.chaoshi.service.impl;

import cn.hutool.core.util.StrUtil;
import org.example.chaoshi.util.PasswordEncryptUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.dto.request.LoginRequest;
import org.example.chaoshi.dto.request.RegisterRequest;
import org.example.chaoshi.dto.response.LoginResponse;
import org.example.chaoshi.dto.response.UserResponse;
import org.example.chaoshi.entity.User;
import org.example.chaoshi.mapper.UserMapper;
import org.example.chaoshi.service.FileUploadService;
import org.example.chaoshi.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final FileUploadService fileUploadService;
    
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
            loginResponse.setNickname(user.getNickname());
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
    public String uploadAvatar(Long userId, MultipartFile file) {
        try {
            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 验证文件
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("请选择要上传的头像文件");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("只支持图片格式的头像文件");
            }
            
            // 验证文件大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("头像文件大小不能超过5MB");
            }
            
            // 删除旧头像（如果存在）
            if (user.getAvatar() != null && !user.getAvatar().trim().isEmpty()) {
                try {
                    fileUploadService.deleteFile(user.getAvatar());
                } catch (Exception e) {
                    log.warn("删除旧头像失败: {}", user.getAvatar(), e);
                }
            }
            
            // 上传新头像到MinIO
            String avatarUrl = fileUploadService.uploadFile(file, "image");
            
            // 更新用户头像信息
            user.setAvatar(avatarUrl);
            user.setUpdatedAt(LocalDateTime.now());
            
            int updateResult = userMapper.updateUser(user);
            if (updateResult <= 0) {
                throw new RuntimeException("更新用户头像信息失败");
            }
            
            log.info("用户头像上传成功: userId={}, avatarUrl={}", userId, avatarUrl);
            return avatarUrl;
            
        } catch (Exception e) {
            log.error("用户头像上传失败: userId={}", userId, e);
            throw new RuntimeException("头像上传失败: " + e.getMessage());
        }
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