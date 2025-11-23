package com.datavision.backend.project.controller;

import com.datavision.backend.auth.AuthUtilsService;
import com.datavision.backend.common.dto.project.CreateProjectDto;
import com.datavision.backend.common.dto.project.ProjectDto;
import com.datavision.backend.common.dto.response.ApiResponse;
import com.datavision.backend.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AuthUtilsService authUtils;

    @GetMapping
    public ResponseEntity<ProjectDto> getProjectById(@RequestParam Long id){
        ProjectDto response = projectService.getProjectDtoById(id, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDto>> getProjects(){
        List<ProjectDto> projects = projectService.getAllProjects(authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(projects);
    }
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectDto createProjectDto){
        ProjectDto response = projectService.createProject(createProjectDto, authUtils.getAuthenticatedUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteProjectById(@RequestParam Long id){
        projectService.deleteProjectById(id, authUtils.getAuthenticatedUser());
        return ResponseEntity.ok(new ApiResponse("Project deleted successfully"));
    }
}
