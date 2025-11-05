package com.datavision.backend.common.dto.data;

import com.datavision.backend.user.model.User;
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

    public CleanScaleDataDto(User user, boolean fill_na, String fill_method, boolean scale) {
        this.file_id = user.getCurrentFile();
        this.fill_na = fill_na;
        this.fill_method = fill_method;
        this.scale = scale;
    }
}
