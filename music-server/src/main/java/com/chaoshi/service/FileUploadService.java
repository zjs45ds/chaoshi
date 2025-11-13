package com.chaoshi.service;

import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 上传用户头像到MinIO
     * @param file 头像文件
     * @param userId 用户ID
     * @return 上传后的文件URL
     * @throws IOException 文件读取异常
     * @throws MinioException MinIO操作异常
     */
    String uploadAvatar(MultipartFile file, Long userId) throws IOException, MinioException;
}