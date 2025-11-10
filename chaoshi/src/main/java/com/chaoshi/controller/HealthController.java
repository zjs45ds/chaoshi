package com.chaoshi.controller;

import com.chaoshi.dto.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public ApiResult<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        healthInfo.put("service", "chaoshi-music");
        healthInfo.put("version", "1.0.0");
        
        return ApiResult.success("服务正常", healthInfo);
    }
    
    @GetMapping("/ping")
    public ApiResult<String> ping() {
        return ApiResult.success("pong");
    }
}