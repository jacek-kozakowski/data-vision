package com.datavision.backend.predictions.dto.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PredictionResponse {

    private List<Object> predictions;

    private List<List<Double>> probabilities;

    private List<String> classNames;

    private String taskName;

    private Integer predictionCount;
}
