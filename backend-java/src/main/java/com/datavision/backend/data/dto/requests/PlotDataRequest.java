package com.datavision.backend.data.dto.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlotDataRequest {

    @NotNull
    private Integer plotId;
    @NotBlank
    private String plotType;
    @NotBlank
    private String column1;
    @NotBlank
    private String column2;

    private Boolean useScaled = false;

    public Boolean getUseScaled() {
        return useScaled != null ? useScaled : false;
    }
}
