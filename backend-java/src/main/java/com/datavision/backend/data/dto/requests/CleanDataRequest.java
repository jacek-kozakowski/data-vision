package com.datavision.backend.data.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CleanDataRequest {
    @NotNull(message = "fillNa cannot be null")
    private Boolean fillNa = false;

    @Pattern(regexp = "mean|median", message = "fillMethod must be mean or median")
    private String fillMethod = "mean";

    @NotNull(message = "scale cannot be null")
    private Boolean scale = false;

    public Boolean getFillNa() {
        return fillNa != null ? fillNa : false;
    }

    public String getFillMethod() {
        return fillMethod != null ? fillMethod : "mean";
    }

    public Boolean getScale() {
        return scale != null ? scale : false;
    }
}
