package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;

public interface ProjectsService {

    public ApiResponseDTO<Projects> addProject(Projects project);

}


