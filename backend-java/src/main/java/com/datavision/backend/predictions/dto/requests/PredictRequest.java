package com.datavision.backend.predictions.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PredictRequest {

    @NotNull(message = "Data cannot be null")
    @NotEmpty(message = "Data cannot be empty - provide at least one")
    private List<Map<String, Object>> data;

    @JsonProperty("return_probabilities")
    private Boolean returnProbabilities = false;

    public Boolean getReturnProbabilities() {
        return returnProbabilities != null ? returnProbabilities : false;
    }
}
