package com.datavision.backend.predictions.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CreatePipelineRequest {

    @NotBlank(message = "Pipeline name cannot be empty")
    @JsonProperty("pipeline_name")
    private String pipelineName;

    @NotBlank(message = "Dataset name cannot be empty")
    @JsonProperty("dataset_name")
    private String datasetName;

    @NotBlank(message = "Target column cannot be empty")
    private String target;

    private List<String> features;

    @NotNull(message = "Pipeline steps cannot be null")
    private List<PipelineStepRequest> steps;

    @NotNull
    @DecimalMin(value = "0.1", message = "Train size must be at least 0.1")
    @DecimalMax(value = "0.9", message = "Train size must be at most 0.9")
    @JsonProperty("train_size")
    private Float trainSize;

    @JsonProperty("random_state")
    private Integer randomState = 42;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PipelineStepRequest {
        @NotBlank
        private String name;

        @NotBlank
        private String type;

        private Map<String, Object> params;
    }
}
