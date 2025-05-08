package com.example.demo.controller;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;
import com.example.demo.service.impl.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/projects")
public class ProjectsController {

    @Autowired
    private ProjectsService projectsService;


    @PostMapping("/addProject")
    public ResponseEntity<ApiResponseDTO<Projects>> addProject(@RequestBody Projects project){
        System.out.println(project);
        ApiResponseDTO<Projects> response = projectsService.addProject(project);
        return ResponseEntity.ok(response);

    }


}
