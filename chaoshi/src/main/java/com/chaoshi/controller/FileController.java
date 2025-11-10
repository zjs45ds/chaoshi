package com.chaoshi.controller;

import com.chaoshi.service.FileUploadService;
import com.chaoshi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            response.put("success", false);
            response.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系统错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}