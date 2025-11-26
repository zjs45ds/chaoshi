package com.chaoshi.service.impl;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.chaoshi.config.MinioConfig;
import com.chaoshi.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
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
    private static final String USER_AVATAR_DIR = "User/";
    private static final Set<String> VALID_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".svg"
    ));

    @Value("${minio.endpoint:http://localhost:9000}")
    private String minioEndpoint;

    @Override
    public String uploadAvatar(MultipartFile file, Long userId) throws IOException, MinioException {
        log.info("[FILE UPLOAD] 开始上传用户头像: userId={}", userId);
        
        try {
            ensureBucketExists();

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                originalFilename = "avatar.jpg";
            }

            String fileExtension = resolveFileExtension(file, originalFilename);
            String uniqueFilename = buildTimestampFilename("avatar_", userId, fileExtension);
            String objectName = USER_AVATAR_DIR + uniqueFilename;

            uploadObject(file, objectName);

            // 生成MinIO直接访问URL
            String fileUrl = buildPublicUrl(objectName);
            log.info("[FILE UPLOAD] 生成MinIO直接访问URL: {}", fileUrl);
            
            return fileUrl;
        } catch (MinioException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            log.error("[FILE UPLOAD] 文件上传处理失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public String uploadUserBackground(MultipartFile file, Long userId) throws IOException, MinioException {
        log.info("[FILE UPLOAD] 开始上传用户背景: userId={}", userId);
        try {
            ensureBucketExists();
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                originalFilename = "background.jpg";
            }

            String fileExtension = resolveFileExtension(file, originalFilename);
            String uniqueFilename = buildTimestampFilename("background-", userId, fileExtension);
            String objectName = USER_AVATAR_DIR + uniqueFilename;

            uploadObject(file, objectName);
            String directUrl = buildPublicUrl(objectName);
            log.info("[FILE UPLOAD] 用户背景上传成功: {}", directUrl);
            return directUrl;
        } catch (MinioException | IOException e) {
            throw e;
        } catch (Exception e) {
            log.error("[FILE UPLOAD] 背景上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("背景上传失败", e);
        }
    }

    @Override
    public InputStream downloadFile(String bucketName, String objectName) throws IOException, MinioException {
        log.info("[FILE DOWNLOAD] 开始下载文件: bucket={}, object={}", bucketName, objectName);
        
        try {
            // 检查存储桶是否存在
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                log.error("[FILE DOWNLOAD] 存储桶不存在: {}", bucketName);
                throw new IOException("存储桶不存在: " + bucketName);
            }
            
            // 获取文件流
            InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            
            log.info("[FILE DOWNLOAD] 文件下载成功: bucket={}, object={}", bucketName, objectName);
            return inputStream;
            
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                log.error("[FILE DOWNLOAD] 文件不存在: bucket={}, object={}", bucketName, objectName);
                throw new IOException("文件不存在: " + objectName);
            }
            log.error("[FILE DOWNLOAD] MinIO错误: {}", e.getMessage(), e);
            throw new MinioException("MinIO下载文件失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("[FILE DOWNLOAD] 下载文件时发生异常: {}", e.getMessage(), e);
            throw new IOException("下载文件失败: " + e.getMessage());
        }
    }
    
    private void ensureBucketExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build());
        if (!bucketExists) {
            log.info("[FILE UPLOAD] 存储桶不存在，创建存储桶: {}", BUCKET_NAME);
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        }
    }

    private String resolveFileExtension(MultipartFile file, String originalFilename) {
        String extension = "";
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }
        }
        if (!VALID_EXTENSIONS.contains(extension.toLowerCase())) {
            extension = mapContentTypeToExtension(file.getContentType());
        }
        return extension;
    }

    private String mapContentTypeToExtension(String contentType) {
        if (contentType == null) {
            return ".jpg";
        }
        if (contentType.contains("image/jpeg")) {
            return ".jpg";
        }
        if (contentType.contains("image/png")) {
            return ".png";
        }
        if (contentType.contains("image/gif")) {
            return ".gif";
        }
        if (contentType.contains("image/webp")) {
            return ".webp";
        }
        if (contentType.contains("image/svg")) {
            return ".svg";
        }
        if (contentType.contains("image/bmp")) {
            return ".bmp";
        }
        return ".jpg";
    }

    private String buildTimestampFilename(String prefix, Long userId, String extension) {
        String userPart = userId != null ? String.valueOf(userId) : "guest";
        return prefix + userPart + "-" + System.currentTimeMillis() + extension;
    }

    private void uploadObject(MultipartFile file, String objectName) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        }
    }

    private String buildPublicUrl(String objectName) {
        String endpoint = (minioEndpoint == null || minioEndpoint.isBlank())
                ? "http://localhost:9000"
                : minioEndpoint;
        if (endpoint.endsWith("/")) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        return endpoint + "/" + BUCKET_NAME + "/" + objectName;
    }
}
