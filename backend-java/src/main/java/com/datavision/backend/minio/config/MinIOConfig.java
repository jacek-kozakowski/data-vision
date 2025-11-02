package com.datavision.backend.minio.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {
    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String minioAccessKey;
    @Value("${minio.secretKey}")
    private String minioSecretKey;
    @Value("${minio.bucket}")
    private String minioBucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(minioUrl).credentials(minioAccessKey, minioSecretKey).build();
    }

}
