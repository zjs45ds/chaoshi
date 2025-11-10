package com.chaoshi.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.config.MinioConfig;
import com.chaoshi.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileUploadServiceImpl implements FileUploadService {

    private final MinioClient minioClient;

    // 存储桶名称 - 使用配置类中的常量
    private static final String BUCKET_NAME = MinioConfig.AVATAR_BUCKET;
    // MinIO访问地址
    private static final String MINIO_URL = "http://localhost:9000";
    
    private static final String USER_AVATAR_DIR = "User/";

    @Override
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException, MinioException {
        log.info("[FILE UPLOAD] 开始上传用户头像: userId={}", userId);
        
        try {
            // 检查存储桶是否存在，如果不存在则创建
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
            if (!bucketExists) {
                log.info("[FILE UPLOAD] 存储桶不存在，创建存储桶: {}", BUCKET_NAME);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            // 生成文件名 - 仅使用原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                originalFilename = "avatar.jpg";
            }
            
            // 提取文件名和扩展名
            String baseName;
            String fileExtension;
            int dotIndex = originalFilename.lastIndexOf('.');
            
            if (dotIndex > 0) {
                // 提取基本文件名
                baseName = originalFilename.substring(0, dotIndex);
                // 提取扩展名（包含点号）
                fileExtension = originalFilename.substring(dotIndex);
                
                // 验证扩展名是否有效
                Set<String> validExtensions = new HashSet<>(Arrays.asList(
                    ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg"
                ));
                
                // 如果扩展名无效，使用Content-Type尝试推断或使用默认值
                if (!validExtensions.contains(fileExtension.toLowerCase())) {
                    String contentType = file.getContentType();
                    if (contentType != null) {
                        if (contentType.contains("image/jpeg")) {
                            fileExtension = ".jpg";
                        } else if (contentType.contains("image/png")) {
                            fileExtension = ".png";
                        } else if (contentType.contains("image/gif")) {
                            fileExtension = ".gif";
                        } else if (contentType.contains("image/webp")) {
                            fileExtension = ".webp";
                        } else if (contentType.contains("image/svg")) {
                            fileExtension = ".svg";
                        } else {
                            fileExtension = ".jpg"; // 默认扩展名
                        }
                    } else {
                        fileExtension = ".jpg"; // 默认扩展名
                    }
                    log.warn("[FILE UPLOAD] 使用了无效扩展名 {}, 已根据Content-Type调整为 {}", 
                             originalFilename.substring(dotIndex), fileExtension);
                }
            } else {
                // 没有扩展名的情况
                baseName = originalFilename;
                // 尝试根据Content-Type推断扩展名
                String contentType = file.getContentType();
                if (contentType != null) {
                    if (contentType.contains("image/jpeg")) {
                        fileExtension = ".jpg";
                    } else if (contentType.contains("image/png")) {
                        fileExtension = ".png";
                    } else if (contentType.contains("image/gif")) {
                        fileExtension = ".gif";
                    } else if (contentType.contains("image/webp")) {
                        fileExtension = ".webp";
                    } else if (contentType.contains("image/svg")) {
                        fileExtension = ".svg";
                    } else {
                        fileExtension = ".jpg"; // 默认扩展名
                    }
                } else {
                    fileExtension = ".jpg"; // 默认扩展名
                }
            }
            
            // 直接使用原始文件名，不添加额外字符
            String uniqueFilename = baseName + fileExtension;
            
            // 完整的对象路径 - 存储在User目录下
            String objectName = USER_AVATAR_DIR + uniqueFilename;

            // 获取文件输入流
            try (InputStream inputStream = file.getInputStream()) {
                // 上传文件到MinIO
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            }

            String fileUrl = MINIO_URL + "/" + BUCKET_NAME + "/" + objectName;
            log.info("[FILE UPLOAD] 文件上传成功，URL: {}", fileUrl);
            
            return fileUrl;
        } catch (MinioException e) {
            log.error("[FILE UPLOAD] MinIO上传异常: {}", e.getMessage(), e);
            throw e;
        } catch (IOException e) {
            log.error("[FILE UPLOAD] 文件读取异常: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[FILE UPLOAD] 上传过程中发生未知异常: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
}