package com.example.demo.service;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;
import com.example.demo.mapper.ProjectsMapper;
import com.example.demo.service.impl.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectsServiceImpl implements ProjectsService {

    @Autowired
    private ProjectsMapper projectsMapper;


    @Override
    public ApiResponseDTO<Projects> addProject(Projects project) {

        int projectCount = projectsMapper.findProject(project.getName());

        if (projectCount > 0 ) {
            return ApiResponseDTO.error("项目名称已存在！");
        }
        System.out.println(project);

        if (project.getName() == null || project.getName().trim().isEmpty() ) {
            return ApiResponseDTO.error("项目名称不能为空！");
        }

        // 注册成功
        UUID uuid = UUID.randomUUID();
        project.setId(uuid.toString().replace("-", ""));
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        // 格式化当前时间
        String formattedTime = now.format(formatter);
        project.setAddtime(formattedTime);

        int i = projectsMapper.addProject(project);
        if (i > 0) {
            return ApiResponseDTO.success(project);
        } else {
            return ApiResponseDTO.error("项目添加失败！");
        }
    }

    @Override
    public ApiResponseDTO<List<Projects>> getAllProjects() {
        return ApiResponseDTO.success(projectsMapper.findAllProjects());
    }


}
