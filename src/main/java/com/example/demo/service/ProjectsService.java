package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;
import com.example.demo.vo.ProjectWithPermissionVO;

import java.util.List;

public interface ProjectsService {

    public ApiResponseDTO<Projects> addProject(Projects project);

    public ApiResponseDTO<List<Projects>> getAllProjects();

    public ApiResponseDTO<List<ProjectWithPermissionVO>> getProjectsForUser(String userId);

}


