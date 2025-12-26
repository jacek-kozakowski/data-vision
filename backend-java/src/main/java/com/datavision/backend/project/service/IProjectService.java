package com.datavision.backend.project.service;

import com.datavision.backend.project.dto.CreateProjectDto;
import com.datavision.backend.project.dto.ProjectDto;
import com.datavision.backend.user.model.User;

import java.util.List;

public interface IProjectService {
    ProjectDto createProject(CreateProjectDto createProjectDto, User user);

    void deleteProjectById(Long id, User user);

    ProjectDto getProjectDtoById(Long id, User user);

    List<ProjectDto> getAllProjects(User user);

    void addDatasetToProject(Long projectId, String datasetName, String filePath, User user);

    void addPlotToProject(Long projectId, Integer plotId, String filePath, User user);

    void addModelToProject(Long projectId, String modelName, String filePath, User user);

    void removeModelFromProject(Long projectId, String modelName, User user);

    void addPipelineToProject(Long projectId, String pipelineName, String pipelineId, User user);

    void removePipelineFromProject(Long projectId, String pipelineName, User user);
}
