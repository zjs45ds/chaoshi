package org.example.chaoshi.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    // MinIO服务器地址
    private final String endpoint = "http://localhost:9000";
    // MinIO用户名
    private final String accessKey = "zhang";
    // MinIO密码
    private final String secretKey = "12345678";
    
    // 存储桶名称 - 头像存储桶
    public static final String AVATAR_BUCKET = "chaoshi";

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}