package com.datavision.backend.predictions.service;

import com.datavision.backend.predictions.dto.requests.CreateModelRequest;
import com.datavision.backend.predictions.dto.requests.CreatePipelineRequest;
import com.datavision.backend.predictions.dto.requests.PredictRequest;
import com.datavision.backend.predictions.dto.responses.ModelInfoResponse;
import com.datavision.backend.predictions.dto.responses.PipelineInfoResponse;
import com.datavision.backend.predictions.dto.responses.PredictionResponse;
import com.datavision.backend.user.model.User;

import java.util.List;

public interface IPredictionService {

    String createModel(Long projectId, CreateModelRequest request, User user); // returns model name
    PredictionResponse predictWithModel(Long projectId, String modelName, PredictRequest request, User user);
    ModelInfoResponse getModelInfo(Long projectId, String modelName, User user);
    List<String> getAllModels(Long projectId, User user);
    void deleteModel(Long projectId, String modelName, User user);

    String createPipeline(Long projectId, CreatePipelineRequest request, User user); // returns pipeline name
    PredictionResponse predictWithPipeline(Long projectId, String pipelineName, PredictRequest request, User user);
    PipelineInfoResponse getPipelineInfo(Long projectId, String pipelineName, User user);
    List<String> getAllPipelines(Long projectId, User user);
    void deletePipeline(Long projectId, String pipelineName, User user);

}
