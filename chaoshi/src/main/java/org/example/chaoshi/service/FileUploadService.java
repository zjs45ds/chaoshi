package org.example.chaoshi.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 上传文件
     * @param file 文件
     * @param fileType 文件类型
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String fileType);
    
    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 获取预签名URL
     * @param objectName 对象名称
     * @return 预签名URL
     */
    String getPresignedUrl(String objectName);
}

