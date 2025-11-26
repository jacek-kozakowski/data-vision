package com.datavision.backend.predictions.service;

import com.datavision.backend.auth.AuthUtilsService;
import com.datavision.backend.client.MLClient;
import com.datavision.backend.minio.service.MinIOService;
import com.datavision.backend.predictions.dto.requests.CreateModelRequest;
import com.datavision.backend.predictions.dto.requests.CreatePipelineRequest;
import com.datavision.backend.predictions.dto.requests.PredictRequest;
import com.datavision.backend.predictions.dto.responses.ModelInfoResponse;
import com.datavision.backend.predictions.dto.responses.PipelineInfoResponse;
import com.datavision.backend.predictions.dto.responses.PredictionResponse;
import com.datavision.backend.project.service.IProjectService;
import com.datavision.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredictionService implements IPredictionService{
    private final MLClient mlClient;
    private final MinIOService minIOService;
    private final IProjectService projectService;


    @Override
    public String createModel(Long projectId, CreateModelRequest request, User user) {
        return "";
    }

    @Override
    public PredictionResponse predictWithModel(Long projectId, String modelName, PredictRequest request, User user) {
        return null;
    }

    @Override
    public byte[] predictWithCsvFile(Long projectId, String modelName, MultipartFile file, boolean returnProbabilities, User user) {
        return new byte[0];
    }

    @Override
    public ModelInfoResponse getModelInfo(Long projectId, String modelName, User user) {
        return null;
    }

    @Override
    public List<String> getAllModels(Long projectId, User user) {
        return List.of();
    }

    @Override
    public void deleteModel(Long projectId, String modelName, User user) {

    }

    @Override
    public String createPipeline(Long projectId, CreatePipelineRequest request, User user) {
        return "";
    }

    @Override
    public PredictionResponse predictWithPipeline(Long projectId, String pipelineName, PredictRequest request, User user) {
        return null;
    }

    @Override
    public byte[] predictWithCsvFilePipeline(Long projectId, String pipelineName, MultipartFile file, boolean returnProbabilities, User user) {
        return new byte[0];
    }


    @Override
    public PipelineInfoResponse getPipelineInfo(Long projectId, String pipelineName, User user) {
        return null;
    }

    @Override
    public List<String> getAllPipelines(Long projectId, User user) {
        return List.of();
    }

    @Override
    public void deletePipeline(Long projectId, String pipelineName, User user) {

    }
}
