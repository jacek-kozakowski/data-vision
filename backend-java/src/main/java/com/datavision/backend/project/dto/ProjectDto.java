package com.datavision.backend.project.dto;

import com.datavision.backend.project.model.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ProjectDto {

    private Long id;
    private String name;
    private Map<String, String> datasets;
    private Map<Integer, String> plots;
    private Map<String, String> models;
    private Map<String, String> pipelines;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.name = project.getProjectName();
        this.datasets = project.getDatasets();
        this.plots = project.getPlots();
        this.models = project.getModels();
        this.pipelines = project.getPipelines();
    }
}
