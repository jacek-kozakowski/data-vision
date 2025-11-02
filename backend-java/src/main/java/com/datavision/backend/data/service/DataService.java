package com.datavision.backend.data.service;

import com.datavision.backend.client.MLClient;
import com.datavision.backend.minio.service.MinIOService;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.service.UserService;
import io.jsonwebtoken.lang.Maps;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {
    private final MLClient mlClient;
    private final UserService userService;
    private final MinIOService minIOService;

    public boolean uploadData(MultipartFile file, User user){
        log.info("Uploading data for user: {}", user.getUsername());
        String fileName = minIOService.uploadFile(file);
        return userService.setCurrentFile(user.getId(), fileName);
    }

    public String analyze_data(User user){
        Map<String, String> body = Map.of(
                "file_id", user.getCurrentFile()
        );
        return mlClient.postForAnalysis("/api/data/analyze", body);
    }
    public String correlation_data(String target, User user){
        Map<String, String> body = Map.of(
                "file_id", user.getCurrentFile(),
                "target", target
        );

        return mlClient.postForAnalysis("/api/data/correlation", body );
    }
    public byte[] plot_data(String plotType, String column1, String column2, User user){
        Map<String, String> body = Map.of(
                "file_id", user.getCurrentFile(),
                "plot_type", plotType,
                "column1", column1,
                "column2", column2
        );
        return mlClient.postForImage("/api/data/plot", body);
    }
}
