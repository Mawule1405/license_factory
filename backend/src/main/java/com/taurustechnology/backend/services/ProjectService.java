package com.taurustechnology.backend.services;

import com.taurustechnology.backend.dtos.ProjectMiniStats;
import com.taurustechnology.backend.dtos.requests.ProjectRequest;
import com.taurustechnology.backend.dtos.responses.ProjectResponse;
import com.taurustechnology.backend.models.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    Project createProject(ProjectRequest project, String username);
    Project getProject(String id   );
    Page<Project> getProjects(String key, int page, int size);
    void deleteProject(String id, String username);
    void updateProject(String id, Project project, String username);


    ProjectMiniStats getProjectStats();
}
