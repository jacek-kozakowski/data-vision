package com.datavision.backend.data.service;

import com.datavision.backend.client.MLClient;
import com.datavision.backend.common.dto.data.CleanScaleDataDto;
import com.datavision.backend.common.dto.project.ProjectDto;
import com.datavision.backend.minio.service.MinIOService;
import com.datavision.backend.project.service.ProjectService;
import com.datavision.backend.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {
    private final MLClient mlClient;
    private final MinIOService minIOService;
    private final ProjectService projectService;

    private static final String DATASET_NAME = "original_dataset";
    private static final String SCALED_DATASET_NAME = "scaled_dataset";

    public void uploadData(Long projectId, MultipartFile file, User user){
        log.info("Uploading data for user: {}", user.getUsername());
        String fileName = minIOService.uploadFile(file);
        projectService.addDatasetToProject(projectId, DATASET_NAME, fileName, user);
    }

    public String analyze_data(Long projectId, User user, boolean useScaled){
        String dataset = getDataset(projectId, user, useScaled);
        Map<String, String> body = Map.of(
                "file_id", dataset
        );
        return mlClient.postForAnalysis("/api/data/analyze", body);
    }
    public String correlation_data(Long projectId, String target, User user, boolean useScaled){
        String dataset = getDataset(projectId, user, useScaled);
        Map<String, String> body = Map.of(
                "file_id", dataset,
                "target", target
        );

        return mlClient.postForAnalysis("/api/data/correlation", body );
    }
    public byte[] plot_data(Long projectId, String plotType, String column1, String column2, User user, boolean useScaled){
        String dataset = getDataset(projectId, user, useScaled);
        Map<String, String> body = Map.of(
                "file_id", dataset,
                "plot_type", plotType,
                "column1", column1,
                "column2", column2
        );
        return mlClient.postForImage("/api/data/plot", body);
    }

    public String clean_scale_data(Long projectId, boolean fillNa, String fillMethod, boolean scale, User user){
        log.info("Cleaning data for user: {}", user.getUsername());
        String dataset = getDataset(projectId, user);
        CleanScaleDataDto body = new CleanScaleDataDto(dataset, fillNa, fillMethod, scale);
        String response =  mlClient.postForAnalysis("/api/data/clean", body);
        try{
            JsonNode root = new ObjectMapper().readTree(response);
            String scaledDataset = root.get("new_file_id").asText();

            projectService.addDatasetToProject(projectId, SCALED_DATASET_NAME, scaledDataset, user);
        }catch (JsonProcessingException e ){
            log.error("Error parsing response: {}", e.getMessage());
        }
        return response;
    }


    private String getDataset(Long projectId, User user){
        ProjectDto dto = projectService.getProjectDtoById(projectId, user);
        return dto.getDatasets().get(DATASET_NAME);
    }

    private String getDataset(Long projectId, User user, boolean useScaled){
        String datasetName = useScaled ? SCALED_DATASET_NAME : DATASET_NAME;
        ProjectDto dto = projectService.getProjectDtoById(projectId, user);
        return dto.getDatasets().get(datasetName);
    }
}
