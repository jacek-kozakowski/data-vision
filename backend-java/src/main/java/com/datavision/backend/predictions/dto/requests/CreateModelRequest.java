package com.datavision.backend.predictions.dto.requests;

import com.datavision.backend.predictions.type.ModelType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateModelRequest {

    @NotBlank(message = "Model name cannot be empty")
    @Size(min=3, max=100)
    private String modelName;

    @NotNull(message = "Model cannot be null")
    private ModelType modelType;

    @NotBlank(message = "Target column cannot be empty")
    private String target;

    private List<String> features;

    @NotNull
    @DecimalMin(value = "0.1", message = "Train size must be at least 0.1")
    @DecimalMax(value = "0.9", message = "Train size must be at most 0.9")
    private Float trainSize;

    private Boolean useScaled = false;

    private Integer randomState = 42;


    public Boolean getUseScaled() {
        return useScaled != null ? useScaled : false;
    }

    public Integer getRandomState() {
        return randomState != null ? randomState : 42;
    }

}
