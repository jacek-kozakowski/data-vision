package com.datavision.backend.project.model;

import com.datavision.backend.common.dto.project.CreateProjectDto;
import com.datavision.backend.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "projects")
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_datasets", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyColumn(name = "dataset_name", length = 100)
    @Column(name = "minio_url", length = 1024)
    private Map<String, String> datasets = new HashMap<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "project_plots", joinColumns = @JoinColumn(name = "project_id"))
    @MapKeyColumn(name = "plots_index")
    @Column(name = "minio_path", length = 1024)
    private Map<Integer, String> plots = new HashMap<>();

    public Project(CreateProjectDto dto, User user) {
        this.projectName = dto.getName();
        this.user = user;
    }

}
