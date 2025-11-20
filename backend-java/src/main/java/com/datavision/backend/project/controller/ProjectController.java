package com.datavision.backend.project.controller;

import com.datavision.backend.auth.AuthUtils;
import com.datavision.backend.common.dto.project.CreateProjectDto;
import com.datavision.backend.common.dto.project.ProjectDto;
import com.datavision.backend.common.dto.response.ApiResponse;
import com.datavision.backend.project.service.ProjectService;
import com.datavision.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<ProjectDto> getProjectById(@RequestParam Long id){
        ProjectDto response = projectService.getProjectDtoById(id, AuthUtils.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectDto createProjectDto){
        ProjectDto response = projectService.createProject(createProjectDto, AuthUtils.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteProjectById(@RequestParam Long id){
        projectService.deleteProjectById(id, AuthUtils.getUser());
        return ResponseEntity.ok(new ApiResponse("Project deleted successfully"));
    }
}
