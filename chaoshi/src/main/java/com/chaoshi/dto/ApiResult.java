package com.chaoshi.dto;

import lombok.Data;

/**
 * 通用API响应结果类
 */
@Data
public class ApiResult<T> {
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    public ApiResult() {}
    
    public ApiResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "成功", data);
    }
    
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data);
    }
    
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "成功", null);
    }
    
    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(500, message, null);
    }
    
    public static <T> ApiResult<T> error(Integer code, String message) {
        return new ApiResult<>(code, message, null);
    }
    
    public static <T> ApiResult<T> error(Integer code, String message, T data) {
        return new ApiResult<>(code, message, data);
    }
}