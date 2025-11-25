package com.datavision.backend.minio.service;

import com.datavision.backend.common.exceptions.IllegalExtensionException;
import com.datavision.backend.common.exceptions.MinioDownloadException;
import com.datavision.backend.common.exceptions.MinioUploadException;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinIOService {
    private final MinioClient minioClient;
    private static final List<String> ALLOWED_FILE_TYPES = List.of(".csv", ".xlsx");

    @Getter
    @Value("${minio.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile file){
        try{
            String fileName = file.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            if (fileExtension.isEmpty() || !ALLOWED_FILE_TYPES.contains(fileExtension)) {
                throw new IllegalExtensionException(fileExtension + " is not allowed.");
            }

            String newFileName = UUID.randomUUID().toString() + fileExtension;

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info("File uploaded successfully to MinIO");
            return newFileName;
        }catch (Exception e){
            log.error("Failed to upload file to MinIO", e);
            throw new MinioUploadException("Failed to upload file to MinIO");
        }
    }

    public byte[] downloadFile(String fileName){
        if (fileName == null || fileName.isEmpty()){
            throw new IllegalArgumentException("File name cannot be empty");
        }
        try{
            GetObjectResponse response= minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build()
            );
            return response.readAllBytes();
        }catch (Exception e){
            log.error("Failed to download file from MinIO", e);
            throw new MinioDownloadException("Failed to download file from MinIO");
        }
    }

}
