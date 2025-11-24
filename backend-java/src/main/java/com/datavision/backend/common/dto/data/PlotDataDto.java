package com.datavision.backend.common.dto.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlotDataDto {
    private String file_id;
    private String plot_type;
    private String column1;
    private String column2;
}
