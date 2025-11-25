package com.datavision.backend.project.controller;

import com.datavision.backend.auth.AuthUtilsService;
import com.datavision.backend.project.dto.CreateProjectDto;
import com.datavision.backend.project.dto.ProjectDto;
import com.datavision.backend.common.response.ApiResponse;
import com.datavision.backend.project.service.IProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;
    private final AuthUtilsService authUtils;

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id){
        ProjectDto response = projectService.getProjectDtoById(id, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getUserProjects(){
        List<ProjectDto> projects = projectService.getAllProjects(authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(projects);
    }
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid CreateProjectDto createProjectDto){
        ProjectDto response = projectService.createProject(createProjectDto, authUtils.getAuthenticatedUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProjectById(@PathVariable Long id){
        projectService.deleteProjectById(id, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(new ApiResponse("Project deleted successfully"));
    }
}
