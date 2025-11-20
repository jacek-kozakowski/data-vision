package com.datavision.backend.project.controller;

import com.datavision.backend.common.dto.project.CreateProjectDto;
import com.datavision.backend.common.dto.project.ProjectDto;
import com.datavision.backend.common.dto.response.ApiResponse;
import com.datavision.backend.project.service.ProjectService;
import com.datavision.backend.user.model.User;
import com.datavision.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ProjectDto> getProjectById(@RequestParam Long id){
        User user = getUser();
        ProjectDto response = projectService.getProjectDtoById(id, user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody CreateProjectDto createProjectDto){
        User user = getUser();
        ProjectDto response = projectService.createProject(createProjectDto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteProjectById(@RequestParam Long id){
        User user = getUser();
        projectService.deleteProjectById(id, user);
        return ResponseEntity.ok(new ApiResponse("Project deleted successfully"));
    }

    private User getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userService.getUserByUsername(username);
    }
}
