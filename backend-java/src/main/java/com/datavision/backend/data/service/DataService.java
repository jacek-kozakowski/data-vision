package com.datavision.backend.data.service;

import com.datavision.backend.client.MLClient;
import com.datavision.backend.common.exceptions.DatasetNotFoundException;
import com.datavision.backend.data.dto.AnalyzeDataDto;
import com.datavision.backend.data.dto.CleanScaleDataDto;
import com.datavision.backend.data.dto.CorrelationDataDto;
import com.datavision.backend.data.dto.PlotDataDto;
import com.datavision.backend.data.dto.requests.CleanDataRequest;
import com.datavision.backend.data.dto.requests.PlotDataRequest;
import com.datavision.backend.project.dto.ProjectDto;
import com.datavision.backend.common.exceptions.ResponseParsingException;
import com.datavision.backend.minio.service.MinIOService;
import com.datavision.backend.project.service.IProjectService;
import com.datavision.backend.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataService {
    private final MLClient mlClient;
    private final MinIOService minIOService;
    private final IProjectService projectService;

    private static final String DATASET_NAME = "original_dataset";
    private static final String SCALED_DATASET_NAME = "scaled_dataset";

    @Transactional
    public void uploadData(Long projectId, MultipartFile file, User user) {
        log.info("Uploading data to project: {}", projectId);
        String fileName = minIOService.uploadFile(file);
        projectService.addDatasetToProject(projectId, DATASET_NAME, fileName, user);
    }

    public String analyzeData(Long projectId, User user, boolean useScaled) {
        log.info("Analyzing data, project id: {}", projectId);
        String dataset = getDataset(projectId, user, useScaled);
        AnalyzeDataDto body = new AnalyzeDataDto(dataset);
        return mlClient.postForAnalysis("/api/data/analyze", body);
    }

    public String correlationData(Long projectId, String target, User user, boolean useScaled) {
        log.info("Calculating correlation for target: {}, project id: {}", target, projectId);
        String dataset = getDataset(projectId, user, useScaled);
        CorrelationDataDto body = new CorrelationDataDto(dataset, target);
        return mlClient.postForAnalysis("/api/data/correlation", body);
    }

    public byte[] plotData(Long projectId, PlotDataRequest request, User user) {
        log.info("Plotting data for project: {}", projectId);
        String dataset = getDataset(projectId, user, request.getUseScaled());
        PlotDataDto body = new PlotDataDto(dataset, request.getPlotType(), request.getColumn1(), request.getColumn2());
        String response = mlClient.postForAnalysis("/api/data/plot", body);
        String minioPath = null;
        try {
            JsonNode node = new ObjectMapper().readTree(response);
            minioPath = node.get("plot_file_id").asText();
            projectService.addPlotToProject(projectId, request.getPlotId(), minioPath, user);
        } catch (JsonProcessingException e) {
            log.error("Error parsing response: {}", e.getMessage());
        }
        if (minioPath == null) {
            log.error("Minio path is null");
            throw new ResponseParsingException("Error parsing response");
        }

        return minIOService.downloadFile(minioPath);
    }

    public String cleanScaleData(Long projectId, CleanDataRequest request, User user) {
        log.info("Cleaning data for user: {}", user.getUsername());
        String dataset = getDataset(projectId, user);
        CleanScaleDataDto body = new CleanScaleDataDto(dataset, request.getFillNa(), request.getFillMethod(),
                request.getScale());
        String response = mlClient.postForAnalysis("/api/data/clean", body);
        try {
            JsonNode root = new ObjectMapper().readTree(response);
            String scaledDataset = root.get("cleaned_file_id").asText();
            projectService.addDatasetToProject(projectId, SCALED_DATASET_NAME, scaledDataset, user);
        } catch (JsonProcessingException e) {
            log.error("Error parsing response: {}", e.getMessage());
        }
        return response;
    }

    private String getDataset(Long projectId, User user) {
        ProjectDto dto = projectService.getProjectDtoById(projectId, user);
        return dto.getDatasets().get(DATASET_NAME);
    }

    private String getDataset(Long projectId, User user, boolean useScaled) {
        String datasetName = useScaled ? SCALED_DATASET_NAME : DATASET_NAME;
        ProjectDto dto = projectService.getProjectDtoById(projectId, user);
        String dataset = dto.getDatasets().get(datasetName);
        if (dataset == null) {
            log.warn("Dataset {} not found for project {}", datasetName, projectId);
            throw new DatasetNotFoundException("Dataset " + datasetName + " not found in project " + projectId);
        }
        return dataset;
    }
}
