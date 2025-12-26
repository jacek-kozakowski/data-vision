package com.datavision.backend.predictions.dto.responses;

import com.datavision.backend.predictions.dto.requests.CreatePipelineRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PipelineInfoResponse {
    private String pipeline;
    private List<CreatePipelineRequest.PipelineStepRequest> steps;
    private String target;
    private List<String> features;
    private Float trainSize;
    private Integer randomState;
    private Map<String, Object> trainMetrics;
    private Double trainingTime;
    private Map<String, Object> testMetrics;
    private String createdAt;
    private String updatedAt;
    private Integer trainingDataSize;
    private Integer testDataSize;
}
