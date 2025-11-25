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
    void addPlotToProject(Long projectId,Integer plotId, String filePath, User user);
}
