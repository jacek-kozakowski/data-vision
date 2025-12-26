package com.datavision.backend.predictions.service;

import com.datavision.backend.client.MLClient;
import com.datavision.backend.common.exceptions.ModelNotFoundException;
import com.datavision.backend.common.exceptions.PipelineNotFoundException;
import com.datavision.backend.common.exceptions.ProjectNotFoundException;
import com.datavision.backend.common.exceptions.ResponseParsingException;
import com.datavision.backend.minio.service.MinIOService;
import com.datavision.backend.predictions.dto.requests.CreateModelRequest;
import com.datavision.backend.predictions.dto.requests.CreatePipelineRequest;
import com.datavision.backend.predictions.dto.requests.PredictRequest;
import com.datavision.backend.predictions.dto.responses.ModelInfoResponse;
import com.datavision.backend.predictions.dto.responses.PipelineInfoResponse;
import com.datavision.backend.predictions.dto.responses.PredictionResponse;
import com.datavision.backend.project.dto.ProjectDto;
import com.datavision.backend.project.service.IProjectService;
import com.datavision.backend.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PredictionService implements IPredictionService {
    private final MLClient mlClient;
    private final MinIOService minIOService;
    private final IProjectService projectService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public String createModel(Long projectId, CreateModelRequest request, User user) {
        log.info("Creating model for project: {}", projectId);
        ProjectDto project = projectService.getProjectDtoById(projectId, user);
        String fileId = project.getDatasets().get(request.getDatasetName());

        if (fileId == null) {
            throw new ResponseParsingException("Dataset '" + request.getDatasetName() + "' not found in project");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = objectMapper.convertValue(request, java.util.Map.class);
        body.remove("dataset_name");
        body.put("dataset_file_id", fileId);

        body.putIfAbsent("use_scaled", request.getUseScaled());
        body.putIfAbsent("random_state", request.getRandomState());
        body.putIfAbsent("features", request.getFeatures() != null ? request.getFeatures() : List.of());

        String response = mlClient.postForAnalysis("/api/predictions/create-model", body);
        try {
            JsonNode root = objectMapper.readTree(response);
            String modelId = root.get("model_id").asText();
            projectService.addModelToProject(projectId, request.getModelName(), modelId, user);
            return modelId;
        } catch (JsonProcessingException e) {
            log.error("Error parsing response: {}", e.getMessage());
            throw new ResponseParsingException("Error parsing response");
        }
    }

    @Override
    public PredictionResponse predictWithModel(Long projectId, String modelName, PredictRequest request, User user) {
        log.info("Predicting with model {} in project {}", modelName, projectId);
        ProjectDto projectDto = projectService.getProjectDtoById(projectId, user);
        String modelId = projectDto.getModels().get(modelName);

        if (modelId == null) {
            log.warn("Model '{}' not found in project {}", modelName, projectId);
            throw new ProjectNotFoundException("Model '" + modelName + "' not found in project");
        }

        List<Map<String, Object>> rows = request.getData();
        Map<String, List<Object>> columns = new HashMap<>();
        if (!rows.isEmpty()) {
            for (String key : rows.get(0).keySet()) {
                columns.put(key, new ArrayList<>());
            }

            for (Map<String, Object> row : rows) {
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    if (columns.containsKey(entry.getKey())) {
                        columns.get(entry.getKey()).add(entry.getValue());
                    }
                }
            }
        }

        Map<String, Object> body = new java.util.HashMap<>();
        body.put("model_id", modelId);
        body.put("data", columns);
        body.put("return_probabilities", request.getReturnProbabilities());
        log.info("Sending prediction request to ML service: {}", body);

        String response = mlClient.postForAnalysis("/api/predictions/predict", body);
        log.info("Received response from ML service: {}", response);
        try {
            JsonNode root = objectMapper.readTree(response);

            if (root.has("error")) {
                String errorMessage = root.get("error").asText();
                log.error("ML Service returned error: {}", errorMessage);
                throw new ResponseParsingException("ML Service error: " + errorMessage);
            }

            PredictionResponse predictionResponse = new PredictionResponse();

            if (root.has("predictions")) {
                predictionResponse.setPredictions(objectMapper.convertValue(root.get("predictions"),
                        new TypeReference<List<Object>>() {
                        }));
            }

            if (root.has("probabilities")) {
                predictionResponse.setProbabilities(objectMapper.convertValue(root.get("probabilities"),
                        new TypeReference<List<List<Double>>>() {
                        }));
            }

            if (root.has("task_type")) {
                predictionResponse.setTaskName(root.get("task_type").asText());
            }

            if (predictionResponse.getPredictions() != null) {
                predictionResponse.setPredictionCount(predictionResponse.getPredictions().size());
            }
            log.debug("Prediction response: {}", predictionResponse);
            return predictionResponse;

        } catch (JsonProcessingException e) {
            log.error("Error parsing response: {}", e.getMessage());
            throw new ResponseParsingException("Error parsing response");
        }
    }

    @Override
    public ModelInfoResponse getModelInfo(Long projectId, String modelName, User user) {
        log.info("Fetching model info for model {} in project {}", modelName, projectId);
        ProjectDto project = projectService.getProjectDtoById(projectId, user);
        String modelId = project.getModels().get(modelName);

        if (modelId == null) {
            throw new ModelNotFoundException("Model '" + modelName + "' not found in project");
        }

        String metadataFileName = "models/" + modelId + "_metadata.json";

        try {
            String metadataJson = minIOService.downloadFileAsString(metadataFileName);
            ModelInfoResponse response = objectMapper.readValue(metadataJson, ModelInfoResponse.class);
            log.info("Model info fetched successfully");
            return response;
        } catch (JsonProcessingException e) {
            log.error("Error parsing model metadata: {}", e.getMessage());
            throw new ResponseParsingException("Error parsing model metadata");
        } catch (Exception e) {
            log.error("Error fetching model metadata from MinIO: {}", e.getMessage());
            throw new ResponseParsingException("Error fetching model metadata: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAllModels(Long projectId, User user) {
        ProjectDto projectDto = projectService.getProjectDtoById(projectId, user);
        return new ArrayList<>(projectDto.getModels().keySet());
    }

    @Override
    public void deleteModel(Long projectId, String modelName, User user) {
        log.info("Deleting model {} from project {}", modelName, projectId);
        ProjectDto projectDto = projectService.getProjectDtoById(projectId, user);
        minIOService.deleteFile("models/" + projectDto.getModels().get(modelName) + "_metadata.json");
        minIOService.deleteFile("models/" + projectDto.getModels().get(modelName) + ".pkl");
        projectService.removeModelFromProject(projectId, modelName, user);
        log.debug("Model {} deleted successfully", modelName);
    }

    @Override
    @Transactional
    public String createPipeline(Long projectId, CreatePipelineRequest request, User user) {
        log.info("Creating pipeline for project: {}", projectId);
        ProjectDto projectDto = projectService.getProjectDtoById(projectId, user);
        Map<String, Object> body = objectMapper.convertValue(request, java.util.Map.class);
        body.remove("dataset_name");
        body.putIfAbsent("random_state", request.getRandomState());
        body.put("dataset_file_id", projectDto.getDatasets().get(request.getDatasetName()));
        String response = mlClient.postForAnalysis("/api/predictions/create-pipeline", body);
        try {
            JsonNode root = objectMapper.readTree(response);
            String pipelineId = root.get("pipeline_id").asText();
            projectService.addPipelineToProject(projectId, request.getPipelineName(), pipelineId, user);
            return pipelineId;
        } catch (JsonProcessingException e) {
            log.error("Error parsing response: {}", e.getMessage());
            throw new ResponseParsingException("Error parsing response");
        }
    }

    @Override
    public PredictionResponse predictWithPipeline(Long projectId, String pipelineName, PredictRequest request,
            User user) {
        log.info("Predicting with pipeline {} in project {}", pipelineName, projectId);
        ProjectDto projectDto = projectService.getProjectDtoById(projectId, user);
        String pipelineId = projectDto.getPipelines().get(pipelineName);
        if (pipelineId == null || pipelineId.isEmpty()) {
            throw new PipelineNotFoundException("Pipeline '" + pipelineName + "' not found in project");
        }
        List<Map<String, Object>> rows = request.getData();
        Map<String, List<Object>> columns = new HashMap<>();
        if (!rows.isEmpty()) {
            for (String key : rows.get(0).keySet()) {
                columns.put(key, new ArrayList<>());
            }

            for (Map<String, Object> row : rows) {
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    if (columns.containsKey(entry.getKey())) {
                        columns.get(entry.getKey()).add(entry.getValue());
                    }
                }
            }
        }
        Map<String, Object> body = new HashMap<>();
        body.put("model_id", pipelineId);
        body.put("data", columns);
        body.put("return_probabilities", request.getReturnProbabilities());
        String response = mlClient.postForAnalysis("/api/predictions/predict-pipeline", body);
        try {
            JsonNode root = objectMapper.readTree(response);

            if (root.has("error")) {
                String errorMessage = root.get("error").asText();
                log.error("ML Service returned error: {}", errorMessage);
                throw new ResponseParsingException("ML Service error: " + errorMessage);
            }

            PredictionResponse predictionResponse = new PredictionResponse();

            if (root.has("predictions")) {
                predictionResponse.setPredictions(objectMapper.convertValue(root.get("predictions"),
                        new TypeReference<List<Object>>() {
                        }));
            }

            if (root.has("probabilities")) {
                predictionResponse.setProbabilities(objectMapper.convertValue(root.get("probabilities"),
                        new TypeReference<List<List<Double>>>() {
                        }));
            }

            if (root.has("task_type")) {
                predictionResponse.setTaskName(root.get("task_type").asText());
            }

            if (predictionResponse.getPredictions() != null) {
                predictionResponse.setPredictionCount(predictionResponse.getPredictions().size());
            }
            return predictionResponse;
        } catch (JsonProcessingException e) {
            log.error("Error parsing response: {}", e.getMessage());
            throw new ResponseParsingException("Error parsing response");
        }

    }

    @Override
    public PipelineInfoResponse getPipelineInfo(Long projectId, String pipelineName, User user) {
        log.info("Fetching pipeline info for pipeline {} in project {}", pipelineName, projectId);
        ProjectDto projectDto = projectService.getProjectDtoById(projectId, user);
        String pipelineId = projectDto.getPipelines().get(pipelineName);
        if (pipelineId == null) {
            throw new PipelineNotFoundException("Pipeline '" + pipelineName + "' not found in project");
        }
        try {
            String metadata = minIOService.downloadFileAsString("pipelines/" + pipelineId + "_metadata.json");
            PipelineInfoResponse response = objectMapper.readValue(metadata, PipelineInfoResponse.class);
            return response;
        } catch (Exception e) {
            log.error("Error fetching pipeline metadata: {}", e.getMessage());
            throw new ResponseParsingException("Error fetching pipeline metadata");
        }
    }

    @Override
    public List<String> getAllPipelines(Long projectId, User user) {
        ProjectDto projectDto = projectService.getProjectDtoById(projectId, user);
        return new ArrayList<>(projectDto.getPipelines().keySet());
    }

    @Override
    public void deletePipeline(Long projectId, String pipelineName, User user) {
        log.info("Deleting pipeline {} from project {}", pipelineName, projectId);
        minIOService.deleteFile("pipelines/" + pipelineName + ".pkl");
        minIOService.deleteFile("pipelines/" + pipelineName + "_metadata.json");
        projectService.removePipelineFromProject(projectId, pipelineName, user);
        log.debug("Pipeline {} deleted successfully", pipelineName);
    }
}
