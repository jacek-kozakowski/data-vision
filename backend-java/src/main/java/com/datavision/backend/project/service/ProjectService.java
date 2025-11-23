package com.datavision.backend.project.service;

import com.datavision.backend.common.dto.project.CreateProjectDto;
import com.datavision.backend.common.dto.project.ProjectDto;
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
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectDto createProject(CreateProjectDto createProjectDto, User user){
        log.info("Creating project for user: {}", user.getUsername());
        if (createProjectDto == null){
            throw new EmptyRequestException("Cant create project without data");
        }
        Project project = new Project(createProjectDto, user);
        Project savedProject = projectRepository.save(project);
        log.debug("Project created successfully");
        return new ProjectDto(savedProject);
    }

    @Transactional
    public void deleteProjectById(Long id, User user){
        log.info("Deleting project with id: {}", id);
        Project project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user) ){
            throw new UserNotOwnerException("User is not owner of the project");
        }
        projectRepository.delete(project);
        log.debug("Project deleted successfully");
    }

    public ProjectDto getProjectDtoById(Long id, User user){
        log.info("Fetching project with id: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user) ){
            throw new UserNotOwnerException("User is not owner of the project");
        }
        log.debug("Project fetched successfully");
        return new ProjectDto(project);
    }

    public List<ProjectDto> getAllProjects(User user){
        log.info("Fetching all projects for user {}", user.getUsername());
        List<Project> projects = projectRepository.findAllByUserId(user.getId());
        return projects.stream().map(ProjectDto::new).toList();
    }

    @Transactional
    public void addDatasetToProject(Long projectId, String datasetName, String filePath, User user){
        log.info("Adding dataset {} to project {}", datasetName, projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user) ){
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<String, String> datasets = project.getDatasets();
        datasets.put(datasetName, filePath);
        project.setDatasets(datasets);
        projectRepository.save(project);
        log.debug("Dataset {} added to project {}", datasetName, projectId);
    }

    @Transactional
    public void addPlotToProject(Long projectId,Integer plotId, String filePath, User user){
        log.info("Adding dataset to project {}",  projectId);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Invalid project id."));
        if (!isProjectOwner(project, user)){
            throw new UserNotOwnerException("User is not owner of the project");
        }
        Map<Integer, String> plots = project.getPlots();
        plots.put(plotId, filePath);
        project.setPlots(plots);
        projectRepository.save(project);
        log.debug("Plot added to project {}", projectId);
    }

    private boolean isProjectOwner(Project project, User user){
        return project.getUser().getId().equals(user.getId());
    }
}
