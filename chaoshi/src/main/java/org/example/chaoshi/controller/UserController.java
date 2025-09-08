package org.example.chaoshi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.chaoshi.dto.ApiResult;
import org.example.chaoshi.dto.request.LoginRequest;
import org.example.chaoshi.dto.request.RegisterRequest;
import org.example.chaoshi.dto.response.LoginResponse;
import org.example.chaoshi.dto.response.UserResponse;
import org.example.chaoshi.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户Controller
 */
@Tag(name = "用户管理", description = "用户注册、登录、信息管理相关接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:8081", "http://127.0.0.1:8081"}, allowCredentials = "true")
public class UserController {
    
    private final UserService userService;
    
    @Operation(summary = "用户注册", description = "用户注册接口")
    @PostMapping("/register")
    public ApiResult<UserResponse> register(@RequestBody RegisterRequest registerRequest) {
        try {
            UserResponse userResponse = userService.register(registerRequest);
            return ApiResult.success("🎉 注册成功！欢迎加入潮石音乐", userResponse);
        } catch (Exception e) {
            // 根据异常类型返回友好的错误消息
            String errorMessage = getRegisterErrorMessage(e.getMessage(), registerRequest.getUsername());
            return ApiResult.error(errorMessage);
        }
    }
    
    @Operation(summary = "用户登录", description = "用户登录接口")
    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("[LOGIN] 收到登录请求: " + loginRequest.getUsername());
            System.out.println("[LOGIN] 请求数据: " + loginRequest);
            LoginResponse loginResponse = userService.login(loginRequest);
            System.out.println("[LOGIN] 登录成功: " + loginResponse.getUsername());
            
            // 返回成功消息，包含用户昵称
            String successMessage = String.format("🎉 欢迎回来，%s！登录成功", 
                loginResponse.getNickname() != null ? loginResponse.getNickname() : loginResponse.getUsername());
            return ApiResult.success(successMessage, loginResponse);
        } catch (Exception e) {
            System.out.println("[LOGIN] 登录失败: " + e.getMessage());
            e.printStackTrace();
            
            // 根据异常类型返回友好的错误消息
            String errorMessage = getLoginErrorMessage(e.getMessage(), loginRequest.getUsername());
            return ApiResult.error(errorMessage);
        }
    }
    
    @Operation(summary = "获取用户信息", description = "获取当前用户信息")
    @GetMapping("/info")
    public ApiResult<UserResponse> getUserInfo(@RequestParam Long userId) {
        try {
            UserResponse userResponse = userService.getUserInfo(userId);
            return ApiResult.success(userResponse);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "上传头像", description = "用户头像上传接口")
    @PostMapping("/upload-avatar")
    public ApiResult<String> uploadAvatar(@RequestParam Long userId,
                                          @RequestParam("avatar") MultipartFile file) {
        try {
            String avatarUrl = userService.uploadAvatar(userId, file);
            return ApiResult.success("上传成功", avatarUrl);
        } catch (Exception e) {
            return ApiResult.error(e.getMessage());
        }
    }
    
    @Operation(summary = "修改密码", description = "用户修改密码接口")
    @PostMapping("/change-password")
    public ApiResult<Boolean> changePassword(@RequestParam Long userId,
                                          @RequestParam String oldPassword,
                                          @RequestParam String newPassword) {
        try {
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                return ApiResult.success("🔐 密码修改成功", true);
            } else {
                return ApiResult.error("❌ 密码修改失败");
            }
        } catch (Exception e) {
            return ApiResult.error("❌ " + e.getMessage());
        }
    }
    
    /**
     * 根据登录异常信息生成用户友好的错误消息
     */
    private String getLoginErrorMessage(String exceptionMessage, String username) {
        if (exceptionMessage == null) {
            return "❌ 登录失败，请稍后重试";
        }
        
        // 根据不同的错误类型返回相应的提示
        if (exceptionMessage.contains("用户不存在")) {
            return String.format("❌ 用户名 '%s' 不存在，请检查用户名是否正确", username);
        } else if (exceptionMessage.contains("密码错误")) {
            return "❌ 密码错误，请检查密码是否正确";
        } else if (exceptionMessage.contains("账户被锁定")) {
            return "🔒 账户已被锁定，请联系管理员";
        } else if (exceptionMessage.contains("账户已禁用")) {
            return "⛔ 账户已被禁用，请联系管理员";
        } else if (exceptionMessage.contains("网络") || exceptionMessage.contains("连接")) {
            return "🌐 网络连接异常，请检查网络后重试";
        } else if (exceptionMessage.contains("数据库")) {
            return "💾 系统繁忙，请稍后重试";
        } else {
            return "❌ 登录失败：" + exceptionMessage;
        }
    }
    
    /**
     * 根据注册异常信息生成用户友好的错误消息
     */
    private String getRegisterErrorMessage(String exceptionMessage, String username) {
        if (exceptionMessage == null) {
            return "❌ 注册失败，请稍后重试";
        }
        
        // 根据不同的错误类型返回相应的提示
        if (exceptionMessage.contains("用户名已存在") || exceptionMessage.contains("重复")) {
            return String.format("👤 用户名 '%s' 已被使用，请选择其他用户名", username);
        } else if (exceptionMessage.contains("邮箱已存在")) {
            return "📧 该邮箱已被注册，请使用其他邮箱或尝试登录";
        } else if (exceptionMessage.contains("手机号已存在")) {
            return "📱 该手机号已被注册，请使用其他手机号或尝试登录";
        } else if (exceptionMessage.contains("密码") && exceptionMessage.contains("强度")) {
            return "🔐 密码强度不够，请使用至少6位包含字母和数字的密码";
        } else if (exceptionMessage.contains("邮箱格式")) {
            return "📧 邮箱格式不正确，请输入有效的邮箱地址";
        } else if (exceptionMessage.contains("手机号格式")) {
            return "📱 手机号格式不正确，请输入有效的手机号";
        } else {
            return "❌ 注册失败：" + exceptionMessage;
        }
    }
}