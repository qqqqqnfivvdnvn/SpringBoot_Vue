package com.example.demo.controller;
import java.util.List;
import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;
import com.example.demo.service.ProjectsService;
import com.example.demo.vo.ProjectWithPermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/projects")
public class ProjectsController {

    @Autowired
    private ProjectsService projectsService;




    
    @PostMapping("/addproject")
    public ResponseEntity<ApiResponseDTO<Projects>> addProject(@RequestBody Projects project){
        System.out.println(project);
        ApiResponseDTO<Projects> response = projectsService.addProject(project);
        return ResponseEntity.ok(response);

    }

    /**
     * 获取当前用户有权限的项目列表（带权限信息）
     */
    @GetMapping("/getmyprojects")
    public ResponseEntity<ApiResponseDTO<List<ProjectWithPermissionVO>>> getMyProjects(@RequestParam String userId) {
        ApiResponseDTO<List<ProjectWithPermissionVO>> response = projectsService.getProjectsForUser(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有项目（管理员专用）
     */
    @GetMapping("/getallprojects")
    public ResponseEntity<ApiResponseDTO<List<Projects>>> getAllProjects() {
        ApiResponseDTO<List<Projects>> response = projectsService.getAllProjects();
        return ResponseEntity.ok(response);
    }

}
