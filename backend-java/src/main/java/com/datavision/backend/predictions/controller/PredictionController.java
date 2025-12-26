package com.datavision.backend.predictions.controller;

import com.datavision.backend.auth.AuthUtilsService;
import com.datavision.backend.common.response.ApiResponse;
import com.datavision.backend.predictions.dto.requests.CreateModelRequest;
import com.datavision.backend.predictions.dto.requests.CreatePipelineRequest;
import com.datavision.backend.predictions.dto.requests.PredictRequest;
import com.datavision.backend.predictions.dto.responses.ModelInfoResponse;
import com.datavision.backend.predictions.dto.responses.PipelineInfoResponse;
import com.datavision.backend.predictions.dto.responses.PredictionResponse;
import com.datavision.backend.predictions.service.IPredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/predictions")
public class PredictionController {
    private final IPredictionService predictionService;
    private final AuthUtilsService authUtils;


    @PostMapping("/{projectId}/create-model")
    public ResponseEntity<ApiResponse> createModel(@PathVariable("projectId") Long projectId,
                                                   @RequestBody CreateModelRequest request) {
        log.info("Creating model for project: {}", projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Model created successfully: "
                + predictionService.createModel(projectId, request, authUtils.getAuthenticatedUser())));
    }

    @PostMapping("/{projectId}/create-pipeline")
    public ResponseEntity<ApiResponse> createPipeline(@PathVariable("projectId") Long projectId,
                                                      @RequestBody CreatePipelineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Pipeline created successfully: "
                + predictionService.createPipeline(projectId, request, authUtils.getAuthenticatedUser())));
    }


    @GetMapping("/{projectId}/models/{modelName}")
    public ResponseEntity<ModelInfoResponse> getModelInfo(@PathVariable("projectId") Long projectId,
                                                          @PathVariable("modelName") String modelName) {
        log.info("Getting model info for project: {}, model: {}", projectId, modelName);
        return ResponseEntity
                .ok(predictionService.getModelInfo(projectId, modelName, authUtils.getAuthenticatedUser()));
    }

    @GetMapping("/{projectId}/pipelines/{pipelineName}")
    public ResponseEntity<PipelineInfoResponse> getPipelineInfo(@PathVariable("projectId") Long projectId,
                                                                @PathVariable("pipelineName") String pipelineName) {
        return ResponseEntity.ok(predictionService.getPipelineInfo(projectId, pipelineName, authUtils.getAuthenticatedUser()));
    }

    @PostMapping("/{projectId}/models/{modelName}/predict")
    public ResponseEntity<PredictionResponse> predictWithModel(@PathVariable("projectId") Long projectId,
                                                               @PathVariable("modelName") String modelName,
                                                               @RequestBody PredictRequest request) {
        return ResponseEntity.ok(predictionService.predictWithModel(projectId, modelName, request, authUtils.getAuthenticatedUser()));
    }

    @PostMapping("/{projectId}/pipelines/{pipelineName}/predict")
    public ResponseEntity<PredictionResponse> predictWithPipeline(@PathVariable("projectId") Long projectId,
                                                                  @PathVariable("pipelineName") String pipelineName,
                                                                  @RequestBody PredictRequest request) {
        return ResponseEntity.ok(predictionService.predictWithPipeline(projectId, pipelineName, request, authUtils.getAuthenticatedUser()));
    }

    @DeleteMapping("/{projectId}/models/{modelName}")
    public ResponseEntity<ApiResponse> deleteModel(@PathVariable("projectId") Long projectId,
                                                   @PathVariable("modelName") String modelName) {
        log.info("Deleting model for project: {}, model: {}", projectId, modelName);
        predictionService.deleteModel(projectId, modelName, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(new ApiResponse("Model deleted successfully"));
    }

    @DeleteMapping("/{projectId}/pipelines/{pipelineName}")
    public ResponseEntity<ApiResponse> deletePipeline(@PathVariable("projectId") Long projectId,
                                                      @PathVariable("pipelineName") String pipelineName) {
        predictionService.deletePipeline(projectId, pipelineName, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(new ApiResponse("Pipeline deleted successfully"));
    }

    @GetMapping("/{projectId}/models")
    public ResponseEntity<List<String>> getAllModels(@PathVariable("projectId") Long projectId){
        return ResponseEntity.ok(predictionService.getAllModels(projectId, authUtils.getAuthenticatedUser()));
    }
    @GetMapping("/{projectId}/pipelines")
    public ResponseEntity<List<String>> getAllPipelines(@PathVariable("projectId") Long projectId){
        return ResponseEntity.ok(predictionService.getAllPipelines(projectId, authUtils.getAuthenticatedUser()));
    }
}
