package com.datavision.backend.project.service;

import com.datavision.backend.project.dto.CreateProjectDto;
import com.datavision.backend.project.dto.ProjectDto;
import com.datavision.backend.common.exceptions.EmptyRequestException;
import com.datavision.backend.common.exceptions.ProjectNotFoundException;
import com.datavision.backend.common.exceptions.UserNotOwnerException;
import com.datavision.backend.project.model.Project;
import com.datavision.backend.project.repository.ProjectRepository;
import com.datavision.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService implements IProjectService{

    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public ProjectDto createProject(CreateProjectDto createProjectDto, User user){
        log.info("Creating project for user: {}", user.getUsername());
        if (createProjectDto == null){
            log.warn("Empty request body");
            throw new EmptyRequestException("Cant create project without data");
        }
        Project project = new Project(createProjectDto, user);
        Project savedProject = projectRepository.save(project);
        log.debug("Project created successfully");
        return new ProjectDto(savedProject);
    }

    @Override
    @Transactional
    public void deleteProjectById(Long id, User user){
        log.info("Deleting project with id: {}", id);
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user) ){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        projectRepository.delete(project);
        log.debug("Project deleted successfully");
    }

    @Override
    public ProjectDto getProjectDtoById(Long id, User user){
        log.info("Fetching project with id: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user) ){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        log.debug("Project fetched successfully");
        return new ProjectDto(project);
    }

    @Override
    public List<ProjectDto> getAllProjects(User user){
        log.info("Fetching all projects for user {}", user.getUsername());
        List<Project> projects = projectRepository.findAllByUserId(user.getId());
        return projects.stream().map(ProjectDto::new).toList();
    }

    @Override
    @Transactional
    public void addDatasetToProject(Long projectId, String datasetName, String filePath, User user){
        log.info("Adding dataset {} to project {}", datasetName, projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user) ){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<String, String> datasets = project.getDatasets();
        datasets.put(datasetName, filePath);
        project.setDatasets(datasets);
        projectRepository.save(project);
        log.debug("Dataset {} added to project {}", datasetName, projectId);
    }

    @Override
    @Transactional
    public void addPlotToProject(Long projectId,Integer plotId, String filePath, User user){
        log.info("Adding dataset to project {}",  projectId);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user)){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<Integer, String> plots = project.getPlots();
        plots.put(plotId, filePath);
        project.setPlots(plots);
        projectRepository.save(project);
        log.debug("Plot added to project {}", projectId);
    }

    @Override
    @Transactional
    public void addModelToProject(Long projectId, String modelName, String filePath, User user){
        log.info("Adding model {} to project {}", modelName, projectId);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user)){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<String, String> models = project.getModels();
        models.put(modelName, filePath);
        project.setModels(models);
        projectRepository.save(project);
        log.debug("Model {} added to project {}", modelName, projectId);
    }

    @Override
    @Transactional
    public void removeModelFromProject(Long projectId, String modelName, User user){
        log.info("Removing model {} from project {}", modelName, projectId);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user)){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<String, String> models = project.getModels();
        models.remove(modelName);
        project.setModels(models);
        projectRepository.save(project);
        log.debug("Model {} removed from project {}", modelName, projectId);
    }

    @Override
    @Transactional
    public void addPipelineToProject(Long projectId, String pipelineName, String filePath, User user) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user)){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<String, String> pipelines = project.getPipelines();
        pipelines.put(pipelineName, filePath);
        project.setPipelines(pipelines);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void removePipelineFromProject(Long projectId, String pipelineName, User user) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user)){
            log.warn("User is not owner of the project");
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<String, String> pipelines = project.getPipelines();
        pipelines.remove(pipelineName);
        project.setPipelines(pipelines);
        projectRepository.save(project);
    }

    private boolean isProjectOwner(Project project, User user){
        return project.getUser().getId().equals(user.getId());
    }
}
