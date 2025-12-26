package com.datavision.backend.predictions.dto.responses;

import com.datavision.backend.predictions.type.ModelType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModelInfoResponse {

    private String modelName;
    private ModelType modelType;
    private String taskType;

    private String target;
    private List<String> features;
    private Float trainSize;
    private Boolean useScaled;
    private Integer randomState;

    private Map<String, Object> hyperparameters;

    private Map<String, Double> trainMetrics;
    private Map<String, Double> testMetrics;

    private List<String> classNames;
    private Integer numberOfClasses;
    private List<List<Integer>> confusionMatrix;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer trainingDataSize;
    private Integer testDataSize;
    private Double trainingTime;
    private List<String> categoricalCols;
}
