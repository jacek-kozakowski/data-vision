package com.datavision.backend.common.dto.project;

import com.datavision.backend.project.model.Project;

import java.util.HashMap;
import java.util.Map;

public class ProjectDto {

    private Long id;
    private String name;
    private Map<String, String> datasets;
    private Map<Integer, String> plots;

    public ProjectDto(Project project) {
        this.id = project.getId();
        this.name = project.getProjectName();
        this.datasets = project.getDatasets();
        this.plots = project.getPlots();
    }
}
