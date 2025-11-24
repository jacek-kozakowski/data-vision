package com.datavision.backend.common.dto.data.requests;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class CleanDataRequest {
    private Boolean fillNa = false;

    private String fillMethod = "mean";

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

