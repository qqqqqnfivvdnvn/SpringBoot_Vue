package com.example.demo.service.impl;

import com.example.demo.dto.ApiResponseDTO;
import com.example.demo.entity.Projects;
import com.example.demo.entity.UserProjectRelation;
import com.example.demo.entity.Users;
import com.example.demo.mapper.ProjectsMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserProjectRelationMapper;
import com.example.demo.service.ProjectsService;
import com.example.demo.vo.ProjectWithPermissionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectsServiceImpl implements ProjectsService {

    @Autowired
    private ProjectsMapper projectsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProjectRelationMapper userProjectRelationMapper;

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
            // 项目创建后自动添加创建者的管理权限
            UserProjectRelation relation = new UserProjectRelation();
            relation.setUserId(project.getUserId());
            relation.setProjectId(project.getId());
            relation.setPermissionType("admin");
            relation.setCreatorId(project.getUserId());
            userProjectRelationMapper.insert(relation);

            return ApiResponseDTO.success(project);
        } else {
            return ApiResponseDTO.error("项目添加失败！");
        }
    }

    @Override
    public ApiResponseDTO<List<Projects>> getAllProjects() {
        return ApiResponseDTO.success(projectsMapper.findAllProjects());
    }

    @Override
    public ApiResponseDTO<List<ProjectWithPermissionVO>> getProjectsForUser(String userId) {
        try {
            Users user = userMapper.selectById(userId);
            if (user == null) {
                return ApiResponseDTO.error("用户不存在");
            }

            List<ProjectWithPermissionVO> result;

            if ("admin".equals(user.getRole())) {
                // 管理员可以看到所有项目
                List<Projects> allProjects = projectsMapper.findAllProjects();
                result = allProjects.stream().map(p -> {
                    ProjectWithPermissionVO vo = convertToVO(p);
                    vo.setPermissionType("admin");
                    return vo;
                }).collect(Collectors.toList());
            } else {
                // 普通用户只能看到有权限的项目
                List<UserProjectRelation> relations = userProjectRelationMapper.findByUserId(userId);
                Set<String> projectIds = relations.stream()
                    .map(UserProjectRelation::getProjectId)
                    .collect(Collectors.toSet());

                // 加上自己创建的项目
                List<Projects> ownedProjects = projectsMapper.findByUserId(userId);
                for (Projects p : ownedProjects) {
                    projectIds.add(p.getId());
                }

                result = new ArrayList<>();
                for (String projectId : projectIds) {
                    Projects project = projectsMapper.selectById(projectId);
                    if (project != null) {
                        ProjectWithPermissionVO vo = convertToVO(project);
                        // 检查是否是创建者
                        if (projectId.equals(project.getUserId())) {
                            vo.setPermissionType("owner");
                        } else {
                            UserProjectRelation relation = userProjectRelationMapper.findByUserAndProject(userId, projectId);
                            vo.setPermissionType(relation != null ? relation.getPermissionType() : "write");
                        }
                        result.add(vo);
                    }
                }
            }

            return ApiResponseDTO.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponseDTO.error("获取项目失败：" + e.getMessage());
        }
    }

    private ProjectWithPermissionVO convertToVO(Projects project) {
        ProjectWithPermissionVO vo = new ProjectWithPermissionVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setDescription(project.getDescription());
        vo.setIcon(project.getIcon());
        vo.setColor(project.getColor());
        vo.setRouteName(project.getRouteName());
        vo.setAddtime(project.getAddtime());
        vo.setUserId(project.getUserId());
        vo.setUserName(project.getUserName());
        return vo;
    }

}
