package com.chaoshi.controller;

import com.chaoshi.service.FileUploadService;
import com.chaoshi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private UserService userService;

    /**
     * 上传用户头像
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file, 
                                                           @RequestParam("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要上传的文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "只能上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证文件大小（限制为5MB）
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "文件大小不能超过5MB");
                return ResponseEntity.badRequest().body(response);
            }

            // 上传文件
            String fileUrl = fileUploadService.uploadAvatar(file, userId);
            
            // 更新用户头像URL到数据库
            boolean updateResult = userService.updateUserAvatar(userId, fileUrl);
            if (!updateResult) {
                response.put("success", false);
                response.put("message", "更新头像信息失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            // 返回成功结果
            response.put("success", true);
            response.put("message", "文件上传成功");
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 上传用户背景图
     */
    @PostMapping("/background")
    public ResponseEntity<Map<String, Object>> uploadBackground(@RequestParam("file") MultipartFile file,
                                                                @RequestParam("userId") Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择要上传的文件");
                return ResponseEntity.badRequest().body(response);
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("success", false);
                response.put("message", "只能上传图片文件");
                return ResponseEntity.badRequest().body(response);
            }
            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "文件大小不能超过10MB");
                return ResponseEntity.badRequest().body(response);
            }

            String fileUrl = fileUploadService.uploadUserBackground(file, userId);
            userService.updateUserBackground(userId, fileUrl);

            response.put("success", true);
            response.put("message", "背景上传成功");
            response.put("url", fileUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "背景上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 下载文件
     * @param bucket 桶名
     * @param object 对象名（文件路径）
     * @return 文件流
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("bucket") String bucket,
                                               @RequestParam("object") String object) {
        try {
            // 通过 FileUploadService 获取文件流
            InputStream inputStream = fileUploadService.downloadFile(bucket, object);
            
            // 读取文件内容
            byte[] fileContent = inputStream.readAllBytes();
            inputStream.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            
            // 根据文件扩展名设置 Content-Type
            String contentType = getContentTypeByExtension(object);
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // 设置文件大小
            headers.setContentLength(fileContent.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    /**
     * 根据文件扩展名获取 Content-Type
     */
    private String getContentTypeByExtension(String fileName) {
        if (fileName == null) return "application/octet-stream";
        
        String extension = fileName.toLowerCase();
        if (extension.endsWith(".jpg") || extension.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (extension.endsWith(".png")) {
            return "image/png";
        } else if (extension.endsWith(".gif")) {
            return "image/gif";
        } else if (extension.endsWith(".webp")) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }
}