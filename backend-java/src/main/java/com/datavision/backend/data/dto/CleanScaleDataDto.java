package com.datavision.backend.data.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CleanScaleDataDto {
    private String file_id;
    private boolean fill_na;
    private String fill_method;
    private boolean scale;

    public CleanScaleDataDto(String dataset, boolean fill_na, String fill_method, boolean scale) {
        this.file_id = dataset;
        this.fill_na = fill_na;
        this.fill_method = fill_method;
        this.scale = scale;
    }
}
