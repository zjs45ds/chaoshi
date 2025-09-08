package org.example.chaoshi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.chaoshi.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 本地文件上传服务实现类
 * 用于替代MinIO，将文件保存到本地目录
 */
@Service
@Slf4j
public class LocalFileUploadServiceImpl implements FileUploadService {
    
    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    @Override
    public String uploadFile(MultipartFile file, String fileType) {
        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath, fileType);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath.toFile());
            
            // 返回访问URL
            String fileUrl = "http://localhost:" + serverPort + "/uploads/" + fileType + "/" + fileName;
            log.info("文件上传成功: {}", fileUrl);
            
            return fileUrl;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) {
                return true;
            }
            
            // 从URL中提取文件路径
            String filePath = extractFilePathFromUrl(fileUrl);
            if (filePath != null) {
                Path path = Paths.get(uploadPath, filePath);
                if (Files.exists(path)) {
                    Files.delete(path);
                    log.info("文件删除成功: {}", fileUrl);
                    return true;
                }
            }
            
            log.warn("文件不存在或无法删除: {}", fileUrl);
            return false;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", fileUrl, e);
            return false;
        }
    }
    
    @Override
    public String getPresignedUrl(String objectName) {
        // 对于本地文件，直接返回访问URL
        return "http://localhost:" + serverPort + "/uploads/" + objectName;
    }
    
    /**
     * 从URL中提取文件路径
     */
    private String extractFilePathFromUrl(String fileUrl) {
        try {
            String prefix = "/uploads/";
            int index = fileUrl.indexOf(prefix);
            if (index != -1) {
                return fileUrl.substring(index + prefix.length());
            }
            return null;
        } catch (Exception e) {
            log.error("提取文件路径失败", e);
            return null;
        }
    }
}
