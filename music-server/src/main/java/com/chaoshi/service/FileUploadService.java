package com.chaoshi.service;

import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

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

    /**
     * 上传用户背景图到MinIO
     * @param file 背景文件
     * @param userId 用户ID
     * @return MinIO访问URL
     * @throws IOException 文件读取异常
     * @throws MinioException MinIO操作异常
     */
    String uploadUserBackground(MultipartFile file, Long userId) throws IOException, MinioException;

    /**
     * 从MinIO下载文件
     * @param bucketName 桶名
     * @param objectName 对象名（文件路径）
     * @return 文件输入流
     * @throws IOException 文件读取异常
     * @throws MinioException MinIO操作异常
     */
    InputStream downloadFile(String bucketName, String objectName) throws IOException, MinioException;
}