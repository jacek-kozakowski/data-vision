package com.datavision.backend.project.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateProjectDto {
    @NotBlank(message = "Project name cannot be empty")
    @Size(min= 3, max= 100)
    private String name;
}
