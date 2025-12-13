package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;

import java.util.List;

public interface ProjectsService {

    public ApiResponseDTO<Projects> addProject(Projects project);

    public ApiResponseDTO<List<Projects>> getAllProjects();

}


